package com.astadevs.sympathyapp.helper

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.location.LocationServices
import java.io.IOException
import java.util.*

class UserLocation(
    private val mContext: Context,
    private val mOnLocationSetListener: OnLocationSetListener
) : GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private lateinit var alertDialog: AlertDialog
    private var isGPSEnabled = false
    private var isNetworkEnabled = false
    private var mGoogleApiClient: GoogleApiClient? = null
    override fun onConnectionFailed(arg0: ConnectionResult) {
        mGoogleApiClient!!.connect()
    }

    override fun onConnected(arg0: Bundle?) {
        findUserLocation()
    }

    override fun onConnectionSuspended(arg0: Int) {
        mGoogleApiClient!!.connect()
    }

    fun checkLocation() {
        mOnLocationSetListener.onLoadStart()
        val locationManager = mContext.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        handleLocationAccess()
    }

    private fun handleLocationAccess() {
        if (!isGPSEnabled && !isNetworkEnabled) {
            useLastSavedLocation()
        } else {
            buildGoogleApiClient()
        }
    }

    @Synchronized
    private fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(mContext).addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this).addApi(LocationServices.API).build()
        mGoogleApiClient!!.connect()
    }

    private fun findUserLocation() {
        val mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient!!)
        if (mCurrentLocation != null) {
            val latitude = mCurrentLocation.latitude
            val longitude = mCurrentLocation.longitude
            val geoCoder = Geocoder(mContext, Locale.getDefault())
            try {
                val addressList = geoCoder.getFromLocation(latitude, longitude, 1)
                if (addressList != null && addressList.size > 0) {
                    val address = addressList[0]
                    val country = address.countryName
                    val city = address.locality
                    val subAddress = address.subLocality
                    mOnLocationSetListener.onLocationSet(
                        cityName = city,
                        countryName = country,
                        subLocality = subAddress,
                        latitude = latitude,
                        longitude = longitude
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: NullPointerException) {
                e.printStackTrace()
            }
        } else {
            useLastSavedLocation()
        }
    }

    private fun useLastSavedLocation() {
        Log.i("Location", "Manual")
        val city = AppSettings.selectedCity
        if (city != "") {
            val latitude = AppSettings.selectedLat
            val longitude = AppSettings.selectedLong
            val country = AppSettings.selectedCountry
            val subAddress = AppSettings.selectedSubAddress
            mOnLocationSetListener.onLocationSet(
                cityName = city,
                countryName = country,
                subLocality = subAddress,
                latitude = latitude.toDouble(),
                longitude = longitude.toDouble()
            )
        } else {
            providerAlertMessage()
        }
    }

    private fun providerAlertMessage() {
        dismissDialog()
        val builder = AlertDialog.Builder(mContext)
        builder.setTitle(mContext.resources.getString(R.string.unable_to_find_location))
        builder.setMessage(mContext.resources.getString(R.string.enable_provider))
        builder.setCancelable(false)
        builder.setPositiveButton(mContext.resources.getString(R.string.settings)) { _, _ ->
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            mContext.startActivity(settingsIntent)
        }
        builder.setNegativeButton(mContext.resources.getString(R.string.cancel)) { _, _ -> dismissDialog() }
        alertDialog = builder.create()
        alertDialog.show()
    }

    private fun dismissDialog() {
        alertDialog.dismiss()
    }

    interface OnLocationSetListener {
        fun onLoadStart()
        fun onLocationSet(
            cityName: String,
            countryName: String,
            subLocality: String,
            latitude: Double,
            longitude: Double
        )
    }
}
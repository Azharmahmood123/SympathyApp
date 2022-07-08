package com.astadevs.sympathyapp.ui.maps

import android.Manifest
import com.astadevs.sympathyapp.App
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.ChatWithUserInfo
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.data.model.UserObject
import com.astadevs.sympathyapp.databinding.FragmentMapsBinding
import com.astadevs.sympathyapp.helper.*
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import com.astadevs.sympathyapp.ui.organization.OrganizationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class MapsFragment : BaseFragment<FragmentMapsBinding>(
    FragmentMapsBinding::inflate
), OnMapReadyCallback, UserLocation.OnLocationSetListener, GoogleMap.OnMarkerClickListener {

    companion object {
        private val permissions_all = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    var onOrganizationSelected: (OrganizationObject) -> Unit = {}
    var onChatSelected: (ChatWithUserInfo) -> Unit = {}

    private val organizationViewModel: OrganizationViewModel by viewModel()
    private val mapsViewModel: MapsViewModel by viewModel()

    private val currentPositionSnippet = "currentPosition"
    private lateinit var userLocation: UserLocation
    private lateinit var mMap: GoogleMap
    private var mCurrLocationMarker: Marker? = null
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private val allMarkers: List<Marker> = ArrayList()
    private var organizationList: List<OrganizationObject> = ArrayList()
    private var organizationUserID = ""


    override fun initializeControls() {

        (activity as DashboardActivity).setSelectedNav(2)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        userLocation = UserLocation(mActivity, this)
    }

    override fun observeViewModel() {
        organizationViewModel.organizationList.observe(viewLifecycleOwner) { result ->
            if (result is Resource.Success && result.data != null) {
                this.organizationList = result.data
                addOrganizationMarkers(organizationList)
            }
        }

        mapsViewModel.responseAvailableChat.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog?.show()
                }
                is Resource.Error -> {
                    progressDialog?.dismiss()
                    val message = it.message
                    showToast(message)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    val chatObject = it.data
                    if (chatObject != null) {
                        val chatWithUser =
                            ChatWithUserInfo(chatObject, UserObject(uid = organizationUserID))
                        onChatSelected.invoke(chatWithUser)
                    } else {
                        mapsViewModel.updateNewChat(AppSettings.userUID, organizationUserID)
                    }
                }
            }
        }

        mapsViewModel.responseNewChat.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog?.show()
                }
                is Resource.Error -> {
                    progressDialog?.dismiss()
                    val message = it.message
                    showToast(message)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    val chatObject = it.data
                    if (chatObject != null) {
                        val chatWithUser =
                            ChatWithUserInfo(chatObject, UserObject(uid = organizationUserID))
                        onChatSelected.invoke(chatWithUser)
                    }
                }
            }
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isMyLocationButtonEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.isCompassEnabled = true
        mMap.uiSettings.isMapToolbarEnabled = true
        mMap.setOnMarkerClickListener(this)

        checkLocationPermission()
        organizationViewModel.fetchOrganizations()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val snippet = marker.snippet
        if (snippet != currentPositionSnippet) {
            val organization = organizationList.find {
                snippet == it.title
            }
            if (organization != null) {
                showMarkerOpenDialog(organization)
            }
            return true
        }
        return false
    }

    override fun onLoadStart() {

    }

    override fun onLocationSet(
        cityName: String,
        countryName: String,
        subLocality: String,
        latitude: Double,
        longitude: Double
    ) {
        this.latitude = latitude
        this.longitude = longitude

        mCurrLocationMarker?.remove()

        val latLng = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
        markerOptions.draggable(false)
        markerOptions.position(latLng)
        markerOptions.title("Current Position")
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
        markerOptions.snippet(currentPositionSnippet)
        mCurrLocationMarker = mMap.addMarker(markerOptions)

        //move map camera
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        mMap.animateCamera(CameraUpdateFactory.zoomTo(13f))
    }

    private fun showMarkerOpenDialog(organization: OrganizationObject) {
        val showUserOrganizationDialog = ShowUserOrganizationDialog()
        showUserOrganizationDialog.organziation = organization
        showUserOrganizationDialog.onStartChat = {
            organizationUserID = it.userID
            mapsViewModel.checkChatAvailable(AppSettings.userUID, it.userID)
        }
        showUserOrganizationDialog.onOrganizationInfo = { onOrganizationSelected.invoke(it) }
        showUserOrganizationDialog.show(requireActivity().supportFragmentManager, "show_info")
    }

    private fun checkLocationPermission() {
        Dexter.withContext(requireContext())
            .withPermissions(*permissions_all)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    when {
                        multiplePermissionsReport.areAllPermissionsGranted() -> {
                            userLocation.checkLocation()
                        }
                        multiplePermissionsReport.isAnyPermissionPermanentlyDenied -> {
                            showPermissionPermanentlyDeniedAlert(requireContext())
                        }
                        else -> {
                            showPermissionNotGrantedAlert(requireContext())
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    list: List<PermissionRequest>,
                    permissionToken: PermissionToken
                ) {
                    permissionToken.continuePermissionRequest()
                }
            })
            .withErrorListener { dexterError: DexterError ->
                showToast(dexterError.toString())
            }
            .check()
    }

    private fun addOrganizationMarkers(organizationList: List<OrganizationObject>) {
        organizationList.forEach {
            val orgLat = it.latitude
            val orgLong = it.longitude
            if (orgLat.isNotBlank() && orgLong.isNotBlank()) {
                addCustomMarker(it.title, orgLat, orgLong)
            }
        }
    }

    private fun addCustomMarker(orgTitle: String, lat: String, lng: String) {
        val latLng = LatLng(lat.toDouble(), lng.toDouble())
        val markerOptions = MarkerOptions()
        markerOptions.draggable(false)
        markerOptions.position(latLng)
        markerOptions.icon(
            BitmapDescriptorFactory.fromBitmap(
                BitmapUtils.getMarkerBitmapFromView(
                    App.self,
                    R.drawable.account
                )
            )
        )
        markerOptions.snippet(orgTitle)
        val mLocationMarker = mMap.addMarker(markerOptions)
        if (mLocationMarker != null) {
            allMarkers.toMutableList().add(mLocationMarker)
        }
    }

}
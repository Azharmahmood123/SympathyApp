package com.astadevs.sympathyapp.ui.organization

import android.Manifest
import android.view.View
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseActivity
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.ActivityOrganizationInfoBinding
import com.astadevs.sympathyapp.helper.*
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.DexterError
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrganizationInfoActivity : BaseActivity<ActivityOrganizationInfoBinding>(
    ActivityOrganizationInfoBinding::inflate
), UserLocation.OnLocationSetListener {

    private val viewModel: OrganizationViewModel by viewModel()
    private lateinit var userLocation: UserLocation


    companion object {
        private val permissions_all = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    private var latitude: Double = 0.0
    private var longitude: Double = 0.0

    override fun initializeControls() {
        userLocation = UserLocation(this, this)
        checkLocationPermission()
    }

    override fun attachListeners() {
        binding.tvContinue.setOnClickListener(this@OrganizationInfoActivity)
        viewModel.responseOrganization.observe(this) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog?.show()
                }
                is Resource.Error -> {
                    progressDialog?.dismiss()
                    showToast(it.message)
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    showToast("Organization Created Successfully")
                    startSpecificActivity(DashboardActivity::class.java)
                    finish()
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (view == binding.tvContinue) {
            val etTitle = binding.etTitle.text.toString().trim()
            if (etTitle.isEmpty()) {
                binding.etTitle.error = getString(R.string.error_enter_title)
                binding.etTitle.requestFocus()
                return
            }
            if (latitude == 0.0 || longitude == 0.0) {
                showToast("Please wait for location to update.")
                return
            }
            val etBio = binding.etBio.text.toString().trim()

            val organization = OrganizationObject(
                userID = AppSettings.userUID,
                title = etTitle,
                bio = etBio,
                latitude = latitude.toString(),
                longitude = longitude.toString()
            )
            viewModel.addOrganizationDetail(organization)
        } else
            super.onClick(view)
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
        binding.tvLat.text = latitude.toString()
        binding.tvLong.text = longitude.toString()
    }

    private fun checkLocationPermission() {
        Dexter.withContext(this)
            .withPermissions(*permissions_all)
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(multiplePermissionsReport: MultiplePermissionsReport) {
                    when {
                        multiplePermissionsReport.areAllPermissionsGranted() -> {
                            userLocation.checkLocation()
                        }
                        multiplePermissionsReport.isAnyPermissionPermanentlyDenied -> {
                            showPermissionPermanentlyDeniedAlert(this@OrganizationInfoActivity)
                        }
                        else -> {
                            showPermissionNotGrantedAlert(this@OrganizationInfoActivity)
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

}
package com.astadevs.sympathyapp.ui.profile

import android.view.View
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.databinding.FragmentProfileBinding
import com.astadevs.sympathyapp.helper.hide
import com.astadevs.sympathyapp.helper.show
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import com.astadevs.sympathyapp.ui.login.LoginActivity
import com.astadevs.sympathyapp.ui.organization.OrganizationViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class ProfileFragment : BaseFragment<FragmentProfileBinding>(
    FragmentProfileBinding::inflate
) {

    var onEditOrganizationClick: (OrganizationObject) -> Unit = {}

    private val viewModel: ProfileViewModel by viewModel()
    private val organizationViewModel: OrganizationViewModel by viewModel()

    lateinit var organizationObject: OrganizationObject

    override fun initializeControls() {
        (activity as DashboardActivity).setSelectedNav(4)
    }

    override fun attachListeners() {
        binding.btnLogout.setOnClickListener(this)
        binding.btnEditOrganizationBio.setOnClickListener(this)
    }

    override fun observeViewModel() {
        viewModel.isLogout.observe(viewLifecycleOwner) {
            if (it) {
                (activity as DashboardActivity).startSpecificActivity(LoginActivity::class.java)
                AppSettings.userUID = ""
                AppSettings.userEmail = ""
                AppSettings.organizationID = ""
                (activity as DashboardActivity).finish()
            }
        }

        viewModel.responseUserObject.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                val userObject = it.data
                if (userObject != null) {
                    binding.layoutUserProfile.show()
                    binding.tvEmail.text = userObject.email
                    if (AppSettings.isOrganization()) {
                        binding.tvFbr.text = userObject.fbrNo
                        binding.layoutFBR.show()
                    } else {
                        binding.layoutFBR.hide()
                    }
                }
            }
        }

        organizationViewModel.responseOrganizationDetail.observe(viewLifecycleOwner) {
            if (it is Resource.Success) {
                val organizationObject = it.data
                if (organizationObject != null) {
                    this.organizationObject = organizationObject;
                    binding.layoutOrganizationProfile.show()
                    binding.tvTitle.text = organizationObject.title
                    binding.tvBio.text = organizationObject.bio
                }
            }
        }
    }

    override fun loadData() {
        viewModel.fetchUserDetail(AppSettings.userUID)
        if (AppSettings.isOrganization()) {
            organizationViewModel.fetchOrganizationDetail(AppSettings.userUID, "")
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnLogout -> {
                viewModel.logoutUser()
            }
            binding.btnEditOrganizationBio -> {
                onEditOrganizationClick.invoke(organizationObject)
            }
        }
    }

}
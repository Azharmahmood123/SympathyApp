package com.astadevs.sympathyapp.ui.profile

import android.view.View
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.databinding.FragmentEditOrganizationInfoBinding
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.helper.toEditable
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditOrganizationFragment : BaseFragment<FragmentEditOrganizationInfoBinding>(
    FragmentEditOrganizationInfoBinding::inflate
) {

    var onQuitEditOrganizationFragment: () -> Unit = {}

    private val viewModel: ProfileViewModel by viewModel()

    lateinit var organizationObject: OrganizationObject

    override fun initializeControls() {
        binding.etTitle.text = organizationObject.title.toEditable()
        binding.etBio.text = organizationObject.bio.toEditable()
    }

    override fun attachListeners() {
        binding.tvUpdate.setOnClickListener(this)
        binding.ivCloseEditOrganization.setOnClickListener(this)
    }

    override fun observeViewModel() {
        viewModel.responseOrganization.observe(viewLifecycleOwner) {
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
                    val updated = it.data
                    if (updated == true) {
                        showToast("Updated successfully")
                        onQuitEditOrganizationFragment.invoke()
                    }
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.tvUpdate -> {
                updateOrganization()
            }
            binding.ivCloseEditOrganization -> {
                onQuitEditOrganizationFragment.invoke()
            }
        }
    }

    private fun updateOrganization() {
        val etTitle = binding.etTitle.text.toString().trim()
        if (etTitle.isEmpty()) {
            binding.etTitle.error = getString(R.string.error_enter_title)
            binding.etTitle.requestFocus()
            return
        }
        val etBio = binding.etBio.text.toString().trim()

        val organization = OrganizationObject(
            userID = AppSettings.userUID,
            organizationID = organizationObject.organizationID,
            title = etTitle,
            bio = etBio,
            latitude = organizationObject.latitude,
            longitude = organizationObject.longitude
        )
        viewModel.updateOrganizationDetail(organizationObject.organizationID, organization)
    }
}
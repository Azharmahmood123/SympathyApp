package com.astadevs.sympathyapp.ui.organization

import android.content.Intent
import android.net.Uri
import android.view.View
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.databinding.FragmentOrganationDetailBinding
import com.astadevs.sympathyapp.data.model.OrganizationObject

class OrganizationDetailFragment : BaseFragment<FragmentOrganationDetailBinding>(
    FragmentOrganationDetailBinding::inflate
) {

    var onQuitOrganizationDetailFragment: () -> Unit = {}

    lateinit var organizationObject: OrganizationObject

    override fun initializeControls() {
        binding.tvOrganizationTitle.text = organizationObject.title
        binding.tvOrganizationBio.text = organizationObject.bio
    }

    override fun attachListeners() {
        binding.btnFindCoordinate.setOnClickListener(this)
        binding.ivCloseOrganizationDetail.setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnFindCoordinate -> {
                val uri =
                    "http://maps.google.com/maps?daddr=" + organizationObject.latitude + "," + organizationObject.longitude + " (" + organizationObject.title + ")"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                mActivity.startActivity(intent)
            }
            binding.ivCloseOrganizationDetail -> {
                onQuitOrganizationDetailFragment.invoke()
            }
            else -> super.onClick(view)
        }
    }
}
package com.astadevs.sympathyapp.ui.campaigns

import android.content.Intent
import android.net.Uri
import android.view.View
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.FragmentCampaignsPasscodeBinding
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.data.model.CampaignDonationObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CampaignPasscodeFragment : BaseFragment<FragmentCampaignsPasscodeBinding>(
    FragmentCampaignsPasscodeBinding::inflate
) {

    var onQuitCampaignPasscodeFragment: () -> Unit = {}

    private val campaignsViewModel: CampaignsViewModel by viewModel()

    lateinit var donation: CampaignDonationObject

    override fun initializeControls() {
        val amount = donation.requestAmount + " Rs."
        binding.tvCampaignAmount.text = amount

        binding.tvCampaignPasscode.text = donation.requestPasscode
    }

    override fun attachListeners() {
        binding.ivCloseCampaignPasscode.setOnClickListener(this)
        binding.ivGoToLocation.setOnClickListener(this)
    }

    override fun observeViewModel() {
        campaignsViewModel.campaignsDetail.observe(viewLifecycleOwner) {
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
                    val campaignObject = it.data
                    if (campaignObject != null) {
                        binding.tvCampaignTitle.text = campaignObject.campaignTitle
                    }
                }
            }
        }
    }

    override fun loadData() {
        val campaignID = donation.campaignID
        campaignsViewModel.fetchCampaignDetail(campaignID)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.ivCloseCampaignPasscode -> {
                onQuitCampaignPasscodeFragment.invoke()
            }
            binding.ivGoToLocation -> {
                val uri =
                    "http://maps.google.com/maps?daddr=" + donation.organizationLat + "," + donation.organizationLong + " (NGO)"
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
                intent.setPackage("com.google.android.apps.maps")
                mActivity.startActivity(intent)
            }
        }
    }
}
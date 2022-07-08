package com.astadevs.sympathyapp.ui.campaigns

import android.view.View
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.FragmentCampaignsDetailBinding
import com.astadevs.sympathyapp.helper.hide
import com.astadevs.sympathyapp.helper.show
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.data.model.CampaignObject
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.ui.organization.OrganizationViewModel
import com.squareup.picasso.Picasso
import org.koin.androidx.viewmodel.ext.android.viewModel

class CampaignDetailFragment : BaseFragment<FragmentCampaignsDetailBinding>(
    FragmentCampaignsDetailBinding::inflate
) {

    var onQuitCampaignDetailFragment: () -> Unit = {}
    var onCampaignSelected: (CampaignObject, OrganizationObject) -> Unit = { _, _ -> }
    var onOrganizationSelected: (OrganizationObject) -> Unit = {}
    var onViewDonationListFragment: (CampaignObject) -> Unit = {}

    private val organizationViewModel: OrganizationViewModel by viewModel()

    lateinit var campaign: CampaignObject
    lateinit var organization: OrganizationObject

    override fun initializeControls() {
        binding.tvCampaignTitle.text = campaign.campaignTitle
        binding.tvCampaignDescription.text = campaign.campaignDescription
        val amountRequired = campaign.campaignAmount + " Rs."
        binding.tvCampaignAmount.text = amountRequired

        Picasso.get()
            .load(campaign.campaignImageURL)
            .placeholder(R.drawable.stub_image)
            .into(binding.feedImage)

        if (AppSettings.isOrganization()) {
            binding.layoutUserButtonCampaign.hide()
            binding.layoutAaminButtonCampaign.show()
        } else {
            binding.layoutAaminButtonCampaign.hide()
            binding.layoutUserButtonCampaign.show()
        }
    }

    override fun attachListeners() {
        binding.ivCloseCampaignDetail.setOnClickListener(this)
        binding.btnDonateNow.setOnClickListener(this)
        binding.btnViewOrganization.setOnClickListener(this)
        binding.btnCancelCampaign.setOnClickListener(this)
        binding.btnViewDonations.setOnClickListener(this)

        organizationViewModel.responseOrganizationDetail.observe(viewLifecycleOwner) {
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
                    val organizationObject = it.data
                    if (organizationObject != null) {
                        this.organization = organizationObject
                    }
                }
            }
        }
    }

    override fun loadData() {
        organizationViewModel.fetchOrganizationDetail("", campaign.organizationID)
    }

    override fun onClick(view: View) {
        when (view) {
            binding.ivCloseCampaignDetail -> {
                onQuitCampaignDetailFragment.invoke()
            }
            binding.btnDonateNow -> {
                onCampaignSelected.invoke(campaign, organization)
            }
            binding.btnViewOrganization -> {
                onOrganizationSelected.invoke(organization)
            }
            binding.btnCancelCampaign -> {

            }
            binding.btnViewDonations -> {
                onViewDonationListFragment.invoke(campaign)
            }
        }
    }
}
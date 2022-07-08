package com.astadevs.sympathyapp.ui.campaigns

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.FragmentCampaignDonationListBinding
import com.astadevs.sympathyapp.helper.EqualSpacingItemDecoration
import com.astadevs.sympathyapp.helper.hide
import com.astadevs.sympathyapp.helper.show
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.data.model.CampaignDonationObject
import com.astadevs.sympathyapp.data.model.CampaignObject
import org.koin.androidx.viewmodel.ext.android.viewModel

class CampaignDonationListFragment : BaseFragment<FragmentCampaignDonationListBinding>(
    FragmentCampaignDonationListBinding::inflate
) {

    var onQuitCampaignDonationListFragment: () -> Unit = {}
    var onCampaignDonationObjectSelected: (CampaignDonationObject) -> Unit = {}

    private val viewModel: CampaignsViewModel by viewModel()
    private val donationAdapter: DonationAdapter = DonationAdapter()

    lateinit var campaign: CampaignObject

    override fun initializeControls() {

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = donationAdapter
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_twenty)
            addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        }
    }

    override fun attachListeners() {
        binding.ivCloseCampaignDonation.setOnClickListener { }
        donationAdapter.setOnItemClickListener { clickTag, model, _ ->
            when (clickTag) {
                "itemView" -> {
                    onCampaignDonationObjectSelected.invoke(model)
                }
            }
        }
    }

    override fun observeViewModel() {
        viewModel.donationList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog?.show()
                }
                is Resource.Error -> {
                    progressDialog?.dismiss()
                    val message = it.message
                    showToast(message)
                    binding.tvNoDonationFound.show()
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    val donationList = it.data
                    if (donationList != null && donationList.isNotEmpty()) {
                        binding.tvNoDonationFound.hide()
                        donationAdapter.submitList(donationList)
                    } else {
                        binding.tvNoDonationFound.show()
                    }
                }
            }
        }
    }

    override fun loadData() {
        if (AppSettings.isOrganization()) {
            viewModel.fetchDonationsForCampaign(campaignID = campaign.campaignID)
        } else {
            viewModel.fetchDonationsForCampaign(userID = AppSettings.userUID)
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.ivCloseCampaignDonation -> {
                onQuitCampaignDonationListFragment.invoke()
            }
        }
    }
}
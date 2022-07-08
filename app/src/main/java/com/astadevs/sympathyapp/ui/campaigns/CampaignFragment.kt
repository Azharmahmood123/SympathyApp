package com.astadevs.sympathyapp.ui.campaigns

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.FragmentCampaignsBinding
import com.astadevs.sympathyapp.helper.EqualSpacingItemDecoration
import com.astadevs.sympathyapp.helper.hide
import com.astadevs.sympathyapp.helper.show
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.data.model.CampaignObject
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class CampaignFragment : BaseFragment<FragmentCampaignsBinding>(
    FragmentCampaignsBinding::inflate
) {

    var onAddNewCampaign: () -> Unit = {}
    var onCampaignSelected: (CampaignObject) -> Unit = {}

    private val viewModel: CampaignsViewModel by viewModel()
    private val campaignsAdapter: CampaignsAdapter = CampaignsAdapter()

    override fun initializeControls() {

        (activity as DashboardActivity).setSelectedNav(1)

        if (!AppSettings.isOrganization()) {
            binding.btnAddNewCampaign.hide()
        }
        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = campaignsAdapter
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_twenty)
            addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        }
    }

    override fun attachListeners() {
        binding.btnAddNewCampaign.setOnClickListener { onAddNewCampaign.invoke() }
        campaignsAdapter.setOnItemClickListener { clickTag, model, _ ->
            when (clickTag) {
                "itemView" -> {
                    onCampaignSelected.invoke(model)
                }
            }
        }
    }

    override fun observeViewModel() {
        viewModel.campaignsList.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Loading -> {
                    progressDialog?.show()
                }
                is Resource.Error -> {
                    progressDialog?.dismiss()
                    val message = it.message
                    showToast(message)
                    binding.tvNoCampaignsFound.show()
                }
                is Resource.Success -> {
                    progressDialog?.dismiss()
                    val campaignsList = it.data
                    if (campaignsList != null && campaignsList.isNotEmpty()) {
                        binding.tvNoCampaignsFound.hide()
                        campaignsAdapter.submitList(campaignsList)
                    } else {
                        binding.tvNoCampaignsFound.show()
                    }
                }
            }
        }
    }

    override fun loadData() {
        if (AppSettings.isOrganization()) {
            val organizationID = AppSettings.organizationID
            viewModel.fetchOrganizationCampaigns(organizationID)
        } else {
            viewModel.fetchUserCampaigns()
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnAddNewCampaign -> {
                onAddNewCampaign.invoke()
            }
        }
    }
}
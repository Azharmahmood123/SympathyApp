package com.astadevs.sympathyapp.ui.organization

import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.FragmentOrganizationBinding
import com.astadevs.sympathyapp.helper.EqualSpacingItemDecoration
import com.astadevs.sympathyapp.helper.showToast
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrganizationFragment : BaseFragment<FragmentOrganizationBinding>(
    FragmentOrganizationBinding::inflate
) {

    var onOrganizationSelected: (OrganizationObject) -> Unit = {}
    var onViewDonationListFragment: () -> Unit = {}

    private val viewModel: OrganizationViewModel by viewModel()
    private lateinit var organizationAdapter: OrganizationAdapter

    override fun initializeControls() {

        (activity as DashboardActivity).setSelectedNav(0)

        organizationAdapter = OrganizationAdapter()

        with(binding.recyclerView) {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = organizationAdapter
            val spacingInPixels = resources.getDimensionPixelSize(R.dimen.margin_ten)
            addItemDecoration(EqualSpacingItemDecoration(spacingInPixels))
        }

        ViewCompat.setNestedScrollingEnabled(binding.recyclerView, false)
    }

    override fun attachListeners() {
        binding.btnViewDonations.setOnClickListener(this)
        organizationAdapter.setOnItemClickListener { clickTag, model, _ ->
            when (clickTag) {
                "itemView" -> {
                    onOrganizationSelected.invoke(model)
                }
            }
        }
    }

    override fun observeViewModel() {
        viewModel.organizationList.observe(this) {
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
                    val organizationList = it.data
                    organizationAdapter.submitList(organizationList)
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnViewDonations -> {
                onViewDonationListFragment.invoke()
            }
        }
    }

    override fun loadData() {
        viewModel.fetchOrganizations()
    }
}
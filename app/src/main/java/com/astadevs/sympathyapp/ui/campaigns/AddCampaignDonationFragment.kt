package com.astadevs.sympathyapp.ui.campaigns

import android.view.View
import android.widget.RadioButton
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseFragment
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.data.model.CampaignDonationObject
import com.astadevs.sympathyapp.data.model.CampaignObject
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.databinding.FragmentAddCampaignsDonationBinding
import com.astadevs.sympathyapp.helper.getRandomString
import com.astadevs.sympathyapp.helper.hide
import com.astadevs.sympathyapp.helper.show
import com.astadevs.sympathyapp.helper.showToast
import org.koin.androidx.viewmodel.ext.android.viewModel

class AddCampaignDonationFragment : BaseFragment<FragmentAddCampaignsDonationBinding>(
    FragmentAddCampaignsDonationBinding::inflate
) {

    var onQuitCampaignDonationFragment: () -> Unit = {}
    var onDonations: (CampaignDonationObject) -> Unit = {}

    private val viewModel: CampaignsViewModel by viewModel()


    lateinit var campaignObject: CampaignObject
    lateinit var organization: OrganizationObject

    private var donationType: String = ""
    private var selfDonationType: String = ""

    fun setData(campaign: CampaignObject, organization: OrganizationObject) {
        this.campaignObject = campaign
        this.organization = organization
    }


    override fun initializeControls() {
        binding.rbBankAccount.isChecked = true
        donationType = DONATE_BANK
        binding.layoutBankAccount.show()
        binding.layoutJazzCash.hide()
        binding.rgSelfOptions.hide()
    }

    override fun attachListeners() {
        binding.ivCloseCampaignDonation.setOnClickListener(this)
        binding.btnAddDonation.setOnClickListener(this)

        binding.rgDonationType.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<RadioButton>(checkedId)
            when (rb.id) {
                R.id.rb_bank_account -> {
                    donationType = DONATE_BANK
                    binding.layoutBankAccount.show()
                    binding.layoutJazzCash.hide()
                    binding.rgSelfOptions.hide()
                    binding.btnAddDonation.text = "Donate"
                }
                R.id.rb_jazz_cash -> {
                    donationType = DONATE_JAZZ_CASH
                    binding.layoutBankAccount.hide()
                    binding.layoutJazzCash.show()
                    binding.rgSelfOptions.hide()
                    binding.btnAddDonation.text = "Donate"
                }
                R.id.rb_self -> {
                    donationType = DONATE_SELF
                    binding.layoutBankAccount.hide()
                    binding.layoutJazzCash.hide()
                    binding.rgSelfOptions.show()
                    binding.rbPickFromLocation.isChecked = true
                    selfDonationType = DONATE_SELF_PICK_UP
                    binding.btnAddDonation.text = "Generate Donation Code"
                }
            }
        }

        binding.rgSelfOptions.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<RadioButton>(checkedId)
            when (rb.id) {
                R.id.rb_pick_from_location -> {
                    selfDonationType = DONATE_SELF_PICK_UP
                }
                R.id.rb_donate_by_hand -> {
                    selfDonationType = DONATE_SELF_BY_HAND
                }
            }
        }
    }

    override fun observeViewModel() {
        viewModel.addDonationRequest.observe(viewLifecycleOwner) {
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
                    val requestObject = it.data
                    if (requestObject != null) {
                        showToast("Donation make successfully.")
                        if (donationType == DONATE_SELF) {
                            onDonations.invoke(requestObject)
                        } else {
                            onQuitCampaignDonationFragment.invoke()
                        }
                    }
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.ivCloseCampaignDonation -> {
                onQuitCampaignDonationFragment.invoke()
            }
            binding.btnAddDonation -> {

                val amount = binding.etAmountDonated.text.toString().trim()
                if (amount.isEmpty()) {
                    showToast("Please enter amount to donate")
                    return
                }

                val passcode = getRandomString(12)
                val requestObject: CampaignDonationObject
                if (donationType == DONATE_SELF) {
                    requestObject = CampaignDonationObject(
                        organizationID = campaignObject.organizationID,
                        organizationLat = organization.latitude,
                        organizationLong = organization.longitude,
                        campaignID = campaignObject.campaignID,
                        userID = AppSettings.userUID,
                        requestType = donationType,
                        requestSelfType = selfDonationType,
                        requestAmount = amount,
                        requestPasscode = passcode
                    )
                } else {
                    requestObject = CampaignDonationObject(
                        organizationID = campaignObject.organizationID,
                        organizationLat = organization.latitude,
                        organizationLong = organization.longitude,
                        campaignID = campaignObject.campaignID,
                        userID = AppSettings.userUID,
                        requestType = donationType,
                        requestAmount = amount
                    )
                }
                viewModel.addCampaignDonation(requestObject)
            }
        }
    }

    companion object {
        private const val DONATE_BANK = "Bank"
        private const val DONATE_JAZZ_CASH = "Jazz Cash"
        private const val DONATE_SELF = "Self"

        private const val DONATE_SELF_PICK_UP = "Pick up"
        private const val DONATE_SELF_BY_HAND = "Donate by Hand"
    }
}
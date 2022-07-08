package com.astadevs.sympathyapp.ui.dashboard

import android.view.View
import com.astadevs.sympathyapp.App
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseActivity
import com.astadevs.sympathyapp.data.model.CampaignDonationObject
import com.astadevs.sympathyapp.data.model.CampaignObject
import com.astadevs.sympathyapp.data.model.ChatWithUserInfo
import com.astadevs.sympathyapp.data.model.OrganizationObject
import com.astadevs.sympathyapp.databinding.ActivityDashboardBinding
import com.astadevs.sympathyapp.helper.*
import com.astadevs.sympathyapp.ui.campaigns.*
import com.astadevs.sympathyapp.ui.chat.ChatFragment
import com.astadevs.sympathyapp.ui.chats.ChatsFragment
import com.astadevs.sympathyapp.ui.maps.MapsFragment
import com.astadevs.sympathyapp.ui.organization.OrganizationDetailFragment
import com.astadevs.sympathyapp.ui.organization.OrganizationFragment
import com.astadevs.sympathyapp.ui.profile.EditOrganizationFragment
import com.astadevs.sympathyapp.ui.profile.ProfileFragment

class DashboardActivity : BaseActivity<ActivityDashboardBinding>(
    ActivityDashboardBinding::inflate
) {

    companion object {
        private const val FRAGMENT_CAMPAIGN = "fragment_campaign"
        private const val FRAGMENT_MAP = "fragment_map"
        private const val FRAGMENT_ORGANIZATION = "fragment_organization"
        private const val FRAGMENT_CHATS = "fragment_chats"
        private const val FRAGMENT_PROFILE = "fragment_profile"
    }

    private var currentFragment: String = ""

    override fun initializeControls() {
        if (AppSettings.isOrganization()) {
            binding.layoutDashboardOrganizations.hide()
            binding.layoutDashboardMap.hide()
            showCampaignsFragment()
        } else {
            showOrganizationFragment()
        }
    }

    override fun attachListeners() {
        binding.layoutDashboardOrganizations.setOnClickListener(this)
        binding.layoutDashboardMap.setOnClickListener(this)
        binding.layoutDashboardCampaigns.setOnClickListener(this)
        binding.layoutDashboardChats.setOnClickListener(this)
        binding.layoutDashboardProfile.setOnClickListener(this)

        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            if (fragment is CampaignFragment
                || fragment is MapsFragment
                || fragment is ChatsFragment
                || fragment is ProfileFragment
                || fragment is OrganizationFragment
            ) {
                showNavbar()
            } else {
                hideNavbar()
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            val fragment = getCurrentTopFragment(this@DashboardActivity)
            if (fragment != null) {
                if (fragment is CampaignFragment
                    || fragment is MapsFragment
                    || fragment is ChatsFragment
                    || fragment is ProfileFragment
                    || fragment is OrganizationFragment
                ) {
                    showNavbar()
                } else {
                    hideNavbar()
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.layoutDashboardCampaigns -> {
                if (currentFragment != FRAGMENT_CAMPAIGN) {
                    showCampaignsFragment()
                }
            }
            binding.layoutDashboardMap -> {
                if (currentFragment != FRAGMENT_MAP) {
                    showMapsFragment()
                }
            }
            binding.layoutDashboardOrganizations -> {
                if (currentFragment != FRAGMENT_ORGANIZATION) {
                    showOrganizationFragment()
                }
            }
            binding.layoutDashboardChats -> {
                if (currentFragment != FRAGMENT_CHATS) {
                    showChatsFragment()
                }
            }
            binding.layoutDashboardProfile -> {
                if (currentFragment != FRAGMENT_PROFILE) {
                    showProfileFragment()
                }
            }
        }
    }

    override fun onBackPressed() {
        val fragment = getCurrentTopFragment(this@DashboardActivity)
        if (fragment != null) {
            if (AppSettings.isOrganization()) {
                if (fragment is CampaignFragment) {
                    back()
                } else {
                    showCampaignsFragment()
                }
            } else {
                if (fragment is OrganizationFragment) {
                    back()
                } else {
                    showOrganizationFragment()
                }
            }
        } else {
            back()
        }
    }

    private fun unselectAllNav() {
        with(binding) {
            ivDashboardCampaigns.setImageTint(App.self, R.color.black)
            tvDashboardCampaigns.setTextColor(App.self, R.color.black)

            ivDashboardMap.setImageTint(App.self, R.color.black)
            tvDashboardMap.setTextColor(App.self, R.color.black)

            ivDashboardChats.setImageTint(App.self, R.color.black)
            tvDashboardChats.setTextColor(App.self, R.color.black)

            ivDashboardProfile.setImageTint(App.self, R.color.black)
            tvDashboardProfile.setTextColor(App.self, R.color.black)
        }
    }

    private val organizationFragment by lazy {
        val f = OrganizationFragment()
        f.onOrganizationSelected = { showOrganizationDetailFragment(it) }
        f.onViewDonationListFragment = { showCampaignDonationListFragment(CampaignObject()) }
        f
    }

    private fun showOrganizationFragment() {
        replaceFragment(organizationFragment)
    }

    private fun getOrganizationFragment(organization: OrganizationObject): OrganizationDetailFragment {
        val f = OrganizationDetailFragment()
        f.onQuitOrganizationDetailFragment = { back() }
        f.organizationObject = organization
        return f
    }

    private fun showOrganizationDetailFragment(organization: OrganizationObject) {
        val fragment = getOrganizationFragment(organization = organization)
        addFragment(fragment)
    }

    private val mapsFragment by lazy {
        val f = MapsFragment()
        f.onOrganizationSelected = { showOrganizationDetailFragment(it) }
        f.onChatSelected = {
            showChatFragment(it)
        }
        f
    }

    private fun showMapsFragment() {
        replaceFragment(mapsFragment)
    }

    private val campaignsFragment by lazy {
        val f = CampaignFragment()
        f.onCampaignSelected = { showCampaignDetailFragment(it) }
        f.onAddNewCampaign = { showNewCampaignFragment() }
        f
    }

    private fun showCampaignsFragment() {
        replaceFragment(campaignsFragment)
    }

    private fun getNewCampaignFragment(): AddCampaignFragment {
        val f = AddCampaignFragment()
        f.onQuitAddCampaignFragment = { back() }
        return f
    }

    private fun showNewCampaignFragment() {
        val fragment = getNewCampaignFragment()
        addFragment(fragment)
    }

    private fun getCampaignDetailFragment(campaign: CampaignObject): CampaignDetailFragment {
        val f = CampaignDetailFragment()
        f.onQuitCampaignDetailFragment = { back() }
        f.onCampaignSelected = { campaignObject, organizationObject ->
            showCampaignDonationFragment(
                campaign = campaignObject,
                organization = organizationObject
            )
        }
        f.onOrganizationSelected = { showOrganizationDetailFragment(it) }
        f.onViewDonationListFragment = { showCampaignDonationListFragment(it) }
        f.campaign = campaign
        return f
    }

    private fun showCampaignDetailFragment(campaign: CampaignObject) {
        val fragment = getCampaignDetailFragment(campaign = campaign)
        addFragment(fragment)
    }

    private fun getCampaignDonationFragment(
        campaign: CampaignObject,
        organization: OrganizationObject
    ): AddCampaignDonationFragment {
        val f = AddCampaignDonationFragment()
        f.onQuitCampaignDonationFragment = { back() }
        f.onDonations = { showCampaignPasscodeFragment(it) }
        f.setData(campaign = campaign, organization = organization)
        return f
    }

    private fun showCampaignDonationFragment(
        campaign: CampaignObject,
        organization: OrganizationObject
    ) {
        hideNavbar()
        val fragment = getCampaignDonationFragment(campaign = campaign, organization = organization)
        addFragment(fragment)
    }

    private fun getCampaignPasscodeFragment(donation: CampaignDonationObject): CampaignPasscodeFragment {
        val f = CampaignPasscodeFragment()
        f.onQuitCampaignPasscodeFragment = { back() }
        f.donation = donation
        return f
    }

    private fun showCampaignPasscodeFragment(donation: CampaignDonationObject) {
        val fragment = getCampaignPasscodeFragment(donation = donation)
        addFragment(fragment)
    }

    private fun getCampaignDonationListFragment(campaign: CampaignObject): CampaignDonationListFragment {
        val f = CampaignDonationListFragment()
        f.onQuitCampaignDonationListFragment = { back() }
        f.onCampaignDonationObjectSelected = { showCampaignPasscodeFragment(it) }
        f.campaign = campaign
        return f
    }

    private fun showCampaignDonationListFragment(campaign: CampaignObject) {
        val fragment = getCampaignDonationListFragment(campaign = campaign)
        addFragment(fragment)
    }

    private val chatsFragment by lazy {
        val f = ChatsFragment()
        f.onChatWithUserInfo = {
            showChatFragment(it)
        }
        f
    }

    private fun showChatsFragment() {
        replaceFragment(chatsFragment)
    }

    private fun getChatFragment(chatUser: ChatWithUserInfo): ChatFragment {
        val f = ChatFragment()
        f.onQuitChatFragment = { back() }
        f.userID = AppSettings.userUID
        f.otherUserID = chatUser.mUserInfo.uid
        f.chatID = convertTwoUserIDs(AppSettings.userUID, chatUser.mUserInfo.uid)
        return f
    }

    private fun showChatFragment(chatUser: ChatWithUserInfo) {
        val fragment = getChatFragment(chatUser)
        addFragment(fragment)
    }

    private val profileFragment by lazy {
        val f = ProfileFragment()
        f.onEditOrganizationClick = {
            showEditOrganizationFragment(it)
        }
        f
    }

    private fun showProfileFragment() {
        replaceFragment(profileFragment)
    }

    private fun getEditOrganizationFragment(organizationObject: OrganizationObject): EditOrganizationFragment {
        val f = EditOrganizationFragment()
        f.onQuitEditOrganizationFragment = { back() }
        f.organizationObject = organizationObject
        return f
    }

    private fun showEditOrganizationFragment(organizationObject: OrganizationObject) {
        val fragment = getEditOrganizationFragment(organizationObject = organizationObject)
        addFragment(fragment)
    }

    private fun hideNavbar() {
        binding.layoutBottomNavigation.hide()
    }

    private fun showNavbar() {
        binding.layoutBottomNavigation.show()
    }

    fun setSelectedNav(case: Int) {
        when (case) {
            0 -> {
                currentFragment = FRAGMENT_ORGANIZATION
                unselectAllNav()
            }
            1 -> {
                currentFragment = FRAGMENT_CAMPAIGN
                unselectAllNav()
                setSelectedNav(binding.ivDashboardCampaigns, binding.tvDashboardCampaigns)
            }
            2 -> {
                currentFragment = FRAGMENT_MAP
                unselectAllNav()
                setSelectedNav(binding.ivDashboardMap, binding.tvDashboardMap)
            }
            3 -> {
                currentFragment = FRAGMENT_CHATS
                unselectAllNav()
                setSelectedNav(binding.ivDashboardChats, binding.tvDashboardChats)
            }
            4 -> {
                currentFragment = FRAGMENT_PROFILE
                unselectAllNav()
                setSelectedNav(binding.ivDashboardProfile, binding.tvDashboardProfile)
            }
        }
    }
}
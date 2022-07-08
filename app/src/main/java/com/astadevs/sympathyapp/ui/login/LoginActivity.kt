package com.astadevs.sympathyapp.ui.login

import android.util.Patterns
import android.view.View
import android.widget.RadioButton
import com.astadevs.sympathyapp.AppSettings
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.base.BaseActivity
import com.astadevs.sympathyapp.base.Resource
import com.astadevs.sympathyapp.databinding.ActivityLoginBinding
import com.astadevs.sympathyapp.helper.*
import com.astadevs.sympathyapp.ui.dashboard.DashboardActivity
import com.astadevs.sympathyapp.ui.organization.OrganizationInfoActivity
import com.astadevs.sympathyapp.ui.organization.OrganizationViewModel
import com.google.android.material.tabs.TabLayout
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : BaseActivity<ActivityLoginBinding>(
    ActivityLoginBinding::inflate
) {

    lateinit var selectedTab: String
    private val loginViewModel: LoginViewModel by viewModel()
    private val organizationViewModel: OrganizationViewModel by viewModel()

    private var userType: String = TYPE_ORGANIZATION

    companion object {
        private const val TYPE_ORGANIZATION = "organization"
        private const val TYPE_DONOR = "donor"
    }

    override fun initializeControls() {
        selectedTab = getString(R.string.txt_login)
        binding.layoutSignIn.show()
        binding.layoutSignUp.hide()
        userType = TYPE_DONOR
    }

    override fun attachListeners() {
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                val tabText = tab?.text as String
                if (tabText == selectedTab) {
                    return
                }
                emptyEveryField()
                if (tabText == getString(R.string.txt_login)) {
                    selectedTab = getString(R.string.txt_login)
                    binding.layoutSignIn.show()
                    binding.layoutSignUp.hide()
                } else {
                    selectedTab = getString(R.string.txt_sign_up)
                    binding.rbDonor.isChecked = true
                    userType = TYPE_DONOR
                    binding.etFbrNoSignUp.hide()
                    binding.viewFbrNo.hide()
                    binding.layoutSignIn.hide()
                    binding.layoutSignUp.show()
                }
                hideKeyboard()
                binding.btnLoginText.text = selectedTab
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }
        })
        binding.ivEyeSignIn.setOnClickListener(this@LoginActivity)
        binding.ivEyeSignUp.setOnClickListener(this@LoginActivity)
        binding.btnLoginSignUp.setOnClickListener(this@LoginActivity)
        binding.ivFb.setOnClickListener(this@LoginActivity)
        binding.ivGoogle.setOnClickListener(this@LoginActivity)
        binding.radioGroupSignUp.setOnCheckedChangeListener { group, checkedId ->
            val rb = group.findViewById<RadioButton>(checkedId)
            when (rb.id) {
                R.id.rb_donor -> {
                    userType = TYPE_DONOR
                    binding.etFbrNoSignUp.hide()
                    binding.viewFbrNo.hide()
                }
                else -> {
                    TYPE_ORGANIZATION
                    binding.etFbrNoSignUp.show()
                    binding.viewFbrNo.show()
                }
            }
        }
    }

    override fun observeViewModel() {
        loginViewModel.responseUser.observe(this) {
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
                    val firebaseUser = it.data
                    if (firebaseUser != null) {
                        AppSettings.userUID = firebaseUser.uid
                        if (selectedTab == getString(R.string.txt_login)) {
                            loginViewModel.fetchUserInfo(firebaseUser.uid)
                        } else {
                            if (userType == TYPE_DONOR) {
                                startSpecificActivity(DashboardActivity::class.java)
                            } else {
                                startSpecificActivity(OrganizationInfoActivity::class.java)
                            }
                            finish()
                        }
                    } else {
                        showToast("User not Found")
                    }
                }
            }
        }
        loginViewModel.userInfo.observe(this) {
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
                    val userObject = it.data
                    if (userObject != null) {
                        if (userObject.userType == TYPE_ORGANIZATION) {
                            organizationViewModel.fetchOrganizationDetail(
                                userID = AppSettings.userUID,
                                organizationID = ""
                            )
                        } else {
                            startSpecificActivity(DashboardActivity::class.java)
                            finish()
                        }
                    }
                }
            }
        }
        organizationViewModel.responseOrganizationDetail.observe(this) {
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
                        AppSettings.organizationID = organizationObject.organizationID
                        startSpecificActivity(DashboardActivity::class.java)
                    } else {
                        startSpecificActivity(OrganizationInfoActivity::class.java)
                    }
                    finish()
                }
            }
        }
    }

    override fun onClick(view: View) {
        if (view == binding.ivEyeSignIn) {
            binding.etPasswordSignIn.showHide()
        } else if (view == binding.ivEyeSignUp) {
            binding.etPasswordSignUp.showHide()
        } else if (view == binding.btnLoginSignUp) {
            hideKeyboard()
            if (selectedTab == getString(R.string.txt_login)) {
                checkLoginMethod()
            } else {
                checkSignUpMethod()
            }
        } else if (view == binding.ivFb) {
            showToast("FB")
        } else if (view == binding.ivGoogle) {
            showToast("Google")
        } else
            super.onClick(view)
    }

    private fun checkLoginMethod() {
        val email = binding.etEmailSignIn.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmailSignIn.error = getString(R.string.error_email_enter)
            binding.etEmailSignIn.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailSignIn.error = getString(R.string.error_email_correct)
            binding.etEmailSignIn.requestFocus()
            return
        }
        val password = binding.etPasswordSignIn.text.toString().trim()
        if (password.isEmpty()) {
            binding.etPasswordSignIn.error = getString(R.string.error_password)
            binding.etPasswordSignIn.requestFocus()
            return
        }
        loginViewModel.loginUserWithEmailAndPassword(email = email, password = password)
    }

    private fun checkSignUpMethod() {
        val email = binding.etEmailSignUp.text.toString().trim()
        if (email.isEmpty()) {
            binding.etEmailSignUp.error = getString(R.string.error_email_enter)
            binding.etEmailSignUp.requestFocus()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmailSignUp.error = getString(R.string.error_email_correct)
            binding.etEmailSignUp.requestFocus()
            return
        }
        val password = binding.etPasswordSignUp.text.toString().trim()
        if (password.isEmpty()) {
            binding.etPasswordSignUp.error = getString(R.string.error_password)
            binding.etPasswordSignUp.requestFocus()
            return
        }
        if (password.length < 6) {
            binding.etPasswordSignUp.error = getString(R.string.error_password_length)
            binding.etPasswordSignUp.requestFocus()
            return
        }
        val confirmPassword = binding.etConfirmPasswordSignUp.text.toString().trim()
        if (confirmPassword.isEmpty()) {
            binding.etConfirmPasswordSignUp.error = getString(R.string.error_password_confirm)
            binding.etConfirmPasswordSignUp.requestFocus()
            return
        }
        if (password != confirmPassword) {
            showToast(getString(R.string.error_password_not_same))
            return
        }
        var fbrNo = ""

        if (userType == TYPE_ORGANIZATION) {
            fbrNo = binding.etFbrNoSignUp.text.toString().trim()
            if (confirmPassword.isEmpty()) {
                binding.etFbrNoSignUp.error = getString(R.string.error_fbr_no)
                binding.etFbrNoSignUp.requestFocus()
                return
            }
        }

        loginViewModel.createUserWithEmailAndPassword(
            email = email,
            password = password,
            fbrNo = fbrNo,
            userType = userType
        )
    }

    private fun emptyEveryField() {
        binding.etEmailSignIn.setText("")
        binding.etPasswordSignIn.setText("")

        binding.etEmailSignUp.setText("")
        binding.etPasswordSignUp.setText("")
        binding.etConfirmPasswordSignUp.setText("")
        binding.etFbrNoSignUp.setText("")
    }
}
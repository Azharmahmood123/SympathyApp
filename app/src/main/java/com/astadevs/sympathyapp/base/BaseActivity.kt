package com.astadevs.sympathyapp.base

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.viewbinding.ViewBinding
import com.astadevs.sympathyapp.App
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.helper.DialogUtils
import com.astadevs.sympathyapp.helper.setImageTint
import com.astadevs.sympathyapp.helper.setTextColor


abstract class BaseActivity<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : AppCompatActivity(), View.OnClickListener {

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding as VB

    protected open fun initializeToolBar() {}

    protected abstract fun initializeControls()

    protected open fun attachListeners() {}

    protected open fun observeViewModel() {}

    var progressDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater.invoke(layoutInflater)
        if (_binding == null)
            throw IllegalArgumentException("Binding cannot be null")
        setContentView(_binding!!.root)
        progressDialog = DialogUtils.showProgress(this)
        initializeToolBar()
        initializeControls()
        attachListeners()
        observeViewModel()
    }

    override fun onClick(view: View) {

    }

    fun startSpecificActivity(otherActivityClass: Class<*>?) {
        val intent = Intent(this@BaseActivity, otherActivityClass)
        startActivity(intent)
    }

    open fun replaceFragment(
        fragment: Fragment,
        addToBackStack: Boolean = false,
        isAnimated: Boolean = false
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (isAnimated) transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.replace(R.id.container, fragment, fragment.javaClass.simpleName)
        transaction.commitAllowingStateLoss()
    }

    fun addFragment(
        fragment: Fragment,
        addToBackStack: Boolean = true,
        isAnimated: Boolean = true
    ) {
        val transaction = supportFragmentManager.beginTransaction()
        if (isAnimated) transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.add(R.id.container, fragment, fragment.tag)
        transaction.commitAllowingStateLoss()
    }

    fun back() {
        val mgr = supportFragmentManager
        if (mgr.backStackEntryCount > 1) {
            mgr.popBackStack()
        } else {
            finish()
        }
    }

    open fun getCurrentTopFragment(activity: FragmentActivity?): Fragment? {
        if (activity == null) return null
        val manager = activity.supportFragmentManager
        val fragmentList = manager.fragments
        if (fragmentList.size > 0) {
            for (i in fragmentList.size - 1 downTo -1 + 1) {
                val fragment = fragmentList[i]
                if (fragment != null && fragment.isVisible) return fragment
            }
        }
        return null
    }

    fun setSelectedNav(image: AppCompatImageView, textView: AppCompatTextView) {
        image.setImageTint(App.self, R.color.colorPrimary)
        textView.setTextColor(App.self, R.color.colorPrimary)
    }
}
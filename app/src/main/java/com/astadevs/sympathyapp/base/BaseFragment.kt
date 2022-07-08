package com.astadevs.sympathyapp.base

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewbinding.ViewBinding
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.helper.DialogUtils

abstract class BaseFragment<VB : ViewBinding>(
    private val bindingInflater: (inflater: LayoutInflater) -> VB
) : Fragment(), View.OnClickListener {

    lateinit var mActivity: FragmentActivity

    private var _binding: VB? = null
    protected val binding: VB
        get() = _binding as VB

    protected open fun getterSetterValue() {}
    protected open fun initializeToolBar() {}
    protected abstract fun initializeControls()
    protected open fun attachListeners() {}
    protected open fun observeViewModel() {}
    protected open fun loadData() {}

    var rootView: View? = null

    var progressDialog: Dialog? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as FragmentActivity
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        if (_binding == null)
            throw IllegalArgumentException("Binding cannot be null")
        return _binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressDialog = DialogUtils.showProgress(requireContext())
        rootView = view
        getterSetterValue()
        initializeToolBar()
        initializeControls()
        attachListeners()
        observeViewModel()
        loadData()
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onClick(view: View) {

    }
}
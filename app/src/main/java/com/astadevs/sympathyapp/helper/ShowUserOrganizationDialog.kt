package com.astadevs.sympathyapp.helper

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentActivity
import com.astadevs.sympathyapp.R
import com.astadevs.sympathyapp.data.model.OrganizationObject

class ShowUserOrganizationDialog : DialogFragment() {

    var onStartChat: (OrganizationObject) -> Unit = {}
    var onOrganizationInfo: (OrganizationObject) -> Unit = {}

    private lateinit var mActivity: FragmentActivity
    lateinit var organziation: OrganizationObject

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is Activity) {
            mActivity = context as FragmentActivity
        }
    }

    @SuppressLint("UseGetLayoutInflater")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(mActivity)
        val view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_show_marker_click, null)
        val tvName = view.findViewById<TextView>(R.id.tvName)
        val tvBio = view.findViewById<TextView>(R.id.tvBio)
        val btnStartChat = view.findViewById<Button>(R.id.btnStartChat)
        val btnInfo = view.findViewById<Button>(R.id.btnInfo)
        dialogBuilder.setView(view)

        tvName.text = organziation.title
        tvBio.text = organziation.bio

        btnStartChat.setOnClickListener {
            dialog!!.dismiss()
            onStartChat.invoke(organziation)
        }
        btnInfo.setOnClickListener {
            dialog!!.dismiss()
            onOrganizationInfo.invoke(organziation)
        }
        return dialogBuilder.create()
    }
}
package com.astadevs.sympathyapp.helper

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.view.WindowManager
import com.astadevs.sympathyapp.R

object DialogUtils {
    fun showProgress(context: Context): Dialog {
        val dialog = Dialog(context, R.style.FullScreenDialogStyle)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.dialog_progress)
        dialog.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT
        )
        return dialog
    }

    fun showAlertWithTwoButton(
        context: Context,
        message: String,
        title: String,
        buttonPositiveText: String,
        buttonNegativeText: String,
        dialogListener: OnDialogTwoButtonClickListener
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(
                buttonPositiveText
            ) { dialog, _ ->
                dialogListener.onDialogPositiveButtonClick()
                dialog.dismiss()
            }
            .setNegativeButton(
                buttonNegativeText
            ) { dialog, _ ->
                dialogListener.onDialogNegativeButtonClick()
                dialog.dismiss()
            }.show()
    }

    fun showAlertWithOneButton(
        context: Context,
        message: String,
        title: String,
        buttonPositiveText: String,
        dialogListener: OnDialogSingleButtonClickListener?
    ) {
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setCancelable(true)
            .setPositiveButton(
                buttonPositiveText
            ) { dialog, _ ->
                dialogListener?.onDialogPositiveButtonClick()
                dialog.dismiss()
            }.show()
    }

    interface OnDialogSingleButtonClickListener {
        fun onDialogPositiveButtonClick()
    }

    interface OnDialogTwoButtonClickListener {
        fun onDialogPositiveButtonClick()
        fun onDialogNegativeButtonClick()
    }
}
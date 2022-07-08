package com.astadevs.sympathyapp.helper

import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.fragment.app.Fragment
import com.astadevs.sympathyapp.ui.organization.OrganizationInfoActivity
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import java.util.*

fun View.show() {
    visibility = View.VISIBLE
}

fun View.hide() {
    visibility = View.GONE
}

fun EditText.showHide() {
    if (transformationMethod is PasswordTransformationMethod) {
        transformationMethod = null
    } else {
        transformationMethod = PasswordTransformationMethod()
    }
    setSelection(length())
}

inline fun <T : View> T.showIf(condition: (T) -> Boolean) {
    if (condition(this)) {
        show()
    } else {
        hide()
    }
}

inline fun <T : View> T.hideIf(condition: (T) -> Boolean) {
    if (condition(this)) {
        hide()
    } else {
        show()
    }
}

fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}

fun Fragment.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    requireContext().showToast(message, duration)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}

fun Context.hideKeyboard(view: View) {
    val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
}

fun ImageView.setImageTint(context: Context, @ColorRes colorId: Int) {
    val color = ContextCompat.getColor(context, colorId)
    val colorStateList = ColorStateList.valueOf(color)
    ImageViewCompat.setImageTintList(this, colorStateList)
}

fun TextView.setTextColor(context: Context, @ColorRes colorId: Int) {
    setTextColor(ContextCompat.getColor(context, colorId))
}

fun String.toEditable(): Editable =  Editable.Factory.getInstance().newEditable(this)

fun getRandomString(sizeOfPasswordString: Int): String {
    val allowedChars = "0123456789qwertyuiopasdfghjklzxcvbnm"
    val random = Random()
    val sb = StringBuilder(sizeOfPasswordString)
    for (i in 0 until sizeOfPasswordString) {
        sb.append(allowedChars[random.nextInt(allowedChars.length)])
    }
    return sb.toString()
}

fun showPermissionPermanentlyDeniedAlert(context: Context) {
    val message =
        "Since permission is permanently denied, Go to Application Setting, to enable Permissions."
    DialogUtils.showAlertWithOneButton(
        context = context,
        message = message,
        title = "Permission Denied Permanently",
        buttonPositiveText = "Ok",
        dialogListener = null
    )
}

fun showPermissionNotGrantedAlert(context: Context) {
    val message =
        "Since permissions are not granted you will not be able access current location"
    DialogUtils.showAlertWithOneButton(
        context = context,
        message = message,
        title = "Permission not granted",
        buttonPositiveText = "Ok",
        dialogListener = null
    )
}
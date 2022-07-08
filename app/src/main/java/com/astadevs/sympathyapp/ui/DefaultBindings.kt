package com.astadevs.sympathyapp.ui.splash

import android.annotation.SuppressLint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


@SuppressLint("SimpleDateFormat")
@BindingAdapter("bind_epochTimeMsToDate_with_days_ago")
fun TextView.bindEpochTimeMsToDateWithDaysAgo(epochTimeMs: Long) {
    val numOfDays = TimeUnit.MILLISECONDS.toDays(Date().time - epochTimeMs)

    this.text = when {
        numOfDays == 1.toLong() -> "Yesterday"
        numOfDays > 1.toLong() -> "$numOfDays days ago"
        else -> {
            val pat =
                SimpleDateFormat().toLocalizedPattern().replace("\\W?[YyMd]+\\W?".toRegex(), " ")
            val formatter = SimpleDateFormat(pat, Locale.getDefault())
            formatter.format(Date(epochTimeMs))
        }
    }
}

@SuppressLint("SimpleDateFormat")
@BindingAdapter("bind_epochTimeMsToDate")
fun TextView.bindEpochTimeMsToDate(epochTimeMs: Long) {
    if (epochTimeMs > 0) {
        val currentTimeMs = Date().time
        val numOfDays = TimeUnit.MILLISECONDS.toDays(currentTimeMs - epochTimeMs)

        val replacePattern = when {
            numOfDays >= 1.toLong() -> "Yy"
            else -> "YyMd"
        }
        val pat = SimpleDateFormat().toLocalizedPattern().replace("\\W?[$replacePattern]+\\W?".toRegex(), " ")
        val formatter = SimpleDateFormat(pat, Locale.getDefault())
        this.text = formatter.format(Date(epochTimeMs))
    }
}

@BindingAdapter("bind_disable_item_animator")
fun bindDisableRecyclerViewItemAnimator(recyclerView: RecyclerView, disable: Boolean) {
    if (disable) {
        recyclerView.itemAnimator = null
    }
}

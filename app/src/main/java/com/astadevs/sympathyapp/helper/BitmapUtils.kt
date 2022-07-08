package com.astadevs.sympathyapp.helper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.view.LayoutInflater
import com.astadevs.sympathyapp.R
import android.graphics.PorterDuff
import android.view.View
import android.widget.ImageView
import androidx.annotation.DrawableRes

object BitmapUtils {
    fun getMarkerBitmapFromView(context: Context, @DrawableRes resId: Int): Bitmap {
        val view =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_custom_marker,
                null
            )
        val mMarkerImageView = view.findViewById<View>(R.id.profile_image) as ImageView
        mMarkerImageView.setImageResource(resId)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = view.background
        drawable?.draw(canvas)
        view.draw(canvas)
        return returnedBitmap
    }

    fun getMarkerBitmapFromView(context: Context, bitmap: Bitmap?): Bitmap {
        val view =
            (context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater).inflate(
                R.layout.view_custom_marker,
                null
            )
        val mMarkerImageView = view.findViewById<View>(R.id.profile_image) as ImageView
        mMarkerImageView.setImageBitmap(bitmap)
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.buildDrawingCache()
        val returnedBitmap = Bitmap.createBitmap(
            view.measuredWidth, view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(returnedBitmap)
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN)
        val drawable = view.background
        drawable?.draw(canvas)
        view.draw(canvas)
        return returnedBitmap
    }
}
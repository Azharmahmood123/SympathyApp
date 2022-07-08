package com.astadevs.sympathyapp.views

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class FeedImage : AppCompatImageView {
    constructor(context: Context?) : super(context!!)
    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyle: Int) : super(
        context!!,
        attrs,
        defStyle
    )

    override fun onMeasure(
        widthMeasureSpec: Int,
        heightMeasureSpec: Int
    ) {
        if (drawable != null) {
            val width = MeasureSpec.getSize(widthMeasureSpec)
            val height = (width * drawable.intrinsicHeight
                    / drawable.intrinsicWidth)
            setMeasuredDimension(width, height)
        } else {
            setMeasuredDimension(widthMeasureSpec, heightMeasureSpec)
        }
    }
}
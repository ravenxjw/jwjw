package com.ssafy.miniroom.util

import android.widget.ImageView
import androidx.databinding.BindingAdapter

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, resourceName: String?) {
    resourceName?.let {
        val resId = imageView.context.resources.getIdentifier(
            resourceName,
            "drawable",
            imageView.context.packageName
        )
        if (resId != 0) {
            imageView.setImageResource(resId)
        }
    }
} 
package com.me.kt_firebase_upload_image.ui.bindingadapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.me.kt_firebase_upload_image.R

class MyBinding {

    companion object {

        @BindingAdapter("app:loadImageFromUri")
        @JvmStatic
        fun loadImageFromUri(view: ImageView, imageUri: Uri?) {
            Glide
                .with(view.context)
                .load(imageUri)
                .fitCenter()
                .placeholder(R.drawable.ic_photo_placeholder)
                .into(view)
        }

        @BindingAdapter("app:loadImage")
        @JvmStatic
        fun loadImage(view: ImageView, url: String?) {
            Glide
                .with(view.context)
                .load(url)
                .fitCenter()
                .placeholder(R.drawable.ic_photo_placeholder)
                .into(view)
        }

    }

}
package com.me.kt_firebase_upload_image.ui.bindingadapters

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso

class MyBinding {

    companion object {

        @BindingAdapter("app:loadImageFromUri")
        @JvmStatic
        fun loadImageFromUri(view: ImageView, imageUri: Uri?) {
            imageUri?.let {
                Picasso.with(view.context)
                    .load(it)
                    .into(view)
            }
        }
    }

}
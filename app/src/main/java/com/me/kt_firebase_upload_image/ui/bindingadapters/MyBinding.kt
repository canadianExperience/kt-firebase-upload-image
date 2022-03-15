package com.me.kt_firebase_upload_image.ui.bindingadapters

import android.net.Uri
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import com.me.kt_firebase_upload_image.R
import com.squareup.picasso.Picasso

class MyBinding {

    companion object {

        @BindingAdapter("app:loadImageFromUri")
        @JvmStatic
        fun loadImageFromUri(view: ImageView, imageUri: Uri?) {
            if(imageUri == null){
                Picasso.with(view.context)
                    .load(R.drawable.ic_photo_placeholder)
                    .into(view)
            } else{
                Picasso.with(view.context)
                    .load(imageUri)
                    .into(view)
            }
        }

        @BindingAdapter("app:loadImage")
        @JvmStatic
        fun loadImage(view: ImageView, url: String?) {
            url?.let {
                Picasso.with(view.context)
                    .load(it)
                    .into(view)
            }
        }

    }

}
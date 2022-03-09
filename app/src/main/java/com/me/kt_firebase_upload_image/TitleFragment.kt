package com.me.kt_firebase_upload_image

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.me.kt_firebase_upload_image.databinding.FragmentTitleBinding

class TitleFragment : Fragment(R.layout.fragment_title) {
    private var _binding: FragmentTitleBinding? = null
    private val binding  get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTitleBinding.bind(view)

    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
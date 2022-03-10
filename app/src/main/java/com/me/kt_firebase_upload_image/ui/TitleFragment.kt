package com.me.kt_firebase_upload_image.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.me.kt_firebase_upload_image.MainActivity
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentTitleBinding
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel
import com.squareup.picasso.Picasso

class TitleFragment : Fragment(R.layout.fragment_title) {
    private var _binding: FragmentTitleBinding? = null
    private val binding  get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentTitleBinding.bind(view)

        (requireActivity() as MainActivity).apply {
            supportActionBar?.show()
            actionBar?.show()
        }

        binding.mainViewModel = mainViewModel

        mainViewModel.imageUri.observe(viewLifecycleOwner){ uri->
            uri?.let {

//                val name = binding.editTextFileName.text.trim().toString()
//                val uploadImage = UploadImage(
//                    name.ifEmpty { "No Name" },
//                    it.toString()
//                )


                Picasso.with(requireContext())
                    .load(it)
                    .into(binding.imageView)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
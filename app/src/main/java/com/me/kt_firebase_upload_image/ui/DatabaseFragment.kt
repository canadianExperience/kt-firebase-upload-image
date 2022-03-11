package com.me.kt_firebase_upload_image.ui

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.MimeTypeMap
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.firestore.FirebaseFirestore
import com.me.kt_firebase_upload_image.MainActivity
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentDatabaseBinding
import com.me.kt_firebase_upload_image.utils.DBType
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel
import com.squareup.picasso.Picasso

class DatabaseFragment : Fragment(R.layout.fragment_database) {
    private var _binding: FragmentDatabaseBinding? = null
    private val binding  get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDatabaseBinding.bind(view)

        setFragmentTitle()

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        binding.buttonUpload.setOnClickListener {
            val name = binding.editTextFileName.text.trim().toString()
            mainViewModel.onUploadImageToFirebase(name)
        }

//        readNewImage()
    }

//    private fun readNewImage() = mainViewModel.imageUri.observe(viewLifecycleOwner){ uri->
//        uri?.let {
//            Picasso.with(requireContext())
//                .load(it)
//                .into(binding.imageView)
//        }
//    }


    private fun setFragmentTitle(){
        val title = when(mainViewModel.dbType){
            DBType.REAL_TIME_DATABASE -> "Realtime Database"
            DBType.FIRESTORE_DATABASE -> "Firestore Database"
        }

        (requireActivity() as MainActivity).supportActionBar?.title = title
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
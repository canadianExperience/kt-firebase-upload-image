package com.me.kt_firebase_upload_image.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentDatabaseBinding
import com.me.kt_firebase_upload_image.model.UploadImage
import com.me.kt_firebase_upload_image.ui.MainActivity
import com.me.kt_firebase_upload_image.ui.adapters.IRemoveClickListener
import com.me.kt_firebase_upload_image.ui.adapters.UploadsAdapter
import com.me.kt_firebase_upload_image.utils.DBType
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel
import com.squareup.picasso.Picasso

class DatabaseFragment : Fragment(R.layout.fragment_database), IRemoveClickListener {
    private var _binding: FragmentDatabaseBinding? = null
    private val binding  get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val uploadsAdapter by lazy { UploadsAdapter(this) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDatabaseBinding.bind(view)

        setFragmentTitle()

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        binding.buttonUpload.setOnClickListener {
            val name = binding.editTextFileName.text.trim().toString()
            mainViewModel.onUploadImageToFirebase(name.ifEmpty { "None name" })
        }

        when(mainViewModel.dbType){
            DBType.REAL_TIME_DATABASE ->
                binding.buttonShowUploads.visibility = View.VISIBLE
            DBType.FIRESTORE_DATABASE -> {
                binding.buttonShowUploads.visibility = View.GONE
                mainViewModel.onShowFirestoreUploads()
            }
        }


        setupRecyclerView()

        readNewImage()

        readFirestoreUploads()

        cleanViews()

    }

    private fun cleanViews() = mainViewModel.cleanViews.observe(viewLifecycleOwner){isClean ->
        if(isClean){
            binding.editTextFileName.text.clear()
            mainViewModel.setImageUriFlowValue(null)
        }

    }

    private fun readNewImage() = mainViewModel.imageUri.observe(viewLifecycleOwner){ uri->
        uri?.let {
            Picasso.with(requireContext())
                .load(it)
                .into(binding.imageView)
        }
    }

    private fun readFirestoreUploads() = mainViewModel.firestoreUploads.observe(viewLifecycleOwner){ uploads ->
        uploads?.let {
            uploadsAdapter.setData(it)
        }
    }

    private fun setupRecyclerView() {
        binding.recyclerviewUploads.apply {
            adapter = uploadsAdapter
            layoutManager = GridLayoutManager(requireContext(),2)
                //LinearLayoutManager(requireContext())
        }
    }


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

    override fun onRemoveItemClick(upload: UploadImage) {
        mainViewModel.deleteFromFirebase(upload.id, upload.url)
    }
}
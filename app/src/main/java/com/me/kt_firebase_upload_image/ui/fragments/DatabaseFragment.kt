package com.me.kt_firebase_upload_image.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentDatabaseBinding
import com.me.kt_firebase_upload_image.model.ReadUploadImage
import com.me.kt_firebase_upload_image.ui.MainActivity
import com.me.kt_firebase_upload_image.ui.adapters.IRemoveClickListener
import com.me.kt_firebase_upload_image.ui.adapters.UploadsAdapter
import com.me.kt_firebase_upload_image.utils.DBType
import com.me.kt_firebase_upload_image.utils.RecyclerViewItemDecoration
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel

class DatabaseFragment : Fragment(R.layout.fragment_database), IRemoveClickListener {
    private var _binding: FragmentDatabaseBinding? = null
    private val binding  get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()
    private val uploadsAdapter by lazy { UploadsAdapter(this) }
       private lateinit var type: DBType

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentDatabaseBinding.bind(view)

        type = mainViewModel.dbType

        setFragmentTitle()

        binding.mainViewModel = mainViewModel
        binding.lifecycleOwner = this

        binding.buttonUpload.setOnClickListener {
            val name = binding.editTextFileName.text.trim().toString()
            mainViewModel.onUploadImageToFirebase(name.ifEmpty { "None name" })
        }

        setUpAdapter()
        cleanUpViews()
    }

    private fun setUpAdapter() = binding.recyclerviewUploads.apply {
        adapter = uploadsAdapter
        layoutManager = GridLayoutManager(requireContext(), 2)

        when(type) {
            DBType.REAL_TIME_DATABASE -> readRealTimeUploads()
            DBType.FIRESTORE_DATABASE -> readFirestoreUploads()
        }

        this.addItemDecoration(RecyclerViewItemDecoration(20))
    }

    private fun cleanUpViews() = mainViewModel.cleanViews.observe(viewLifecycleOwner){ isClean ->
        if(isClean){
            binding.editTextFileName.text.clear()
            mainViewModel.setImageUriFlowValue(null)
        }
    }

    private fun readFirestoreUploads() = mainViewModel.firestoreUploads.observe(viewLifecycleOwner){ uploads ->
        uploads?.let { uploadsAdapter.setData(it) }
    }

    private fun readRealTimeUploads() = mainViewModel.realTimeUploads.observe(viewLifecycleOwner){ uploads ->
        uploads?.let { uploadsAdapter.setData(it) }
    }


    private fun setFragmentTitle(){
        val title = when(type){
            DBType.REAL_TIME_DATABASE -> "Realtime Database"
            DBType.FIRESTORE_DATABASE -> "Firestore Database"
        }

        (requireActivity() as MainActivity).supportActionBar?.title = title
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }

    override fun onRemoveItemClick(upload: ReadUploadImage) {
        mainViewModel.deleteFromFirebase(upload.id, upload.url)
    }
}
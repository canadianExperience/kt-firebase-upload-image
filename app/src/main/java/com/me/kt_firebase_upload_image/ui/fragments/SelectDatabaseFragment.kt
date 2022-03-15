package com.me.kt_firebase_upload_image.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentSelectFirebaseStorageBinding
import com.me.kt_firebase_upload_image.ui.MainActivity
import com.me.kt_firebase_upload_image.utils.DBType
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel

class SelectDatabaseFragment : Fragment(R.layout.fragment_select_firebase_storage) {
    private var _binding: FragmentSelectFirebaseStorageBinding? = null
    private val binding  get() = _binding!!
    private val mainViewModel by activityViewModels<MainViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSelectFirebaseStorageBinding.bind(view)

        (requireActivity() as MainActivity).apply {
            supportActionBar?.show()
            actionBar?.show()
        }

        binding.buttonFirestore.setOnClickListener {
            mainViewModel.dbType = DBType.FIRESTORE_DATABASE
            goToDatabaseFragment()
        }
        binding.buttonRealTimeDatabase.setOnClickListener {
            mainViewModel.dbType = DBType.REAL_TIME_DATABASE
            goToDatabaseFragment()
        }
    }

    private fun goToDatabaseFragment(){
        val action = SelectDatabaseFragmentDirections.actionSelectDatabaseFragmentToTitleFragment()
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
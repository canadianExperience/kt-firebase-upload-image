package com.me.kt_firebase_upload_image.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.me.kt_firebase_upload_image.MainActivity
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentSelectFirebaseStorageBinding
import com.me.kt_firebase_upload_image.utils.DBType

class SelectDatabaseFragment : Fragment(R.layout.fragment_select_firebase_storage) {
    private var _binding: FragmentSelectFirebaseStorageBinding? = null
    private val binding  get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSelectFirebaseStorageBinding.bind(view)

        (requireActivity() as MainActivity).apply {
            supportActionBar?.show()
            actionBar?.show()
        }

        binding.buttonFirestore.setOnClickListener {
            goToDatabaseFragment(DBType.FIRESTORE_DATABASE)
        }
        binding.buttonRealTimeDatabase.setOnClickListener {
            goToDatabaseFragment(DBType.REAL_TIME_DATABASE)
        }
    }

    private fun goToDatabaseFragment(dbType: DBType){
        val action = SelectDatabaseFragmentDirections.actionSelectDatabaseFragmentToTitleFragment(dbType)
        findNavController().navigate(action)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
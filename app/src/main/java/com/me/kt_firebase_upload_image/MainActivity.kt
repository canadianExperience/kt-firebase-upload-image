package com.me.kt_firebase_upload_image

import android.app.Activity
import android.content.Intent
import android.content.Intent.ACTION_PICK
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.me.kt_firebase_upload_image.databinding.ActivityMainBinding
import com.me.kt_firebase_upload_image.utils.Constants.Companion.PICK_IMAGE_REQUEST
import com.me.kt_firebase_upload_image.utils.exhaustive
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val mainViewModel by viewModels<MainViewModel>()
    private lateinit var launchActivityResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        navController = navHostFragment.navController

        setupActionBarWithNavController(navController)

        getMainEvents()

        launchActivityResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result -> onActivityResult(PICK_IMAGE_REQUEST, result) }
    }

    private fun getMainEvents() = lifecycleScope.launch {
        mainViewModel.mainEvent.collect { event ->
            when(event){
                MainViewModel.MainEvent.ChooseFile -> {
                    chooseFile()
                }
                is MainViewModel.MainEvent.ShowToast -> {
                    event.message?.let {message ->
                        Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
                    }
                }
            }.exhaustive
        }
    }

    private fun chooseFile() {
        val chooseIntent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            putExtra(ACTION_PICK, true)
        }

        launchActivityResult.launch(
            chooseIntent
        )
    }

    private fun onActivityResult(requestCode: Int, result: ActivityResult){
        if (result.resultCode == Activity.RESULT_OK && requestCode == PICK_IMAGE_REQUEST) {
            result.data?.data?.let {
                mainViewModel.setImageUriFlow(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
package com.me.kt_firebase_upload_image.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.me.kt_firebase_upload_image.MainActivity
import com.me.kt_firebase_upload_image.R
import com.me.kt_firebase_upload_image.databinding.FragmentLoginBinding
import com.me.kt_firebase_upload_image.viewmodels.MainViewModel

class LoginFragment : Fragment(R.layout.fragment_login) {
    private var _binding: FragmentLoginBinding? = null
    private val binding  get() = _binding!!

    private val mainViewModel by activityViewModels<MainViewModel>()

    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentLoginBinding.bind(view)

        (requireActivity() as MainActivity).apply {
            supportActionBar?.hide()
            actionBar?.hide()
        }

        auth = Firebase.auth

        binding.buttonSignup.setOnClickListener {
            firebaseSignUp()
        }

        binding.buttonLogin.setOnClickListener {
            firebaseLogin()
        }
    }

    private fun firebaseSignUp(){
        val username = binding.username.text?.trim().toString()
        val password = binding.password.text?.trim().toString()

        if(username.isEmpty() || password.isEmpty()) {
            mainViewModel.onShowToast("Username or password can not be empty")
        } else {
            auth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        mainViewModel.onShowToast("Signed up successful")
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSelectDatabaseFragment())
                    } else {
                        mainViewModel.onShowToast(task.exception?.message)
                    }
                }
        }
    }

    private fun firebaseLogin(){
        val username = binding.username.text?.trim().toString()
        val password = binding.password.text?.trim().toString()

        if(username.isEmpty() || password.isEmpty()) {
            mainViewModel.onShowToast("Username or password can not be empty")
        } else {
            auth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener {task->
                    if (task.isSuccessful) {
                        mainViewModel.onShowToast("Signed in successfully")
                        findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToSelectDatabaseFragment())
                    } else {
                        mainViewModel.onShowToast(task.exception?.message)
                    }
                }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}
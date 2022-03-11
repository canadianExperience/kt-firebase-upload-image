package com.me.kt_firebase_upload_image.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.me.kt_firebase_upload_image.model.UploadImage
import com.me.kt_firebase_upload_image.utils.DBType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val mainEventChannel = Channel<MainEvent>()
    val mainEvent = mainEventChannel.receiveAsFlow()

    //Firebase Storage
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference("images")
    private val firebaseRef = FirebaseDatabase.getInstance().getReference("images")

    //Firestore Database
    private val firestore = FirebaseFirestore.getInstance()

    //Realtime Database


    var dbType = DBType.FIRESTORE_DATABASE

    private val imageUriFlow = MutableStateFlow<Uri?>(null)
    val imageUri: LiveData<Uri?> get() = imageUriFlow.asLiveData()
    var fileExtension = ""

    private val progressFlow = MutableStateFlow<Int?>(null)
    val progress: LiveData<Int?> get() = progressFlow.asLiveData()


//    private fun getStorageReference(fileName: String){
//        storageRef.child("images/$fileName").downloadUrl
//            .addOnSuccessListener {
//                saveToFirestoreDatabase(it)
//            }
//            .addOnFailureListener{
//                onShowToast("${it.message}")
//            }
//    }

    private fun saveToFirestoreDatabase(reference: String){
        val uploadImage = mutableMapOf<String, Any>()
        uploadImage["name"] = "Some name"
        uploadImage["uri"] = reference

        firestore.collection("uploads")
            .add(uploadImage)
            .addOnSuccessListener {
                onShowToast("Image uploaded to Firestore")
            }
            .addOnFailureListener {
                onShowToast("${it.message}")
                Log.d("FIRESTORE", "${it.message}")
            }
    }

    fun onChooseFileBtnClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.ChooseFile)
    }

    fun onShowToast(message: String) = viewModelScope.launch {
        showToast(message)
    }

    fun onUploadImageToFirebase(name: String) = viewModelScope.launch {
        val uri = imageUriFlow.first()
        uri?.let {
            val uploadImage = getUploadImage(it, name)

            //First save image to FirebaseStorage (cloud)
            saveImageToFirebaseStorage(it)

        } ?: showToast("No file selected")
    }

    fun onShowImage(uri: Uri, extension: String) {
        imageUriFlow.value = uri
        fileExtension = extension
    }

    private fun saveImageToFirebaseStorage(uri: Uri) {
        val fileName = "${System.currentTimeMillis()}" + "." + fileExtension
        val fileRef = storageRef.child(fileName)
        val uploadTask = fileRef.putFile(uri)

       uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            return@Continuation fileRef.downloadUrl
        }).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                saveToFirestoreDatabase(downloadUri.toString())
            } else {
                task.exception?.message?.let { onShowToast(it) }
            }
        }.addOnFailureListener{

        }
    }

    private suspend fun showToast(message: String) = mainEventChannel.send(MainEvent.ShowToast(message))

    private fun getUploadImage(uri: Uri, name: String) =
        UploadImage(
            name.ifEmpty { "No Name" },
            uri.toString()
        )

    sealed class MainEvent{
        object ChooseFile : MainEvent()
        class ShowToast(val message: String?) : MainEvent()
    }
}
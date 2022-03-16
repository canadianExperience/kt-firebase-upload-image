package com.me.kt_firebase_upload_image.viewmodels

import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.me.kt_firebase_upload_image.model.ReadUploadImage
import com.me.kt_firebase_upload_image.model.UploadImage
import com.me.kt_firebase_upload_image.utils.DBType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val mainEventChannel = Channel<MainEvent>()
    val mainEvent = mainEventChannel.receiveAsFlow()

    //Firebase Storage
    private val storage = FirebaseStorage.getInstance()
    private val storageRef = storage.getReference("images")

    //Firestore Database
    private val firestoreRef = FirebaseFirestore.getInstance().collection("uploads")

    //RealTime Database
    private val realTimeRef = Firebase.database.reference.child("uploads")

    //Realtime Database
    var dbType = DBType.FIRESTORE_DATABASE

    private val isUploadingFlow = MutableStateFlow(false)
    val isUploading: LiveData<Boolean> get() = isUploadingFlow.asLiveData()

    private val imageUriFlow = MutableStateFlow<Uri?>(null)
    val imageUri: LiveData<Uri?> get() = imageUriFlow.asLiveData()

    fun setImageUriFlowValue(uri: Uri?){
        imageUriFlow.value = uri
    }

    var fileExtension = ""

    private val cleanViewsFlow = MutableStateFlow<Boolean>(false)
    val cleanViews: LiveData<Boolean> get() = cleanViewsFlow.asLiveData()

    private suspend fun updateCleanViewsFlow(boolean: Boolean){
        cleanViewsFlow.emit(boolean)
    }

    private val progressFlow = MutableStateFlow<Int?>(null)
    val progress: LiveData<Int?> get() = progressFlow.asLiveData()

    private val firestoreUploadsFlow = MutableStateFlow<List<ReadUploadImage>?>(null)
    val firestoreUploads: LiveData<List<ReadUploadImage>?> get() = firestoreUploadsFlow.asLiveData()

    private val realTimeUploadsFlow = MutableStateFlow<List<ReadUploadImage>?>(null)
    val realTimeUploads: LiveData<List<ReadUploadImage>?> get() = realTimeUploadsFlow.asLiveData()

    init {
        onShowRealTimeUploads()
        onShowFirestoreUploads()
    }

    private fun onShowRealTimeUploads(){
        val childEventListener = object : ChildEventListener {
            override fun onChildAdded(dataSnapshot: DataSnapshot, previousChildName: String?) {
                updateRealTimeUploads(dataSnapshot, true)
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot, previousChildName: String?) {
            }

            override fun onChildRemoved(dataSnapshot: DataSnapshot) {
                updateRealTimeUploads(dataSnapshot, false)
            }

            override fun onChildMoved(dataSnapshot: DataSnapshot, previousChildName: String?) {
                val image = dataSnapshot.getValue<UploadImage>()
                val imageKey = dataSnapshot.key
            }

            override fun onCancelled(databaseError: DatabaseError) {
                onShowToast(databaseError.message)
            }
        }
        realTimeRef.addChildEventListener(childEventListener)
    }

    private fun updateRealTimeUploads(dataSnapshot: DataSnapshot, isAdd: Boolean){
        val image = dataSnapshot.getValue<UploadImage>()

        image?.let {
            val key = dataSnapshot.key
            key?.let {id->
                val imageWithId = ReadUploadImage(id, it.name, it.url)
                viewModelScope.launch {
                    val uploads = realTimeUploadsFlow.value?.toMutableList() ?: mutableListOf()
                    if(isAdd) uploads.add(imageWithId) else uploads.remove(imageWithId)

                    realTimeUploadsFlow.emit(uploads)
                }
            }
        }
    }

    private fun onShowFirestoreUploads() {
        firestoreRef
            .orderBy("name", Query.Direction.ASCENDING)
            .addSnapshotListener { value, error ->
                error?.let { exception ->
                    exception.message?.let { onShowToast(it) }
                }

                val docs = value?.documentChanges
                docs?.let {documentChanges ->
                    for (dc: DocumentChange in documentChanges) {
                        if(dc.type == DocumentChange.Type.ADDED || dc.type == DocumentChange.Type.REMOVED){
                            val image = dc.document.toObject(UploadImage::class.java)
                            val key = dc.document.id
                            val imageWithId = ReadUploadImage(key, image.name, image.url)

                            viewModelScope.launch {
                                val uploads = firestoreUploadsFlow.value?.toMutableList() ?: mutableListOf()
                                when(dc.type){
                                    DocumentChange.Type.ADDED -> uploads.add(imageWithId)
                                    DocumentChange.Type.REMOVED -> uploads.remove(imageWithId)
                                    else -> {}
                                }

                                firestoreUploadsFlow.emit(uploads)
                            }
                        }
                    }
                }
            }
    }


    fun deleteFromFirebase(id: String, url: String?) {
        when(dbType){
            DBType.FIRESTORE_DATABASE -> deleteUploadFromFirestoreDatabase(id)
            DBType.REAL_TIME_DATABASE -> deleteUploadFromRealTimeDatabase(id)
        }
        deleteImageFromFirebaseStorage(url)
    }

    private fun deleteUploadFromFirestoreDatabase(id: String) = firestoreRef
        .document(id)
        .delete()
        .addOnSuccessListener {
            onShowToast("Upload deleted from Firestore")
        }
        .addOnFailureListener {
            onShowToast("${it.message}")
        }

    private fun deleteUploadFromRealTimeDatabase(id: String) {
        realTimeRef
            .child(id)
            .removeValue()
            .addOnSuccessListener {
                onShowToast("Upload deleted from Real Time Database")
            }
            .addOnFailureListener {
                onShowToast("${it.message}")
            }
    }

    private fun deleteImageFromFirebaseStorage(url: String?) = storage
        .getReferenceFromUrl(url?:"")
        .delete()
        .addOnSuccessListener {
            onShowToast("Image deleted from Firebase Storage")
        }
        .addOnFailureListener { exception ->
            onShowToast("${exception.message}")
        }

    private fun saveToRealTimeDatabase(name: String, reference: String){
        val uploadImage = UploadImage(name, reference)

        realTimeRef
            .push()
            .setValue(uploadImage)
            .addOnSuccessListener {
                onDatabaseUploadProgressSuccess("Image uploaded to Real Time Database")
            }
            .addOnFailureListener {
                onShowToast("${it.message}")
                Log.d("REAL_TIME_DB", "${it.message}")
            }
    }

    private fun onDatabaseUploadProgressSuccess(message: String) = viewModelScope.launch {
        delay(500)
        progressFlow.emit(0)
        isUploadingFlow.emit(false)
        updateCleanViewsFlow(true)
        showToast(message)
    }


    private fun saveToFirestoreDatabase(name: String, reference: String){
        val uploadImage = mutableMapOf<String, Any>()
        uploadImage["name"] = name
        uploadImage["url"] = reference

        firestoreRef
            .add(uploadImage)
            .addOnSuccessListener {
                onDatabaseUploadProgressSuccess("Image uploaded to Firestore")
            }
            .addOnFailureListener {
                onShowToast("${it.message}")
                Log.d("FIRESTORE", "${it.message}")
            }
    }

    fun onChooseFileBtnClick() = viewModelScope.launch {
        updateCleanViewsFlow(false)
        mainEventChannel.send(MainEvent.ChooseFile)
    }

    fun onShowToast(message: String) = viewModelScope.launch {
        showToast(message)
    }


    fun onUploadImageToFirebase(name: String) {
        viewModelScope.launch {
            val uri = imageUriFlow.value
            uri?.let {
                isUploadingFlow.emit(true)
                saveImageToFirebase(it, name)
            } ?: showToast("No file selected")
        }
    }

    fun onShowImage(uri: Uri, extension: String) {
        fileExtension = extension
        setImageUriFlowValue(uri)
    }



    private fun saveImageToFirebase(uri: Uri, name: String) {
        val fileName = "${System.currentTimeMillis()}" + "." + fileExtension
        val fileRef = storageRef.child(fileName)
        val uploadTask = fileRef.putFile(uri)

        uploadTask
            .addOnProgressListener {task ->
                val progress = (100.0 * task.bytesTransferred / task.totalByteCount)
                progressFlow.value = progress.toInt()
            }

            .continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (task.isSuccessful) {
                    return@Continuation fileRef.downloadUrl
                } else task.exception?.let {
                    throw it
                }
            })
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onShowToast("Image uploaded to Firebase Storage")
                    val downloadUrl = task.result.toString()
                    when(dbType){
                        DBType.REAL_TIME_DATABASE -> saveToRealTimeDatabase(name, downloadUrl)
                        DBType.FIRESTORE_DATABASE -> saveToFirestoreDatabase(name, downloadUrl)
                    }
                } else {
                    task.exception?.message?.let { onShowToast(it) }
                }
            }

            .addOnFailureListener{exception ->
                exception.message?.let { onShowToast(it) }
            }
    }

    private suspend fun showToast(message: String) = mainEventChannel.send(MainEvent.ShowToast(message))

    sealed class MainEvent{
        object ChooseFile : MainEvent()
        class ShowToast(val message: String?) : MainEvent()
    }
}
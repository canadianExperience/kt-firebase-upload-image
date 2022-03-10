package com.me.kt_firebase_upload_image.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class MainViewModel: ViewModel() {

    private val mainEventChannel = Channel<MainEvent>()
    val mainEvent = mainEventChannel.receiveAsFlow()

    private val imageUriFlow = MutableStateFlow<Uri?>(null)
    val imageUri: LiveData<Uri?> get() = imageUriFlow.asLiveData()

    fun setImageUriFlow(uri: Uri?){
        imageUriFlow.value = uri
    }

    fun onChooseFileBtnClick() = viewModelScope.launch {
        mainEventChannel.send(MainEvent.ChooseFile)
    }

    sealed class MainEvent{
        object ChooseFile : MainEvent()
    }
}
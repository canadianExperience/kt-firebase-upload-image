package com.me.kt_firebase_upload_image.model

data class UploadImage(
    var id: String,
    var name: String,
    var url: String?
){
    constructor() : this( "", "None name", null)
}

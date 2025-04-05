package com.example.sharedfast.model

import android.net.Uri

data class ImageModel(
    val imageUri: Uri,
    val title: String,
    val date: String,
    val time: String
)

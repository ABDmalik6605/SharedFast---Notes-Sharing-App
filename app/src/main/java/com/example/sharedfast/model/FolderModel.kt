package com.example.sharedfast.model

import com.google.gson.annotations.SerializedName

data class FolderModel(
    @SerializedName("imageRes") val imageRes: Int,
    @SerializedName("text") val text: String,
    @SerializedName("folderPath") val folderPath: String
)

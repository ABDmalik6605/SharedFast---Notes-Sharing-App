package com.example.sharedfast

import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

class ImageDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_detail)

        val imageView: ImageView = findViewById(R.id.imageViewDetail)
        val imageUri = Uri.parse(intent.getStringExtra("imageUri"))

        Glide.with(this).load(imageUri).into(imageView)
    }
}

package com.example.sharedfast

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.sharedfast.adapter.ImageRecyclerViewActivity
import com.example.sharedfast.model.ImageModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ImageRecyclerViewActivity

    private val REQUEST_IMPORT_FILES = 1001
    private val REQUEST_IMPORT_IMAGES = 1002
    private val REQUEST_CAPTURE_IMAGE = 1003
    private val CAMERA_PERMISSION_CODE = 101

    private var currentPhotoPath: String? = null
    private var fPath: String? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        val folderPath = intent.getStringExtra("folderPath")
        adapter = ImageRecyclerViewActivity(getImagesFromInternalStorage(folderPath))
        fPath=folderPath

        recyclerView.adapter = adapter

        findViewById<Button>(R.id.btnImportFiles).setOnClickListener {
            openFilePicker()
        }

        findViewById<Button>(R.id.btnImportImages).setOnClickListener {
            openGalleryPicker()
        }

        findViewById<Button>(R.id.btnCaptureImage).setOnClickListener {
            findViewById<Button>(R.id.btnCaptureImage).setOnClickListener {
                if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
                } else {
                    captureImage()
                }
            }

        }
    }

    private fun captureImage() {
        val photoFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            ex.printStackTrace()
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show()
            null
        }

        photoFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                it
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_CAPTURE_IMAGE)
        }
    }


    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val folder = File(fPath)
        if (!folder.exists()) folder.mkdirs()

        return File.createTempFile(
            "IMG_${timeStamp}_",
            ".jpg",
            folder
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    private fun openGalleryPicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_IMPORT_IMAGES)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        startActivityForResult(intent, REQUEST_IMPORT_FILES)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK) return

        when (requestCode) {
            REQUEST_IMPORT_FILES, REQUEST_IMPORT_IMAGES -> {
                data?.data?.let { uri ->
                    copyFileToInternalStorage(uri)
                }
            }

            REQUEST_CAPTURE_IMAGE -> {
                currentPhotoPath?.let { path ->
                    val file = File(path)
                    if (file.exists()) {
                        refreshImageList()
                    }
                } ?: run {
                    Toast.makeText(this, "Photo path is not set", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun copyFileToInternalStorage(uri: Uri) {
        try {
            val fileName = "IMG_${System.currentTimeMillis()}.jpg"
            val folder = File(fPath)
            if (!folder.exists()) folder.mkdirs()

            val file = File(folder, fileName)

            contentResolver.openInputStream(uri)?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("ImageListActivity", "File copied to: ${file.absolutePath}")
            refreshImageList()
        } catch (e: Exception) {
            Log.e("ImageListActivity", "Failed to copy file: ${e.message}", e)
        }
    }

    private fun getImagesFromInternalStorage(folderPath: String?): List<ImageModel> {
        if (folderPath == null) return emptyList()

        val targetFolder = File(folderPath)
        if (!targetFolder.exists() || !targetFolder.isDirectory) return emptyList()

        val imageFiles = targetFolder.listFiles()?.filter {
            it.name.endsWith(".jpg") || it.name.endsWith(".png")
        } ?: emptyList()

        return imageFiles.map {
            ImageModel(Uri.fromFile(it), it.name, "", "")
        }
    }

    private fun refreshImageList() {
        val images = getImagesFromInternalStorage(fPath)
        adapter = ImageRecyclerViewActivity(images)
        recyclerView.adapter = adapter
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                captureImage()
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

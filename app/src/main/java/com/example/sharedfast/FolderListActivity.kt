package com.example.sharedfast


import android.content.ContentResolver
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedfast.adapter.FolderRecyclerViewAdapter
import com.example.sharedfast.model.FolderModel

class FolderListActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.list_folder)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val folders = getImageFolders()
        adapter = FolderRecyclerViewAdapter(folders)
        recyclerView.adapter = adapter
    }

    private fun getImageFolders(): List<FolderModel> {
        val folderSet = mutableSetOf<FolderModel>()
        val projection = arrayOf(MediaStore.Images.Media.BUCKET_DISPLAY_NAME, MediaStore.Images.Media.BUCKET_ID)

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null, null, null
        )?.use { cursor ->
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME)
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID)

            while (cursor.moveToNext()) {
                val folderName = cursor.getString(nameColumn)
                val folderPath = cursor.getString(idColumn)
                folderSet.add(FolderModel(R.drawable.folder, folderName,folderPath))
            }
        }
        return folderSet.toList()
    }
}

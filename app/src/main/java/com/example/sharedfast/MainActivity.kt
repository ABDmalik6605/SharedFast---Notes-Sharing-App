package com.example.sharedfast

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedfast.adapter.FolderRecyclerViewAdapter
import com.example.sharedfast.model.FolderModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.FileReader
import java.io.FileWriter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FolderRecyclerViewAdapter
    private val itemList = mutableListOf<FolderModel>()
    private lateinit var folderListFile: File


    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        val fabAdd: FloatingActionButton = findViewById(R.id.fab_add)
        folderListFile = File(filesDir, "folders.json")
        adapter = FolderRecyclerViewAdapter(itemList)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        loadFolders()

        // Floating button click listener
        fabAdd.setOnClickListener {
            val newFolderName = "Item ${itemList.size + 1}"
            val newFolderPath = "/storage/emulated/0/Pictures/Folder${itemList.size + 1}"

            val newItem = FolderModel(R.drawable.folder, newFolderName, newFolderPath)
            itemList.add(newItem)
            adapter.notifyItemInserted(itemList.size - 1)

            saveFolders()
            Toast.makeText(this, "Item Added", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to check if storage permission is granted
    private fun hasStoragePermission(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
    }

    // Function to request storage permission
    private fun requestStoragePermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
    }

    // Function to save folder list to a JSON file
    private fun saveFolders() {
        Log.d("Save","Saving folders")
        try {
            val json = Gson().toJson(itemList)
            FileWriter(folderListFile).use { it.write(json) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Function to load folder list from a JSON file
    private fun loadFolders() {
        Log.d("Load","Loading")
        if (folderListFile.exists()) {
            Log.d("Load","Files found")
            try {
                val type = object : TypeToken<MutableList<FolderModel>>() {}.type
                val loadedList: MutableList<FolderModel> =
                    Gson().fromJson(FileReader(folderListFile), type)

                itemList.clear()
                itemList.addAll(loadedList)
                adapter.notifyDataSetChanged()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

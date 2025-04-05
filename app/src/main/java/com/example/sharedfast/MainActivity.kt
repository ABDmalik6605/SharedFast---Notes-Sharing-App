package com.example.sharedfast

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

        adapter = FolderRecyclerViewAdapter(itemList, this) {
            saveFolders()
        }

        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = adapter

        loadFolders()

        fabAdd.setOnClickListener {
            val builder = android.app.AlertDialog.Builder(this)
            builder.setTitle("Create New Folder")

            val input = android.widget.EditText(this)
            input.hint = "Enter folder name"
            builder.setView(input)

            builder.setPositiveButton("Create") { dialog, _ ->
                val folderName = input.text.toString().trim()
                if (folderName.isNotEmpty()) {
                    val folderPath = "/storage/emulated/0/Pictures/$folderName"
                    val newItem = FolderModel(R.drawable.folder, folderName, folderPath)
                    itemList.add(newItem)
                    adapter.notifyItemInserted(itemList.size - 1)
                    saveFolders()
                    Toast.makeText(this, "Folder '$folderName' created", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Folder name cannot be empty", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

            builder.show()
        }

    }

    private fun saveFolders() {
        Log.d("Save", "Saving folders")
        try {
            val json = Gson().toJson(itemList)
            FileWriter(folderListFile).use { it.write(json) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadFolders() {
        Log.d("Load", "Loading")
        if (folderListFile.exists()) {
            Log.d("Load", "Files found")
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

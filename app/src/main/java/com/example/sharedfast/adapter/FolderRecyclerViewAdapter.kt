package com.example.sharedfast.adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedfast.ImageListActivity
import com.example.sharedfast.R
import com.example.sharedfast.model.FolderModel
import java.io.File

class FolderRecyclerViewAdapter(
    private val folderList: MutableList<FolderModel>,
    private val context: Context,
    private val onFolderListChanged: () -> Unit
) : RecyclerView.Adapter<FolderRecyclerViewAdapter.FolderViewHolder>() {

    class FolderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.Folder_image)
        val textView: TextView = view.findViewById(R.id.Folder_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FolderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_folder, parent, false)
        return FolderViewHolder(view)
    }

    override fun getItemCount(): Int = folderList.size

    override fun onBindViewHolder(holder: FolderViewHolder, position: Int) {
        val folder = folderList[position]
        holder.imageView.setImageResource(folder.imageRes)
        holder.textView.text = folder.text

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ImageListActivity::class.java).apply {
                putExtra("folderName", folder.text)
                putExtra("folderPath", folder.folderPath)
            }
            holder.itemView.context.startActivity(intent)
        }

        holder.itemView.setOnLongClickListener {
            showFolderOptions(holder.itemView.context, folder)
            true
        }
    }

    private fun showFolderOptions(context: Context, folder: FolderModel) {
        val options = arrayOf("Delete Folder", "Share Folder")

        AlertDialog.Builder(context)
            .setTitle(folder.text)
            .setItems(options) { _, which ->
                when (which) {
                    0 -> deleteFolder(context, folder)
                    1 -> shareFolder(context, folder)
                }
            }
            .show()
    }

    private fun deleteFolder(context: Context, folder: FolderModel) {
        val folderFile = File(folder.folderPath)
        if (!folderFile.exists()) {
            folderFile.mkdirs()
        }

        AlertDialog.Builder(context)
            .setTitle("Delete ${folder.text}?")
            .setMessage("This will permanently delete the folder and its contents.")
            .setPositiveButton("Delete") { _, _ ->
                try {
                    if (folderFile.exists()) {
                        folderFile.deleteRecursively()
                        Toast.makeText(context, "Folder deleted", Toast.LENGTH_SHORT).show()
                        folderList.remove(folder)
                        onFolderListChanged() // Save to JSON
                        notifyDataSetChanged()
                    } else {
                        Toast.makeText(context, "Folder does not exist", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun shareFolder(context: Context, folder: FolderModel) {
        try {
            val folderFiles = File(folder.folderPath).listFiles()
            val uris = folderFiles?.map { file ->
                FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
            }?.toList() ?: emptyList()

            if (uris.isNotEmpty()) {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND_MULTIPLE
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                    type = "*/*"
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(shareIntent, "Share via"))
            } else {
                Toast.makeText(context, "No files to share in the folder", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Failed to share: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    //    private fun shareFolder(context: Context, folder: FolderModel) {
//        try {
//            val folderFiles = File(folder.folderPath).listFiles()
//            val uris = folderFiles?.map { file ->
//                FileProvider.getUriForFile(
//                    context,
//                    "${context.packageName}.fileprovider",
//                    file
//                )
//            }?.toList() ?: emptyList()
//
//            if (uris.isEmpty()) {
//                Toast.makeText(context, "No files to share in the folder", Toast.LENGTH_SHORT).show()
//                return
//            }
//
//            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
//                putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
//                type = "/"
//                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            }
//
//            val packageManager = context.packageManager
//            val resolvedInfos = packageManager.queryIntentActivities(shareIntent, 0)
//
//            val allowedPackages = listOf(
//                "com.android.bluetooth",
//                "com.facebook.katana",
//                "com.google.android.gm",
//                "com.whatsapp"
//            )
//
//            val targetedIntents = resolvedInfos.mapNotNull { info ->
//                val packageName = info.activityInfo.packageName
//                if (allowedPackages.contains(packageName)) {
//                    Intent(shareIntent).apply {
//                        setPackage(packageName)
//                        setClassName(packageName, info.activityInfo.name)
//                    }
//                } else null
//            }.toMutableList()
//
//            if (targetedIntents.isNotEmpty()) {
//                val chooserIntent = Intent.createChooser(targetedIntents.removeAt(0), "Share via")
//                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetedIntents.toTypedArray())
//                context.startActivity(chooserIntent)
//            } else {
//                Toast.makeText(context, "No allowed apps installed for sharing", Toast.LENGTH_SHORT).show()
//            }
//
//        } catch (e: Exception) {
//            Toast.makeText(context, "Failed to share: ${e.message}", Toast.LENGTH_SHORT).show()
//        }
//    }
}

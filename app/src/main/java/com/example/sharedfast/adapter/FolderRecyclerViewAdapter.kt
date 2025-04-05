package com.example.sharedfast.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedfast.ImageListActivity
import com.example.sharedfast.R
import com.example.sharedfast.model.FolderModel

class FolderRecyclerViewAdapter(private val folderList: List<FolderModel>) :
    RecyclerView.Adapter<FolderRecyclerViewAdapter.FolderViewHolder>() {

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
            }
            holder.itemView.context.startActivity(intent)
        }
    }
}

package com.example.sharedfast.adapter

import com.example.sharedfast.model.ImageModel

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sharedfast.ImageDetailActivity
import com.bumptech.glide.Glide
import com.example.sharedfast.R


class ImageRecyclerViewActivity(private val images: List<ImageModel>) :
    RecyclerView.Adapter<ImageRecyclerViewActivity.ImageViewHolder>() {

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val titleView: TextView = itemView.findViewById(R.id.titleView)
        val dateView: TextView = itemView.findViewById(R.id.dateView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image = images[position]
        Glide.with(holder.itemView.context).load(image.imageUri).into(holder.imageView)
        holder.titleView.text = image.title
        holder.dateView.text = "${image.date} | ${image.time}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, ImageDetailActivity::class.java)
            intent.putExtra("imageUri", image.imageUri.toString())
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = images.size
}

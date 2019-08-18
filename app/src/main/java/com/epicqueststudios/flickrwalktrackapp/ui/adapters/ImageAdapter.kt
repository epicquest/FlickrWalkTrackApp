package com.epicqueststudios.flickrwalktrackapp.ui.adapters

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.epicqueststudios.flickrwalktrackapp.Constants
import com.epicqueststudios.flickrwalktrackapp.R
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhoto
import com.epicqueststudios.flickrwalktrackapp.ui.main.MainActivity
import kotlinx.android.synthetic.main.item_photo.view.*

class ImageAdapter(val photoList: MutableList<FlickrPhoto>) : RecyclerView.Adapter<ImageAdapter.ImageHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_photo, parent, false)
        return ImageHolder(v)
    }

    override fun getItemCount(): Int = photoList.size


    override fun onBindViewHolder(holder: ImageHolder, position: Int) {
        Glide.with(holder.itemView.context).load(createUri(photoList[position])).into(holder.imageView)
        holder.imageView.contentDescription = photoList[position].title
    }

    // uri example https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}.jpg
    private fun createUri(flickrPhoto: FlickrPhoto): Uri {
        val builder = Uri.Builder()
        builder.scheme(Constants.SCHEME)
            .authority("farm${flickrPhoto.farm}.staticflickr.com")
            .appendPath(flickrPhoto.server)
            .appendPath("${flickrPhoto.id}_${flickrPhoto.secret}.jpg")
        return builder.build()
    }

    fun addImage(photo: FlickrPhoto) {
        if (Constants.DO_NOT_ADD_DUPLICATE_PHOTOS && checkIfPhotoAlreadyPresentsInList(photo)) {
            return
        }
        photoList.add(0, photo)
        notifyDataSetChanged()
    }

    private fun checkIfPhotoAlreadyPresentsInList(photo: FlickrPhoto): Boolean {
        for (item in photoList) {
            if (item == photo) {
                Log.d(MainActivity.TAG, "Photo already presents in the list: ${item.id}")
                return true
            }
        }
        Log.d(MainActivity.TAG, "New photo found: ${photo.id}")
        return false
    }

    class ImageHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.photo
    }
}
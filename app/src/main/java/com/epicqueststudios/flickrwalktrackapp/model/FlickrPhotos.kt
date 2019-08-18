package com.epicqueststudios.flickrwalktrackapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FlickrPhotos(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    @Expose
    @SerializedName("photo")
    val flickrPhoto: List<FlickrPhoto>,
    val total: String
)
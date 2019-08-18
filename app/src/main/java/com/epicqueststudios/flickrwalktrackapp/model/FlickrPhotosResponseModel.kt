package com.epicqueststudios.flickrwalktrackapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FlickrPhotosResponseModel(
    @Expose
    @SerializedName("photos")
    val flickrPhotos: FlickrPhotos,
    val stat: String
)
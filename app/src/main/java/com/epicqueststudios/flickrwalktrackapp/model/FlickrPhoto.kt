package com.epicqueststudios.flickrwalktrackapp.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class FlickrPhoto(
    @Expose
    val farm: Int,
    @Expose
    val id: String,
    @SerializedName("isfamily")
    val isFamily: Int,
    @SerializedName("isfriend")
    val isFriend: Int,
    @SerializedName("ispublic")
    val isPublic: Int,
    val owner: String,
    @Expose
    val secret: String,
    @Expose
    val server: String,
    @Expose
    val title: String
) {
    override fun equals(other: Any?): Boolean {
        if (other == null || other !is FlickrPhoto)
            return false
        return id == other.id && server == other.server
    }

    override fun hashCode(): Int {
        var result = farm
        result = 31 * result + id.hashCode()
        result = 31 * result + isFamily
        result = 31 * result + isFriend
        result = 31 * result + isPublic
        result = 31 * result + owner.hashCode()
        result = 31 * result + secret.hashCode()
        result = 31 * result + server.hashCode()
        result = 31 * result + title.hashCode()
        return result
    }
}
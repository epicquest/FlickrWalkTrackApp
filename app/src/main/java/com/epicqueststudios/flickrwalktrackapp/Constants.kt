package com.epicqueststudios.flickrwalktrackapp

class Constants {
    companion object {
        // GPS
        const val INTERVAL = 20000L
        const val FASTEST_INTERVAL = INTERVAL
        const val BASE_URL = "https://www.flickr.com/"
        const val REQUESTING_LOCATION_UPDATES_KEY: String = "REQUESTING_LOCATION_UPDATES_KEY"

        const val MINIMUM_DISTANCE = 100f // in meters

        // Flickr
        const val SCHEME = "https"
        const val RADIUS = 0.1f
        const val PER_PAGE = 1
        const val DO_NOT_ADD_DUPLICATE_PHOTOS = false
        const val FLICKR_METHOD = "flickr.photos.search"

        const val API_KEY = "df7124806ed43561f055629c305ec588" //DO NOT COMMIT
    }
}
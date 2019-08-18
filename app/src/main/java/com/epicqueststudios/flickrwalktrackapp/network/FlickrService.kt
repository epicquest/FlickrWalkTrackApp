package com.epicqueststudios.flickrwalktrackapp.network

import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhotosResponseModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {
    @GET("/services/rest/")
    fun photosForLocation(
        @Query("method") method: String,
        @Query("api_key") api_key: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String,
        @Query("radius") radius: Float,
        @Query("per_page") perPage: Int,
        @Query("nojsoncallback") noJsonCallback: Int
    ): Single<Response<FlickrPhotosResponseModel>>
}
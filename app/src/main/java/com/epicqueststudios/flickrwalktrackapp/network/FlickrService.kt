package com.epicqueststudios.flickrwalktrackapp.network

import com.epicqueststudios.flickrwalktrackapp.Constants
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhotosResponseModel
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FlickrService {
    @GET("/services/rest/")
    fun photosForLocation(
        @Query("method") method: String = Constants.FLICKR_METHOD,
        @Query("api_key") api_key: String = Constants.API_KEY,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("format") format: String = "json",
        @Query("radius") radius: Float = Constants.RADIUS,
        @Query("per_page") perPage: Int = Constants.PER_PAGE,
        @Query("nojsoncallback") noJsonCallback: Int = 1
    ): Single<Response<FlickrPhotosResponseModel>>
}
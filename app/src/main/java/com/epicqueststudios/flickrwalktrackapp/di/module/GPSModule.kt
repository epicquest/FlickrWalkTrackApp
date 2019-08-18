package com.epicqueststudios.flickrwalktrackapp.di.module

import android.content.Context
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class GPSModule {
    @Singleton
    @Provides
    fun providesLocation(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(
            context
        )
    }
}
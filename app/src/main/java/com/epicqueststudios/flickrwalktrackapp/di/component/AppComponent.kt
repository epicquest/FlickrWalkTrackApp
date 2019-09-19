package com.epicqueststudios.flickrwalktrackapp.di.component

import com.epicqueststudios.FlickrWalkApplication
import com.epicqueststudios.flickrwalktrackapp.GPSTrackService
import com.epicqueststudios.flickrwalktrackapp.di.module.ApplicationModule
import com.epicqueststudios.flickrwalktrackapp.di.module.GPSModule
import com.epicqueststudios.flickrwalktrackapp.di.module.NetworkModule
import com.epicqueststudios.flickrwalktrackapp.ui.main.MainActivity
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [ApplicationModule::class, GPSModule::class, NetworkModule::class])
interface AppComponent {

    fun inject(app: FlickrWalkApplication)
    fun inject(activity: MainActivity)
    fun inject(service: GPSTrackService)
}
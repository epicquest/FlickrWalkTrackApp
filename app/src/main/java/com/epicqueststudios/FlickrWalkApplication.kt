package com.epicqueststudios

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.epicqueststudios.flickrwalktrackapp.di.component.AppComponent
import com.epicqueststudios.flickrwalktrackapp.di.component.DaggerAppComponent
import com.epicqueststudios.flickrwalktrackapp.di.module.ApplicationModule

class FlickrWalkApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().applicationModule(ApplicationModule(this)).build()
        appComponent.inject(this)

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
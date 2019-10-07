package com.epicqueststudios

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDex
import com.epicqueststudios.flickrwalktrackapp.database.LocationDatabase
import com.epicqueststudios.flickrwalktrackapp.database.entity.LocationEntity
import com.epicqueststudios.flickrwalktrackapp.di.component.AppComponent
import com.epicqueststudios.flickrwalktrackapp.di.component.DaggerAppComponent
import com.epicqueststudios.flickrwalktrackapp.di.module.ApplicationModule
import org.jetbrains.anko.doAsync

class FlickrWalkApplication : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder().applicationModule(ApplicationModule(this)).build()
        appComponent.inject(this)

        doAsync {
            val database = LocationDatabase.getInstance(context = this@FlickrWalkApplication)

            if (database.locationDao().all.isEmpty()) {
                val locations: MutableList<LocationEntity> = mutableListOf()
                for (index: Int in 0..20) {
                    val client = LocationEntity(index.toDouble(), index.toDouble())
                    locations.add(index, client)
                }
                database.locationDao().insertAll(locations)
            }
        }
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }
}
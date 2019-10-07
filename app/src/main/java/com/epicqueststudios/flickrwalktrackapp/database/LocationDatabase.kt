package com.epicqueststudios.flickrwalktrackapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.epicqueststudios.flickrwalktrackapp.database.dao.LocationEntityDao
import com.epicqueststudios.flickrwalktrackapp.database.entity.LocationEntity

@Database(entities = [(LocationEntity::class)], version = 1, exportSchema = false)
abstract class LocationDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationEntityDao

    companion object {

        private var sInstance: LocationDatabase? = null

        @Synchronized
        fun getInstance(context: Context): LocationDatabase {
            if (sInstance == null) {
                sInstance = Room
                    .databaseBuilder(context.applicationContext, LocationDatabase::class.java, "locations")
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return sInstance!!
        }
    }

}

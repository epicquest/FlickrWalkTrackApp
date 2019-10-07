package com.epicqueststudios.flickrwalktrackapp.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.epicqueststudios.flickrwalktrackapp.database.entity.LocationEntity

@Dao
interface LocationEntityDao {

    @get:Query("SELECT * FROM LocationEntity")
    val all: List<LocationEntity>

    @Query("SELECT * FROM LocationEntity WHERE uid = (:locationId)")
    fun findById(locationId: Int): LocationEntity

    @Insert
    fun insertAll(locations: List<LocationEntity>)

    @Insert
    fun insert(location: LocationEntity)

    @Delete
    fun delete(location: LocationEntity)

}
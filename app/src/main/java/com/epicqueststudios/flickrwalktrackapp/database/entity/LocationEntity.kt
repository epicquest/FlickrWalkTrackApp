package com.epicqueststudios.flickrwalktrackapp.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class LocationEntity constructor(latitude: Double, longitude: Double) {

    @PrimaryKey(autoGenerate = true)
    var uid: Int = 0

    @ColumnInfo(name = "latitude")
    var latitude: Double? = latitude

    @ColumnInfo(name = "longitude")
    var longitude: Double? = longitude
}
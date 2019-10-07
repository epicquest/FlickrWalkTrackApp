package com.epicqueststudios.flickrwalktrackapp.interfaces

import android.location.Location

interface LocationAddedCallback {
     fun onLocation(location: Location)
}
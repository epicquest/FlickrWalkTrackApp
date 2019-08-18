package com.epicqueststudios.flickrwalktrackapp.ui.main

import com.epicqueststudios.flickrwalktrackapp.base.BaseContract
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhotosResponseModel

class MainViewContract {

    interface View : BaseContract.View {
        fun populatePhotos(model: FlickrPhotosResponseModel?)
    }

}
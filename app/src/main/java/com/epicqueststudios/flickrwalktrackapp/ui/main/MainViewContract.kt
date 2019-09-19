package com.epicqueststudios.flickrwalktrackapp.ui.main

import com.epicqueststudios.flickrwalktrackapp.base.BaseContract
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhoto

class MainViewContract {

    interface View : BaseContract.View {
        fun populatePhoto(photo: FlickrPhoto)
    }

}
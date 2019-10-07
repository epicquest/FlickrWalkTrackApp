package com.epicqueststudios.flickrwalktrackapp.base

class BaseContract {

    interface Presenter<in T> {
        fun attach(view: T)
        fun detach()
        fun isAttached():Boolean
    }

    interface View
}
package com.epicqueststudios.flickrwalktrackapp.features.permission

interface IPermissionAskInterface {
    fun onPermissionAsk()
    fun onPermissionPreviouslyDenied()
    fun onPermissionDisabled()
    fun onPermissionGranted()
    fun onPermissionDenied()
}
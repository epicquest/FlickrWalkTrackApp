package com.epicqueststudios.flickrwalktrackapp.features.permission

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat

object PermissionUtils {

    const val REQUEST_ACCESS_FINE_LOCATION = 10001

    private fun shouldAskPermission(): Boolean {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
    }

    @JvmStatic
    fun isPermissionGranted(context: Context, permission: String): Boolean {
        if (shouldAskPermission()) {
            val permissionResult = ActivityCompat.checkSelfPermission(context, permission)
            if (permissionResult != PackageManager.PERMISSION_GRANTED) {
                return true
            }
        }
        return false
    }

    @JvmStatic
    fun checkPermission(activity: Activity, permission: String, listener: IPermissionAskInterface) {

        if (isPermissionGranted(activity, permission)) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                listener.onPermissionPreviouslyDenied()
                listener.onPermissionAsk()
            } else {
                if (PreferencesUtils.isFirstTimeAskingPermission(activity, permission)) {
                    PreferencesUtils.firstTimeAskingPermission(activity, permission, false)
                    listener.onPermissionAsk()
                } else {
                    listener.onPermissionDisabled()
                }
            }
        } else {
            listener.onPermissionGranted()
        }
    }

    @JvmStatic
    fun onRequestPermissionsResult(
        requestCode: Int,
        grantResults: IntArray,
        listener: IPermissionAskInterface?
    ) {
        when (requestCode) {
            REQUEST_ACCESS_FINE_LOCATION -> {

                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    listener?.onPermissionGranted()
                } else {
                    listener?.onPermissionDenied()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }
}
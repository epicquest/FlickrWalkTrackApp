package com.epicqueststudios.flickrwalktrackapp.features.permission

import android.content.Context
import android.content.Context.MODE_PRIVATE

object PreferencesUtils {
    private val PREFS_FILE_NAME = "PREFS_FILE_NAME"

    @JvmStatic
    fun firstTimeAskingPermission(context: Context, permission: String, isFirstTime: Boolean) {
        val sharedPreference = context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE)
        sharedPreference.edit().putBoolean(permission, isFirstTime).apply()
    }

    @JvmStatic
    fun isFirstTimeAskingPermission(context: Context, permission: String): Boolean {
        return context.getSharedPreferences(PREFS_FILE_NAME, MODE_PRIVATE).getBoolean(permission, true)
    }
}

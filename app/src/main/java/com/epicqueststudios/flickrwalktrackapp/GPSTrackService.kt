package com.epicqueststudios.flickrwalktrackapp

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import com.epicqueststudios.FlickrWalkApplication
import com.epicqueststudios.flickrwalktrackapp.database.LocationDatabase
import com.epicqueststudios.flickrwalktrackapp.database.entity.LocationEntity
import com.epicqueststudios.flickrwalktrackapp.interfaces.LocationAddedCallback
import com.epicqueststudios.flickrwalktrackapp.ui.main.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import org.jetbrains.anko.doAsync

import javax.inject.Inject

class GPSTrackService : Service() {
    companion object {
        const val TAG = "GPSTrackService"
        const val LOCATION_INTERVAL = 500
        const val LOCATION_DISTANCE = 10
        const val NOTIFICATION_ID = 1001
        const val CHANNEL_ID = "channel01"
        const val CHANNEL_NAME = "Track channel"
    }

    private var callback: LocationAddedCallback? = null
    private var running: Boolean = false
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private val binder = LocationServiceBinder()

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var locationRequest: LocationRequest

    private val notification: Notification
        @RequiresApi(Build.VERSION_CODES.O)
        get() {

            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)

            val builder = Notification.Builder(applicationContext, CHANNEL_ID).setAutoCancel(true)
            return builder.build()
        }

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    private fun injectDependencies() {
        (application as FlickrWalkApplication).appComponent.inject(this)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_NOT_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        injectDependencies()
        startForeground(NOTIFICATION_ID, notification)
    }

    override fun onDestroy() {
        stopTracking()
        super.onDestroy()
    }

    private fun stopLocationUpdates() {
        if (::locationCallback.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
        running = false
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        running = true
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    fun startTracking() {
        initialiseGps()
        startLocationUpdates()
    }

    fun stopTracking() {
        stopLocationUpdates()
        val notificationManager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }

    private fun initialiseGps() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                doAsync {
                    if (locationResult.locations.isNotEmpty()) {
                        val location = locationResult.locations[0]
                        val distance: Float = lastLocation?.distanceTo(location) ?: Constants.MINIMUM_DISTANCE
                        Log.d(MainActivity.TAG, "New location arrived: $location. Distance from previous: $distance")
                        if (distance >= Constants.MINIMUM_DISTANCE) {
                            lastLocation = location
                            LocationDatabase.getInstance(applicationContext).locationDao().insert(
                                LocationEntity(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                            callback?.onLocation(location)
                        }
                    }
                }

            }
        }
    }

    fun setCallback(callback: LocationAddedCallback?) {
        this.callback = callback
    }

    fun isRunning(): Boolean = running

    inner class LocationServiceBinder : Binder() {
        val service: GPSTrackService
            get() = this@GPSTrackService
    }
}

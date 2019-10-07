package com.epicqueststudios.flickrwalktrackapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epicqueststudios.FlickrWalkApplication
import com.epicqueststudios.flickrwalktrackapp.GPSTrackService
import com.epicqueststudios.flickrwalktrackapp.R
import com.epicqueststudios.flickrwalktrackapp.database.LocationDatabase
import com.epicqueststudios.flickrwalktrackapp.features.permission.IPermissionAskInterface
import com.epicqueststudios.flickrwalktrackapp.features.permission.PermissionUtils
import com.epicqueststudios.flickrwalktrackapp.features.permission.PermissionUtils.REQUEST_ACCESS_FINE_LOCATION
import com.epicqueststudios.flickrwalktrackapp.interfaces.LocationAddedCallback
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhoto
import com.epicqueststudios.flickrwalktrackapp.ui.adapters.ImageAdapter
import com.epicqueststudios.flickrwalktrackapp.utils.Utils
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainViewContract.View {
    private var gpsService: GPSTrackService? = null
    private var permissionActions: IPermissionAskInterface? = null
    private lateinit var adapter: ImageAdapter
    private val flickerPhotos: MutableList<FlickrPhoto> = mutableListOf()

    @Inject
    lateinit var presenter: MainActivityPresenter

    companion object {
        const val TAG = "flickrWalkTrack"
    }

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectDependencies()

        initRecyclerView()
        initButtons()
    }


    override fun onResume() {
        super.onResume()
        presenter.attach(this)
        bindGPSService()
        presenter.loadData(LocationDatabase.getInstance(applicationContext))
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
    }

    override fun onDestroy() {
        if (gpsService?.isRunning() == false) {
            val intent = Intent(applicationContext, GPSTrackService::class.java)
            applicationContext.stopService(intent)
        }
        super.onDestroy()
    }

    private fun bindGPSService() {
        val intent = Intent(this.application, GPSTrackService::class.java)
        application.startService(intent)
        application.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
    }

    private fun initButtons() {
        btn_action.setOnClickListener {
            requestPermission()
        }
        btn_clear.setOnClickListener {
            clearData()
        }
    }

    private fun injectDependencies() {
        (application as FlickrWalkApplication).appComponent.inject(this)
    }

    private fun requestPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            doAction()
        } else {
            requestGPSPermission()
        }
    }

    private fun requestGPSPermission() {
        permissionActions = object : IPermissionAskInterface {
            override fun onPermissionDenied() {
                Toast.makeText(this@MainActivity, getString(R.string.permission_required), Toast.LENGTH_LONG).show()
            }

            override fun onPermissionAsk() {
                ActivityCompat.requestPermissions(
                    this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    REQUEST_ACCESS_FINE_LOCATION
                )
            }

            override fun onPermissionPreviouslyDenied() {
                // Do nothing
            }

            override fun onPermissionDisabled() {
                finish()
            }

            override fun onPermissionGranted() {
                doAction()
            }
        }
        permissionActions?.let {
            PermissionUtils.checkPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, it)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        PermissionUtils.onRequestPermissionsResult(requestCode, grantResults, permissionActions)
    }

    private fun initRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = linearLayoutManager
        adapter = ImageAdapter(flickerPhotos)
        recycler_view.adapter = adapter
    }

    private fun clearData() {
        presenter.clearData(LocationDatabase.getInstance(applicationContext))
        adapter.clearImages()
    }

    private fun updateButtonText(running: Boolean) {
        btn_action.text = if (!running) getString(R.string.start) else getString(R.string.stop)
    }

    @SuppressLint("MissingPermission")
    fun doAction() {
        gpsService?.let {
            if (it.isRunning()) {
                stopUpdates()
            } else {
                startUpdates()
            }
        }
    }

    private fun startUpdates() {
        if (!Utils.isInternetAvailable(this)) {
            Toast.makeText(this@MainActivity, getString(R.string.internet_is_not_available), Toast.LENGTH_LONG).show()
            return
        }
        if (!Utils.isGPSEnabled(this)) {
            Toast.makeText(this@MainActivity, getString(R.string.gps_is_not_available), Toast.LENGTH_LONG).show()
            return
        }

        gpsService?.startTracking()
        updateButtonText(true)
    }

    private fun stopUpdates() {
        updateButtonText(false)
        gpsService?.stopTracking()
    }

    override fun populatePhoto(photo: FlickrPhoto) {
        adapter.addImage(photo)
    }

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val name = className.className
            if (name == GPSTrackService::class.java.name) {
                gpsService = (service as GPSTrackService.LocationServiceBinder).service
                btn_action.isEnabled = true
                gpsService?.let {
                    updateButtonText(it.isRunning())

                    it.setCallback(object : LocationAddedCallback {
                        override fun onLocation(location: Location) {
                            if (presenter.isAttached()) {
                                presenter.downloadPhotoForLocation(
                                    location.latitude,
                                    location.longitude
                                )
                            }
                        }
                    })
                }
            }
        }

        override fun onServiceDisconnected(className: ComponentName) {
            if (className.className == GPSTrackService::class.java.name) {
                btn_action.isEnabled = false
                gpsService?.setCallback(null)
                gpsService = null

            }
        }
    }
}
package com.epicqueststudios.flickrwalktrackapp.ui.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epicqueststudios.FlickrWalkApplication
import com.epicqueststudios.flickrwalktrackapp.Constants
import com.epicqueststudios.flickrwalktrackapp.R
import com.epicqueststudios.flickrwalktrackapp.features.permission.IPermissionAskInterface
import com.epicqueststudios.flickrwalktrackapp.features.permission.PermissionUtils
import com.epicqueststudios.flickrwalktrackapp.features.permission.PermissionUtils.REQUEST_ACCESS_FINE_LOCATION
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhoto
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhotosResponseModel
import com.epicqueststudios.flickrwalktrackapp.network.FlickrService
import com.epicqueststudios.flickrwalktrackapp.ui.adapters.ImageAdapter
import com.epicqueststudios.flickrwalktrackapp.utils.Utils
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

class MainActivity : AppCompatActivity(), MainViewContract.View {
    // warnings, check ne,gps, analitics
    private var requestingLocationUpdates: Boolean = false
    private lateinit var locationCallback: LocationCallback
    private var lastLocation: Location? = null
    private var permissionActions: IPermissionAskInterface? = null
    private lateinit var adapter: ImageAdapter
    private val flickerPhotos: MutableList<FlickrPhoto> = mutableListOf()

    @Inject
    lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var locationRequest: LocationRequest

    @Inject
    lateinit var flickrService: FlickrService

    lateinit var presenter: MainActivityPresenter

    private var running = false

    companion object {
        const val TAG = "flickrWalkTrack"
    }

    private lateinit var linearLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        injectDependencies()

        presenter = MainActivityPresenter(flickrService)

        initialiseRecyclerView()
        initializeButtons()
        initialiseGps()
        updateValuesFromBundle(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        presenter.attach(this)
        if (requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        presenter.detach()
        stopLocationUpdates()
    }

    private fun initializeButtons() {
        btn_action.setOnClickListener {
            requestPermission()
        }
    }

    private fun injectDependencies() {
        (application as FlickrWalkApplication).appComponent.inject(this)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        if (savedInstanceState.keySet().contains(Constants.REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                Constants.REQUESTING_LOCATION_UPDATES_KEY
            )
        }
        if (requestingLocationUpdates) {
            updateButtonText(false)
        }
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

    private fun initialiseGps() {
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                if (locationResult.locations.isNotEmpty()) {
                    val location = locationResult.locations[0]
                    val distance: Float = lastLocation?.distanceTo(location) ?: Constants.MINIMUM_DISTANCE
                    Log.d(TAG, "New location arrived: $location. Distance from previous: $distance")
                    if (distance >= Constants.MINIMUM_DISTANCE) {
                        lastLocation = location
                        presenter.downloadPhotoForLocation(
                            location.latitude,
                            location.longitude
                        )
                    }
                }
            }
        }
    }

    private fun initialiseRecyclerView() {
        linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        recycler_view.layoutManager = linearLayoutManager
        adapter = ImageAdapter(flickerPhotos)
        recycler_view.adapter = adapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(Constants.REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            null
        )
    }

    @SuppressLint("MissingPermission")
    fun doAction() {
        if (running) {
            stopUpdates()
        } else {
            startUpdates()
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
        updateButtonText(false)
        running = true
        startLocationUpdates()
    }

    private fun stopUpdates() {
        updateButtonText(true)
        stopLocationUpdates()
        running = false
    }

    private fun updateButtonText(running: Boolean) {
        btn_action.text = if (running) getString(R.string.start) else getString(R.string.stop)
    }

    override fun populatePhotos(model: FlickrPhotosResponseModel?) {
        model?.let {
            if (it.flickrPhotos.flickrPhoto.isNotEmpty()) {
                adapter.addImage(model.flickrPhotos.flickrPhoto[0])
            }
        }
    }
}
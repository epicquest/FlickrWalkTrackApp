package com.epicqueststudios.flickrwalktrackapp.ui.main

import android.util.Log
import com.epicqueststudios.flickrwalktrackapp.base.BaseContract
import com.epicqueststudios.flickrwalktrackapp.database.LocationDatabase
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhotosResponseModel
import com.epicqueststudios.flickrwalktrackapp.network.FlickrService
import io.reactivex.Single
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.jetbrains.anko.doAsync
import retrofit2.Response
import javax.inject.Inject


class MainActivityPresenter @Inject constructor(val flickrService: FlickrService) :
    BaseContract.Presenter<MainViewContract.View> {

    override fun isAttached(): Boolean = view != null

    private var view: MainViewContract.View? = null

    override fun attach(view: MainViewContract.View) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }


    fun downloadPhotoForLocation(latitude: Double, longitude: Double) {
        val single = flickrService.photosForLocation(
            lat = latitude,
            lon = longitude
        )
        download(single, PhotoObserver())
    }

    fun download(
        single: Single<Response<FlickrPhotosResponseModel>>,
        observer: SingleObserver<Response<FlickrPhotosResponseModel>>
    ) {
        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(observer)
    }

    private fun populatePhoto(model: FlickrPhotosResponseModel?) {
        if (model != null && model.flickrPhotos.photos.isNotEmpty()) {
            view?.populatePhoto(model.flickrPhotos.photos[0])
        }
    }

    fun loadData(database: LocationDatabase) {
        doAsync {
            val locations = database.locationDao().all
            for (location in locations) {
                location.latitude?.let { lat ->
                    location.longitude?.let { lon ->
                        downloadPhotoForLocation(lat, lon)
                    }
                }
            }
        }
    }

    fun clearData(database: LocationDatabase) {
        doAsync { database.clearAllTables() }
    }

    inner class PhotoObserver : SingleObserver<Response<FlickrPhotosResponseModel>> {
        override fun onSuccess(t: Response<FlickrPhotosResponseModel>) {
            if (t.isSuccessful) {
                populatePhoto(t.body())
            } else {
                val error = t.errorBody()
                error?.let {
                    Log.e(TAG, it.toString())
                }
            }
        }

        override fun onSubscribe(d: Disposable) {
            // Do nothing
        }

        override fun onError(e: Throwable) {
            Log.e(TAG, e.message, e)
        }

    }

    companion object {
        const val TAG = "MainActivityPresenter"
    }
}
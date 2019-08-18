package com.epicqueststudios.flickrwalktrackapp.ui.main

import android.util.Log
import com.epicqueststudios.flickrwalktrackapp.Constants
import com.epicqueststudios.flickrwalktrackapp.base.BaseContract
import com.epicqueststudios.flickrwalktrackapp.model.FlickrPhotosResponseModel
import com.epicqueststudios.flickrwalktrackapp.network.FlickrService
import io.reactivex.SingleObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import retrofit2.Response

class MainActivityPresenter(val flickrService: FlickrService) : BaseContract.Presenter<MainViewContract.View> {
    private var view: MainViewContract.View? = null

    override fun attach(view: MainViewContract.View) {
        this.view = view
    }

    override fun detach() {
        this.view = null
    }

    fun downloadPhotoForLocation(latitude: Double, longitude: Double) {
        val single = flickrService.photosForLocation(
            Constants.FLICKR_METHOD,
            Constants.API_KEY,
            latitude,
            longitude,
            "json", Constants.RADIUS,
            Constants.PER_PAGE,
            1
        )

        single.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : SingleObserver<Response<FlickrPhotosResponseModel>> {
                override fun onSuccess(t: Response<FlickrPhotosResponseModel>) {
                    if (t.isSuccessful) {
                        view?.populatePhotos(t.body())
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
            })
    }

    companion object {
        const val TAG = "MainActivityPresenter"
    }
}
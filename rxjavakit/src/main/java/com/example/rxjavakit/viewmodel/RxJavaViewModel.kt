package com.example.rxjavakit.viewmodel

import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.kitinterface.MovieProtocol.Item
import com.example.rxjavakit.extension.asLiveData
import com.example.rxjavakit.repository.RxJavaRepository
import com.example.rxjavakit.rest.MovieWebServiceRxJava
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RxJavaViewModel(application: Application) : KitViewModel(application) {

    override val repository: RxJavaRepository = RxJavaRepository(
        MovieWebServiceRxJava.service,
        MovieDatabase.getInstance(application.applicationContext).movieDao
    )

    private val disposableBag = CompositeDisposable()

    private var locationDisposable: Disposable? = null
    private val locationFlowable: Flowable<Location> = Flowable.create({ emitter ->

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                emitter.onNext(result.locations[0])
            }

            override fun onLocationAvailability(availability: LocationAvailability) {
                Log.d(LOG_TAG, "Is the location available ? ${availability.isLocationAvailable}")
            }
        }

        locationServiceClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper())

        emitter.setCancellable { locationServiceClient.removeLocationUpdates(callback) }
    }, BackpressureStrategy.LATEST)


    init {
        fetchTrendingMovies()
    }

    override fun getMovies(): LiveData<List<Item>> = repository.movies
        .map { movies: List<MovieEntity> ->

            // Build a list according to our UI protocol
            val list: MutableList<Item> = movies.map { Item.MovieItem(it) }.toMutableList()
            list.add(Item.FooterItem("Thanks to TMDB API for the movie data."))

            return@map list as List<Item>
        }
        .doOnError { Log.e(LOG_TAG, "Error fetching movies.", it) }
        .doOnComplete { Log.d(LOG_TAG, "Flowable completed.") }
        .subscribeOn(Schedulers.io())
        .asLiveData()

    override fun fetchTrendingMovies() {

        val disposable = repository.fetchTrendingMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.value = true }
            .doOnTerminate { isLoading.value = false }
            .subscribe({
                Log.d(LOG_TAG, "Fetched movies.")
            }, { throwable ->
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies", throwable)
            })

        disposableBag.add(disposable)
    }

    override fun startUpdatesForLocalMovies() {

        locationDisposable?.dispose()
        locationDisposable = locationFlowable
            .observeOn(Schedulers.io())
            .flatMapCompletable { location ->
                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                val countryCode = addresses.first().countryCode
                repository.fetchMoviesNowPlaying(countryCode).doOnComplete {
                    isLoading.postValue(false)
                    isLocalMovies.postValue(true)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.value = true }
            .subscribe({}, {
                message.value = "Could not load local movies. Check your connection and GPS."
                isLoading.value = false
                cancelUpdateForLocalMovies()
            })

        disposableBag.add(locationDisposable!!)
    }

    override fun cancelUpdateForLocalMovies() {
        locationDisposable?.dispose()
        isLocalMovies.value = false
        fetchTrendingMovies()
    }

    override fun onCleared() {
        disposableBag.dispose()
        super.onCleared()
    }
}


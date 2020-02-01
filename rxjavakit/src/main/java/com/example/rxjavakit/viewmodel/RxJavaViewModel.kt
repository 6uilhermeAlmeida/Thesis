package com.example.rxjavakit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.kitinterface.MovieProtocol.Item
import com.example.rxjavakit.extension.asLiveData
import com.example.rxjavakit.location.getLocationFlowable
import com.example.rxjavakit.repository.RxJavaRepository
import com.example.rxjavakit.rest.MovieWebServiceRxJava
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

        disposableBag += repository.fetchTrendingMovies()
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
    }

    override fun startUpdatesForLocalMovies() {

        locationDisposable?.dispose()
        locationDisposable = getLocationFlowable(locationServiceClient, locationRequest)
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
            .subscribe(
                { Log.d(LOG_TAG, "Local movies fetch terminated.") },
                { handleLocalMoviesError(it) })
            .also { disposableBag += it }
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

private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
    this.add(disposable)
}


package com.example.rxjavakit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.protocol.KitViewModel
import com.example.kitprotocol.protocol.MovieProtocol.Item
import com.example.rxjavakit.R
import com.example.rxjavakit.extension.asLiveData
import com.example.rxjavakit.location.getAddressesSingle
import com.example.rxjavakit.location.getLocationFlowable
import com.example.rxjavakit.repository.RxJavaRepository
import com.example.rxjavakit.rest.MovieWebServiceRxJava
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class RxJavaViewModel(application: Application) : KitViewModel(application) {

    private val repository: RxJavaRepository = RxJavaRepository(
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
            return@map movies.map { Item.MovieItem(it) } + Item.FooterItem(context.getString(R.string.thanks_tmdb))
        }
        .subscribeOn(Schedulers.io())
        .asLiveData()

    override fun fetchTrendingMovies() {

        disposableBag += repository.fetchTrendingMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.value = true }
            .doOnTerminate { isLoading.value = false }
            .subscribe(
                { isLocalMovies.value = false },
                { throwable ->
                    message.value = context.getString(R.string.generic_movie_error)
                    Log.e(LOG_TAG, "Could not fetch movies", throwable)
                }
            )
    }

    override fun startUpdatesForLocalMovies() {

        locationDisposable?.dispose()
        locationDisposable = getLocationFlowable(locationServiceClient, locationRequest)
            .subscribeOn(Schedulers.io())
            .observeOn(Schedulers.io())
            .flatMapSingle { location -> getAddressesSingle(addressRepository, location, 1) }
            .flatMapCompletable { addresses ->
                val countryCode = addresses.first().countryCode
                repository.fetchMoviesNowPlaying(countryCode).doOnComplete {
                    isLoading.postValue(false)
                    isLocalMovies.postValue(true)
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.value = true }
            .subscribe({}, { handleLocalMoviesError(it) })
            .also { disposableBag += it }
    }

    override fun cancelUpdateForLocalMovies() {
        locationDisposable?.dispose()
    }

    private operator fun CompositeDisposable.plusAssign(disposable: Disposable) {
        this.add(disposable)
    }

    override fun onCleared() {
        disposableBag.dispose()
        super.onCleared()
    }
}


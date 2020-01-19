package com.example.rxjavakit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.rxjavakit.repository.RxJavaRepository
import com.example.rxjavakit.rest.MovieWebServiceRxJava
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers

class RxJavaViewModel(application: Application) : KitViewModel(application) {

    override val repository: RxJavaRepository = RxJavaRepository(
        MovieWebServiceRxJava.service,
        MovieDatabase.getInstance(application.applicationContext).movieDao
    )

    private val disposableBag = CompositeDisposable()

    init {
        fetchTrendingMovies()
    }

    override fun fetchTrendingMovies() {

        val disposable = repository.fetchMovies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { isLoading.value = true }
            .doOnComplete { isLoading.value = false }
            .subscribe({
                Log.d(LOG_TAG, "Fetched movies.")
            }, { throwable ->
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies", throwable)
            })

        disposableBag.add(disposable)
    }

    override fun fetchMoviesForCurrentLocation() {

    }

    override fun onCleared() {
        disposableBag.dispose()
        super.onCleared()
    }

}

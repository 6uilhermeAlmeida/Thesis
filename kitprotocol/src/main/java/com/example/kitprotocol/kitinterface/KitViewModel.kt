package com.example.kitprotocol.kitinterface

import android.app.Application
import android.location.Geocoder
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kitprotocol.rest.model.Movie
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

abstract class KitViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val LOG_TAG = "ViewModel"
    }

    protected abstract val repository: KitRepository

    protected val locationServiceClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }

    protected val geocoder by lazy { Geocoder(application.applicationContext) }

    protected val message by lazy { MutableLiveData<String?>() }
    protected val isLoading by lazy { MutableLiveData<Boolean>().apply { value = false } }
    protected val moviesForLocation by lazy { MutableLiveData<List<Movie>>() }


    fun getMessage(): LiveData<String?> = message
    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getMoviesForLocation(): LiveData<List<Movie>> = moviesForLocation
    fun getTrendingMovies() = repository.movies

    fun resetMessage() {
        message.value = null
    }

    abstract fun fetchTrendingMovies()
    abstract fun fetchMoviesForCurrentLocation()
}
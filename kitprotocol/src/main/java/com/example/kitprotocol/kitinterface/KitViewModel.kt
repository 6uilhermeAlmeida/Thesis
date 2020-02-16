package com.example.kitprotocol.kitinterface

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kitprotocol.R
import com.example.kitprotocol.location.AddressRepository
import com.example.kitprotocol.throwable.LocationProviderNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

abstract class KitViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val LOG_TAG = "ViewModel"
        const val ONE_MINUTE = 60000L
    }

    protected val locationServiceClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application.applicationContext)
    }

    protected val locationRequest by lazy {
        LocationRequest().apply {
            interval = ONE_MINUTE
            fastestInterval = ONE_MINUTE
        }
    }

    protected val message by lazy { MutableLiveData<String?>() }
    protected val isLoading by lazy { MutableLiveData<Boolean>().apply { value = false } }
    protected val isLocalMovies by lazy { MutableLiveData<Boolean>().apply { value = false } }
    protected val addressRepository by lazy { AddressRepository(application) }

    fun getMessage(): LiveData<String?> = message
    fun getIsLoading(): LiveData<Boolean> = isLoading
    fun getIsLocalMovies(): LiveData<Boolean> = isLocalMovies

    abstract fun getMovies(): LiveData<List<MovieProtocol.Item>>
    protected abstract fun fetchTrendingMovies()
    protected abstract fun startUpdatesForLocalMovies()
    protected abstract fun cancelUpdateForLocalMovies()


    fun resetMessage() {
        message.value = null
    }

    fun onLocalMoviesClick() {
        if (isLocalMovies.value == false) {
            startUpdatesForLocalMovies()
        } else {
            cancelUpdateForLocalMovies()
            fetchTrendingMovies()
        }
    }

    fun onRefresh() {
        if (isLocalMovies.value == false) {
            fetchTrendingMovies()
        } else {
            startUpdatesForLocalMovies()
        }
    }

    fun onLocationPermissionDeniedIndefinitely() {
        message.value = getApplication<Application>().getString(R.string.location_information_permission_denied)
    }

    protected fun handleLocalMoviesError(it: Throwable?) {

        message.value = if (it is LocationProviderNotAvailableException) {
            "It seems like your location provider is turned off."
        } else {
            "Could not load local movies. Check your connection."
        }

        isLoading.value = false
        isLocalMovies.value = false
        cancelUpdateForLocalMovies()
        fetchTrendingMovies()
    }
}
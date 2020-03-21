package com.example.kitprotocol.protocol

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.kitprotocol.R
import com.example.kitprotocol.location.AddressRepository
import com.example.kitprotocol.throwable.LocationProviderNotAvailableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices

abstract class KitViewModel(application: Application, mock: Boolean) : AndroidViewModel(application), MovieTimer {

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

    protected val context: Context
        get() = getApplication()

    override var localRunNumber: Int? = null
    override var trendingRunNumber: Int? = null
    override var startTrending: Long = 0
    override var startLocal: Long = 0

    protected val message by lazy { MutableLiveData<String?>() }
    protected val isLoading by lazy { MutableLiveData<Boolean>().apply { value = false } }
    protected val isLocalMovies by lazy { MutableLiveData<Boolean>().apply { value = false } }
    protected val addressRepository by lazy { AddressRepository(application, mock) }

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

    fun onRefresh() = if (isLocalMovies.value == false) fetchTrendingMovies() else startUpdatesForLocalMovies()

    fun onLocationPermissionDeniedIndefinitely() {
        message.value = getApplication<Application>().getString(R.string.location_information_permission_denied)
    }

    protected fun handleLocalMoviesError(it: Throwable?) {
        stopLocalMoviesTimer()
        message.value = if (it is LocationProviderNotAvailableException) {
            context.getString(R.string.location_provider_off)
        } else {
            context.getString(R.string.generic_local_movies_error)
        }

        isLoading.value = false
        isLocalMovies.value = false
        cancelUpdateForLocalMovies()
        fetchTrendingMovies()
    }
}

class KitViewModelFactory(private val application: Application, private val mock: Boolean) :
    ViewModelProvider.AndroidViewModelFactory(application) {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(Application::class.java, Boolean::class.java).newInstance(application, mock)
    }
}
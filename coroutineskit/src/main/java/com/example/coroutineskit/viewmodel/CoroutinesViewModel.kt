package com.example.coroutineskit.viewmodel

import android.app.Application
import android.location.Location
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.extension.suspend
import com.example.kitprotocol.kitinterface.KitViewModel
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CoroutinesViewModel(application: Application) : KitViewModel(application) {

    override val repository: CoroutinesRepository = CoroutinesRepository(
        MovieWebServiceCoroutines.service,
        MovieDatabase.getInstance(application.applicationContext).movieDao
    )

    private val locationFlow: Flow<Location?> = callbackFlow {

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                offer(result?.locations?.get(0))
                super.onLocationResult(result)
            }

        }

        locationServiceClient.requestLocationUpdates(
            LocationRequest().setInterval(10000)
                .setSmallestDisplacement(20f), callback, Looper.getMainLooper()
        )

        awaitClose { locationServiceClient.removeLocationUpdates(callback) }
    }

    init {
        fetchTrendingMovies()
        viewModelScope.launch {

            locationFlow.collect {
                val location = it ?: throw IllegalArgumentException("Location is null")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val countryCode = addresses.first().countryCode
                val movies = repository.fetchMoviesNowPlaying(countryCode).results

                Log.d(LOG_TAG, "Fetched ${movies.size} movies for $countryCode")

                moviesForLocation.postValue(movies)
            }

        }
    }

    override fun fetchTrendingMovies() {

        viewModelScope.launch {

            isLoading.value = true
            try {
                repository.fetchTrendingMovies()
            } catch (t: Throwable) {
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies", t)
            }
            isLoading.value = false
        }
    }

    override fun fetchMoviesForCurrentLocation() {

        viewModelScope.launch {

            try {

                withContext(Dispatchers.IO) {

                    val location = locationServiceClient.lastLocation.suspend()

                }
            } catch (t: Throwable) {
                message.value = "Could not fetch movies for your region."
                Log.e(LOG_TAG, "Could not fetch movies for your region.", t)
            }
        }
    }
    override fun getTrendingMovies(): LiveData<List<MovieEntity>> = repository.movies.asLiveData()
}
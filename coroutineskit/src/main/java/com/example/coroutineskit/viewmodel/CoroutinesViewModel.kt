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
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.extension.suspend
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.kitinterface.MovieProtocol.Item
import com.google.android.gms.location.LocationAvailability
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CoroutinesViewModel(application: Application) : KitViewModel(application) {

    override val repository: CoroutinesRepository = CoroutinesRepository(
        MovieWebServiceCoroutines.service,
        MovieDatabase.getInstance(application.applicationContext).movieDao
    )

    private var locationJob: Job? = null

    private val locationFlow: Flow<Location?> = callbackFlow {

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                offer(result?.locations?.get(0))
            }

            override fun onLocationAvailability(availability: LocationAvailability?) {
                Log.d(LOG_TAG, "Is the location available ? ${availability?.isLocationAvailable}")
                if (availability?.isLocationAvailable == false) close(IllegalStateException("No gps."))
            }

        }

        locationServiceClient.requestLocationUpdates(locationRequest, callback, Looper.getMainLooper()).suspend()

        awaitClose {
            locationServiceClient.removeLocationUpdates(callback)
            Log.d(LOG_TAG, "Closing location API.")
        }
    }

    init {
        fetchTrendingMovies()
    }

    override fun getMovies(): LiveData<List<Item>> = repository.movies
        .map { movies: List<MovieEntity> ->

            // Build a list according to our UI protocol
            val list: MutableList<Item> = movies.map { Item.MovieItem(it) }.toMutableList()
            list.add(Item.FooterItem("Thanks to TMDB API for the movie data."))

            return@map list
        }
        .catch { Log.e(LOG_TAG, "Error fetching movies.", it) }
        .onCompletion { Log.d(LOG_TAG, "Flow completed.") }
        .flowOn(Dispatchers.IO)
        .asLiveData()


    override fun fetchTrendingMovies() {

        viewModelScope.launch {

            isLoading.value = true
            try {
                repository.fetchTrendingMovies()
            } catch (t: Throwable) {
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies.", t)
            }
            isLoading.value = false
        }
    }

    override fun startUpdatesForLocalMovies() {
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            locationFlow
                .onStart { isLoading.postValue(true) }
                .onEach {
                    val location = it ?: throw IllegalArgumentException("Location is null")
                    val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                    val countryCode = addresses.first().countryCode
                    repository.fetchMoviesNowPlaying(countryCode)
                    isLoading.postValue(false)
                    isLocalMovies.postValue(true)
                }
                .catch {
                    Log.d(LOG_TAG, "Error")
                    message.postValue("Could not load local movies. Check your connection and GPS.")
                    isLoading.postValue(false)
                }
                .flowOn(Dispatchers.IO)
                .collect()
        }
    }

    override fun cancelUpdateForLocalMovies() {
        locationJob?.cancel()
        isLocalMovies.value = false
        fetchTrendingMovies()
    }
}
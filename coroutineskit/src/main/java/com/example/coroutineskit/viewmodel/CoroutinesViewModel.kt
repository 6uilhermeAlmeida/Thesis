package com.example.coroutineskit.viewmodel

import android.app.Application
import android.location.Location
import android.os.Looper
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.constant.Constants
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitViewModel
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import com.example.kitprotocol.kitinterface.MovieProtocol.Item
import com.google.android.gms.measurement.module.Analytics
import com.google.firebase.analytics.FirebaseAnalytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch

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
            LocationRequest().setInterval(10000L).setFastestInterval(5000L)
                .setSmallestDisplacement(20f), callback, Looper.getMainLooper()
        )

        awaitClose { locationServiceClient.removeLocationUpdates(callback) }
    }

    init {
        fetchTrendingMovies()
        viewModelScope.launch(Dispatchers.IO) {
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
                repository.fetchMovies()
            } catch (t: Throwable) {
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies.", t)
            }
            isLoading.value = false
        }
    }

    override fun getTrendingMovies(): LiveData<List<Item>> = repository.movies
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

    override fun fetchMoviesForCurrentLocation() {

    }
}
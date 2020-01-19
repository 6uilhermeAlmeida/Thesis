package com.example.coroutineskit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.extension.suspend
import com.example.kitprotocol.kitinterface.KitViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
class CoroutinesViewModel(application: Application) : KitViewModel(application) {

    override val repository: CoroutinesRepository = CoroutinesRepository(
        MovieWebServiceCoroutines.service,
        MovieDatabase.getInstance(application.applicationContext).movieDao
    )

    init {
        fetchTrendingMovies()
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

        viewModelScope.launch(Dispatchers.IO) {

            try {

                val location = locationServiceClient.lastLocation.suspend()
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val countryCode = addresses.first().countryCode
                val movies = repository.fetchMoviesNowPlaying(countryCode).results

                Log.d(LOG_TAG, "Fetched ${movies.size} movies for $countryCode")

                moviesForLocation.value = movies

            } catch (t: Throwable) {
                message.value = "Could not fetch movies for your region."
                Log.e(LOG_TAG, "Could not fetch movies for your region.", t)
            }
        }
    }
}
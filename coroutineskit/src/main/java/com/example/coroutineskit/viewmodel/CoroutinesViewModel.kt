package com.example.coroutineskit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.R
import com.example.coroutineskit.location.getAddresses
import com.example.coroutineskit.location.getLocationUpdates
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.protocol.KitViewModel
import com.example.kitprotocol.protocol.MovieProtocol.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch

class CoroutinesViewModel(application: Application, mock: Boolean) : KitViewModel(application) {

    private val repository: CoroutinesRepository = CoroutinesRepository(
        if (mock) MovieWebServiceCoroutines.mock() else MovieWebServiceCoroutines.service,
        MovieDatabase.getInstance(application.applicationContext).movieDao
    )

    private var locationJob: Job? = null

    init {
        fetchTrendingMovies()
    }

    override fun getMovies(): LiveData<List<Item>> = repository.movies
        .map { movies: List<MovieEntity> ->

            // Build a list according to our UI protocol
            return@map movies.map { Item.MovieItem(it) } + Item.FooterItem(context.getString(R.string.thanks_tmdb))
        }
        .flowOn(Dispatchers.IO)
        .asLiveData()


    override fun fetchTrendingMovies() {
        startTrendingMoviesTimer()
        viewModelScope.launch {
            try {
                isLoading.value = true
                repository.fetchTrendingMovies()
                isLocalMovies.value = false
            } catch (t: Throwable) {
                message.value = context.getString(R.string.generic_movie_error)
                Log.e(LOG_TAG, "Could not fetch movies.", t)
            } finally {
                stopTrendingMoviesTimer()
                isLoading.value = false
            }
        }
    }

    override fun startUpdatesForLocalMovies() {
        startLocalMoviesTimer()
        locationJob?.cancel()
        locationJob = viewModelScope.launch {
            getLocationUpdates(locationServiceClient, locationRequest)
                .onEach { location ->
                    val addresses = getAddresses(addressRepository, location, 1)
                    val countryCode = addresses.first().countryCode
                    repository.fetchMoviesNowPlaying(countryCode)
                }
                .flowOn(Dispatchers.IO)
                .onStart { isLoading.value = true }
                .catch { handleLocalMoviesError(it) }
                .collect {
                    stopLocalMoviesTimer()
                    isLoading.value = false
                    isLocalMovies.value = true
                }
        }
    }

    override fun cancelUpdateForLocalMovies() {
        locationJob?.cancel()
    }
}
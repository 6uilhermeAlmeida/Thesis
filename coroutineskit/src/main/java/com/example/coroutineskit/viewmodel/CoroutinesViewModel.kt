package com.example.coroutineskit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.location.getAddressesSuspending
import com.example.coroutineskit.location.getLocationFlow
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.kitinterface.MovieProtocol.Item
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
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
            try {
                isLoading.value = true
                repository.fetchTrendingMovies()
            } catch (t: Throwable) {
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies.", t)
            } finally {
                isLoading.value = false
            }
        }
    }

    override fun startUpdatesForLocalMovies() {

        locationJob?.cancel()
        locationJob = viewModelScope.launch {

            getLocationFlow(locationServiceClient, locationRequest)
                .onEach { location ->
                    val addresses = getAddressesSuspending(addressRepository, location, 1)
                    val countryCode = addresses.first().countryCode
                    repository.fetchMoviesNowPlaying(countryCode)
                }
                .flowOn(Dispatchers.IO)
                .onStart { isLoading.value = true }
                .catch { handleLocalMoviesError(it) }
                .collect {
                    isLoading.value = false
                    isLocalMovies.value = true
                }
        }
    }

    override fun cancelUpdateForLocalMovies() {
        locationJob?.cancel()
    }
}
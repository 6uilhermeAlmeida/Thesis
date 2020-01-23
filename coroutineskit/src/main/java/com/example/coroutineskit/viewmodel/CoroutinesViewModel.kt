package com.example.coroutineskit.viewmodel

import android.app.Application
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

    private val logToAnalyticsFlow: Flow<Unit> = flow {
        val instance = FirebaseAnalytics.getInstance(application)
        instance.logEvent(Constants.REFRESH_EVENT_KEY, Bundle())
        emit(Unit)
    }.catch {
        Log.e(LOG_TAG, "Error writing to Analytics", it)
        emit(Unit)
    }

    init {
        fetchTrendingMovies()
    }

    override fun fetchTrendingMovies() {
        viewModelScope.launch {
            isLoading.value = true
            try {
                repository.fetchMovies()
            } catch (t: Throwable) {
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies", t)
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
        .catch {
            Log.e(LOG_TAG, "Error fetching movies.", it)
        }
        .onCompletion {
            Log.d(LOG_TAG, "AHAHHAHAHA")
        }
        .flowOn(Dispatchers.IO)
        .asLiveData()
}
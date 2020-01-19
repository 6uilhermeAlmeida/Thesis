package com.example.coroutineskit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import com.example.kitprotocol.kitinterface.KitViewModel
import kotlinx.coroutines.launch

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
                repository.fetchMovies()
            } catch (t: Throwable) {
                message.value = "Could not fetch movies."
                Log.e(LOG_TAG, "Could not fetch movies", t)
            }
            isLoading.value = false
        }
    }
}
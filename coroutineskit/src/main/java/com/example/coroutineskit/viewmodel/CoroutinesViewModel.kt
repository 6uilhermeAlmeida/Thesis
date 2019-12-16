package com.example.coroutineskit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.model.MovieListResponse
import kotlinx.coroutines.launch

class CoroutinesViewModel(application: Application) : KitViewModel(application) {

    private val repository: CoroutinesRepository = CoroutinesRepository()

    override fun getTrendingMovies() {

        viewModelScope.launch {
            when (val response: MovieListResponse = repository.getTrendingMovies()) {
                is MovieListResponse.Success -> _movies.value = response.movies
                is MovieListResponse.Error -> {
                    Log.e(LOG_TAG, "Error fetching movies.", response.reason)
                    _message.value = "Could not fetch movies."
                }
            }
        }
    }
}
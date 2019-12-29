package com.example.coroutineskit.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.model.Movie
import com.example.kitprotocol.model.MovieDetails
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class CoroutinesViewModel(application: Application) : KitViewModel(application) {

    private val repository: CoroutinesRepository = CoroutinesRepository()

    private val genericExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        Log.e(LOG_TAG, "Could not fetch movies", throwable)
        _message.value = "Could not fetch movies."
    }

    override fun getTrendingMovies() {

        viewModelScope.launch(genericExceptionHandler) {

            val trendingMovies: List<Movie> = repository.getTrendingMovies()

            val moviesDetail: List<MovieDetails> = trendingMovies
                .map { async { repository.getMovieDetails(it.id) } }
                .awaitAll()

            _movies.value = moviesDetail
        }
    }
}
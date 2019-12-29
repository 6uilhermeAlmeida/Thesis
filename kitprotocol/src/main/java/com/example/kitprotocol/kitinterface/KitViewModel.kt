package com.example.kitprotocol.kitinterface

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.kitprotocol.model.Movie
import com.example.kitprotocol.model.MovieDetails

abstract class KitViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val LOG_TAG = "ViewModel"
    }

    protected val _movies by lazy { MutableLiveData<List<MovieDetails>>().also { getTrendingMovies() } }
    val movies: LiveData<List<MovieDetails>>
        get() = _movies

    protected val _message by lazy { MutableLiveData<String?>() }
    val message: LiveData<String?>
        get() = _message

    fun resetMessage() {
        _message.value = null
    }

    abstract fun getTrendingMovies()
}
package com.example.kitprotocol.kitinterface

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

abstract class KitViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val LOG_TAG = "ViewModel"
    }

    protected abstract val repository: KitRepository

    protected val message by lazy { MutableLiveData<String?>() }
    protected val isLoading by lazy { MutableLiveData<Boolean>().apply { value = false } }

    fun getMessage(): LiveData<String?> = message
    fun getIsLoading(): LiveData<Boolean> = isLoading

    fun resetMessage() {
        message.value = null
    }

    abstract fun fetchTrendingMovies()
    abstract fun getTrendingMovies(): LiveData<List<MovieProtocol.Item>>
}
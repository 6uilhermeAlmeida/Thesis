package com.example.rxjavakit.extension

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import io.reactivex.Flowable

fun <T> Flowable<T>.asLiveData() : LiveData<T>  = LiveDataReactiveStreams.fromPublisher(this)
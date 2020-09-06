package com.example.kitprotocol.rest.http

import com.example.kitprotocol.mock.MockInterceptor
import okhttp3.Dispatcher
import okhttp3.OkHttpClient

object MovieHttpClient {

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .cache(null)
        .dispatcher(Dispatcher().apply { maxRequestsPerHost = 20 })

    val instance: OkHttpClient by lazy { builder.build() }

    fun mock(): OkHttpClient = builder.addInterceptor(MockInterceptor())
        .build()
}
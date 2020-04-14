package com.example.kitprotocol.rest.http

import com.example.kitprotocol.mock.MockInterceptor
import okhttp3.Dispatcher
import okhttp3.OkHttpClient

object MovieHttpClient {

    private val builder: OkHttpClient.Builder = OkHttpClient.Builder()
        .cache(null)
        .dispatcher(Dispatcher().apply { maxRequestsPerHost = 20 })

    private val instance: OkHttpClient by lazy { builder.build() }

    private val mock: OkHttpClient by lazy {
        builder
            .addInterceptor(MockInterceptor())
            .build()
    }

    fun get() = instance
    fun mock() = mock
}
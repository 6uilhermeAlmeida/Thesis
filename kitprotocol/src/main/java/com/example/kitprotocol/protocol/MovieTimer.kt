package com.example.kitprotocol.protocol

import android.util.Log

interface MovieTimer {

    companion object {
        private const val LOG_TAG = "MovieTimer"
        private fun log(message: String) = Log.i(LOG_TAG, message)
    }

    var trendingRunNumber: Int
    var localRunNumber: Int

    var startTrending: Long
    var startLocal: Long

    fun startTrendingMoviesTimer() {
        trendingRunNumber++
        startTrending = System.currentTimeMillis()
    }

    fun stopTrendingMoviesTimer() {
        log("Trending Run #$trendingRunNumber took ${System.currentTimeMillis() - startTrending} ms.")
    }

    fun startLocalMoviesTimer() {
        localRunNumber++
        startLocal = System.currentTimeMillis()
    }

    fun stopLocalMoviesTimer() {
        log("Local movies Run #$localRunNumber took ${System.currentTimeMillis() - startLocal} ms.")
    }
}
package com.example.benchmark.coroutines.network

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.coroutines.CoroutinesBenchmark
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutinesNetworkBenchmark : CoroutinesBenchmark() {

    @Test
    fun fetch_movies_single_request() = benchmarkRule.measureRepeated {
        runBlocking { remoteSource.getTrendingMovies() }
    }

    @Test
    fun fetch_movies_three_sequential_requests() = benchmarkRule.measureRepeated {
        runBlocking {
            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()
        }
    }

    @Test
    fun fetch_two_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(2) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetch_ten_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(10) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetch_twenty_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(20) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetch_fifty_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(50) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }
}
package com.example.benchmark.rxjava.network

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.rxjava.RxJavaBenchmark
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxJavaNetworkBenchmark : RxJavaBenchmark() {

    @Test
    fun fetch_movies_single_request() = benchmarkRule.measureRepeated {
        remoteSource.getTrendingMovies().blockingGet()
    }

    @Test
    fun fetch_movies_three_sequential_requests() = benchmarkRule.measureRepeated {
        remoteSource.getTrendingMovies()
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { remoteSource.getTrendingMovies() }
            .blockingGet()
    }

    @Test
    fun fetch_two_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(2) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetch_ten_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(10) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetch_twenty_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(20) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetch_fifty_movie_details() = benchmarkRule.measureRepeated {
        val moviesIds = List(50) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }
}
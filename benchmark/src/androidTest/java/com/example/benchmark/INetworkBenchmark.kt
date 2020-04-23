package com.example.benchmark

import org.junit.Test

interface INetworkBenchmark {
    @Test
    fun fetch_movies_single_request()

    @Test
    fun fetch_movies_three_sequential_requests()

    @Test
    fun fetch_two_movie_details()

    @Test
    fun fetch_ten_movie_details()

    @Test
    fun fetch_twenty_movie_details()

    @Test
    fun fetch_fifty_movie_details()
}
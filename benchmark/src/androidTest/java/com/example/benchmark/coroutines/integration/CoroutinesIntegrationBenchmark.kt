package com.example.benchmark.coroutines.integration

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.coroutines.CoroutinesBenchmark
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutinesIntegrationBenchmark : CoroutinesBenchmark() {
    @Test
    fun fetch_movies_from_network_and_insert_in_db() = benchmarkRule.measureRepeated {
        runBlocking { repository.fetchTrendingMovies() }
    }
}
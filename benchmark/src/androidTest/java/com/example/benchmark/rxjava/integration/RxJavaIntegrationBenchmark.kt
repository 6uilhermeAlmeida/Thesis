package com.example.benchmark.rxjava.integration

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.rxjava.RxJavaBenchmark
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxJavaIntegrationBenchmark : RxJavaBenchmark() {

    @Test
    fun fetch_movies_from_network_and_insert_in_db() = benchmarkRule.measureRepeated {
        repository.fetchTrendingMovies().blockingAwait()
    }
}
package com.example.benchmark.coroutines.integration

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.coroutines.CoroutinesBenchmark
import com.example.kitprotocol.transformer.toEntity
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutinesIntegrationBenchmark : CoroutinesBenchmark() {

    @Test
    fun integration_1() = benchmarkRule.measureRepeated {
        runBlocking {
            remoteSource.getTrendingMovies()
            val movieDetails = with(repository) { getMoviesDetails(List(20) { it }).awaitAll() }
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
        }
    }

    @Test
    fun integration_2() = benchmarkRule.measureRepeated {

        runBlocking {

            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()

            val movieDetails = with(repository) {
                getMoviesDetails(List(20) { it }).awaitAll()
                getMoviesDetails(List(20) { it }).awaitAll()
            }

            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
        }
    }

    @Test
    fun integration_3() = benchmarkRule.measureRepeated {

        runBlocking {

            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()

            val movieDetails = with(repository) {
                getMoviesDetails(List(20) { it }).awaitAll()
                getMoviesDetails(List(20) { it }).awaitAll()
                getMoviesDetails(List(20) { it }).awaitAll()
            }

            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
        }
    }
}
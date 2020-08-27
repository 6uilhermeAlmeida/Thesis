package com.example.benchmark.coroutines.integration

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.IIntegrationBenchmark
import com.example.benchmark.coroutines.CoroutinesBenchmark
import com.example.kitprotocol.transformer.toEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutinesIntegrationBenchmark : CoroutinesBenchmark(), IIntegrationBenchmark {

    @Test
    override fun integration_1() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {
            remoteSource.getTrendingMovies()
            val movieDetails = with(repository) { getMoviesDetails(List(20) { it }).awaitAll() }
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
        }
    }

    @Test
    override fun integration_2() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
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
    override fun integration_3() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
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

    @Test
    override fun integration_4() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {

            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()
            remoteSource.getTrendingMovies()

            val movieDetails = with(repository) {
                getMoviesDetails(List(20) { it }).awaitAll()
                getMoviesDetails(List(20) { it }).awaitAll()
                getMoviesDetails(List(20) { it }).awaitAll()
                getMoviesDetails(List(20) { it }).awaitAll()
            }

            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
            repository.insertMoviesToDatabase(movieDetails.mapNotNull { it.toEntity() })
        }
    }

    @ExperimentalCoroutinesApi
    @Test
    override fun integration_reactive() = benchmarkRule.measureRepeated {

        val flow = callbackFlow {
            offer(runBlocking { remoteSource.getTrendingMovies() })
            awaitClose { }
        }
            .onEach { remoteSource.getMoviesNowPlayingForRegion("PT") }
            .flowOn(Dispatchers.IO)

        runBlocking { flow.first() }
    }
}
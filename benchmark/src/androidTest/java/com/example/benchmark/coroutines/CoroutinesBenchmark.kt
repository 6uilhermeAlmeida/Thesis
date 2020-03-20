package com.example.benchmark.coroutines

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.benchmark.mock.getMockEntity
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutinesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val database = MovieDatabase.getInstance(context)
    private val localSource = database.movieDao
    private val remoteSource = MovieWebServiceCoroutines.mock()

    private val repository = CoroutinesRepository(remoteSource, localSource)

    @Test
    fun nukeAndInsertInDatabase() = benchmarkRule.measureRepeated {
        runBlocking {
            val listToInsert = List(20) { getMockEntity(it) }
            repository.insertMoviesToDatabase(listToInsert)
        }
    }

    @Test
    fun fetchTwoMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(2) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetchTenMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(10) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetchTwentyMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(20) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetchFiftyMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(50) { 1 }
        runBlocking { with(repository) { getMoviesDetails(moviesIds).awaitAll() } }
    }

    @Test
    fun fetchTrendingMovies() = benchmarkRule.measureRepeated {
        runBlocking { repository.fetchTrendingMovies() }
    }

    @Test
    fun queryAll_TwentyCallsInParallel() = benchmarkRule.measureRepeated {

        runWithTimingDisabled {
            runBlocking {
                val listToInsert = List(20) { getMockEntity(it) }
                repository.insertMoviesToDatabase(listToInsert)
            }
        }

        runBlocking {
            List(20) { async { localSource.allSuspending() } }.awaitAll()
        }
    }
}

package com.example.benchmark.coroutines

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.kitprotocol.db.MovieDatabase
import kotlinx.coroutines.runBlocking
import mock.services.MockedMovieServices
import mock.services.getMockEntity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CoroutinesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val localSource = MovieDatabase.getInstance(context).movieDao
    private val remoteSource = MockedMovieServices.coroutineService

    private val repository = CoroutinesRepository(remoteSource, localSource)

    @Test
    fun nukeAndInsertInDatabase() = benchmarkRule.measureRepeated {
        val listToInsert = List(20) { getMockEntity(it) }
        runBlocking { repository.insertMoviesToDatabase(listToInsert) }
    }

    @Test
    fun fetchMoviesDetails() = benchmarkRule.measureRepeated {
        runBlocking {
            val moviesIds = List(2) { 1 }
            with(repository) { getMoviesDetails(moviesIds) }
        }
    }

    @Test
    fun fetchTrendingMovies() = benchmarkRule.measureRepeated {
        runBlocking { repository.fetchTrendingMovies() }
    }

    @Test
    fun fetchLocalMovies() = benchmarkRule.measureRepeated {
        runBlocking { repository.fetchMoviesNowPlaying("mock") }
    }
}
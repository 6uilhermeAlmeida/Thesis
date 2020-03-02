package com.example.benchmark.rxjava

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kitprotocol.db.MovieDatabase
import com.example.rxjavakit.repository.RxJavaRepository
import mock.services.MockedMovieServices
import mock.services.getMockEntity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxJavaBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val localSource = MovieDatabase.getInstance(context).movieDao
    private val remoteSource = MockedMovieServices.rxJavaService

    private val repository = RxJavaRepository(remoteSource, localSource)

    @Test
    fun nukeAndInsertInDatabase() = benchmarkRule.measureRepeated {
        val listToInsert = List(20) { getMockEntity(it) }
        repository.insertMoviesToDatabase(listToInsert).blockingAwait()
    }

    @Test
    fun fetchMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(2) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetchTrendingMovies() = benchmarkRule.measureRepeated {
        repository.fetchTrendingMovies().blockingAwait()
    }

    @Test
    fun fetchLocalMovies() = benchmarkRule.measureRepeated {
        repository.fetchMoviesNowPlaying("mock").blockingAwait()
    }
}
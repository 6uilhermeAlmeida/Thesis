package com.example.benchmark.rxjava

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.benchmark.mock.getMockEntity
import com.example.kitprotocol.db.MovieDatabase
import com.example.rxjavakit.repository.RxJavaRepository
import com.example.rxjavakit.rest.MovieWebServiceRxJava
import io.reactivex.Single
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxJavaBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val localSource = MovieDatabase.getInstance(context).movieDao
    private val remoteSource = MovieWebServiceRxJava.mock()

    private val repository = RxJavaRepository(remoteSource, localSource)

    @Test
    fun nukeAndInsertInDatabase() = benchmarkRule.measureRepeated {
        val listToInsert = List(20) { getMockEntity(it) }
        repository.insertMoviesToDatabase(listToInsert).blockingAwait()
    }

    @Test
    fun fetchTwoMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(2) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetchTenMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(10) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetchTwentyMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(20) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetchFiftyMoviesDetails() = benchmarkRule.measureRepeated {
        val moviesIds = List(50) { 1 }
        repository.getMoviesDetail(moviesIds).blockingGet()
    }

    @Test
    fun fetchTrendingMovies() = benchmarkRule.measureRepeated {
        repository.fetchTrendingMovies().blockingAwait()
    }

    @Test
    fun queryAll_TwentyCallsInParallel() = benchmarkRule.measureRepeated {

        runWithTimingDisabled {
            val listToInsert = List(20) { getMockEntity(it) }
            repository.insertMoviesToDatabase(listToInsert).blockingAwait()
        }

        val singlesToZip = List(20) { localSource.allBySingle() }
        Single.zip(singlesToZip) { it.toList() }.blockingGet()
    }
}
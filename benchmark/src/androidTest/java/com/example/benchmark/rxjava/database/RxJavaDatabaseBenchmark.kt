package com.example.benchmark.rxjava.database

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.IDatabaseBenchmark
import com.example.benchmark.mock.getMockEntity
import com.example.benchmark.rxjava.RxJavaBenchmark
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RxJavaDatabaseBenchmark : RxJavaBenchmark(), IDatabaseBenchmark {

    /**
     * Inserts
     */

    @Test
    override fun insert_two_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        val entities = List(2) { getMockEntity(it) }
        localSource.insertAsCompletable(entities).blockingAwait()
    }

    @Test
    override fun insert_ten_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        val entities = List(10) { getMockEntity(it) }
        localSource.insertAsCompletable(entities).blockingAwait()
    }

    @Test
    override fun insert_twenty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        val entities = List(20) { getMockEntity(it) }
        localSource.insertAsCompletable(entities).blockingAwait()
    }

    @Test
    override fun insert_fifty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        val entities = List(50) { getMockEntity(it) }
        localSource.insertAsCompletable(entities).blockingAwait()
    }

    @Test
    override fun insert_one_hundred_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        val entities = List(100) { getMockEntity(it) }
        localSource.insertAsCompletable(entities).blockingAwait()
    }

    @Test
    override fun clear_and_insert_twenty_movies() = benchmarkRule.measureRepeated {
        clearAndInsertMovies(20)
    }

    /**
     * Queries
     */

    @Test
    override fun query_twenty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 20) }
        localSource.allBySingle().blockingGet()
    }

    @Test
    override fun query_fifty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 50) }
        localSource.allBySingle().blockingGet()
    }

    @Test
    override fun query_one_hundred_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 100) }
        localSource.allBySingle().blockingGet()
    }

    @Test
    override fun query_twenty_movies_in_parallel() = benchmarkRule.measureRepeated {

        runWithTimingDisabled { clearAndInsertMovies(20) }

        val singlesToZip = List(20) { localSource.allBySingle().subscribeOn(Schedulers.io()) }
        Single.zip(singlesToZip) { it.toList() }.blockingGet()
    }

    @Test
    override fun query_twenty_movies_reactive() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(20) }
        localSource.allByFlowable().blockingFirst()
    }

    private fun clearAndInsertMovies(size: Int) {
        val listToInsert = List(size) { getMockEntity(it) }
        repository.insertMoviesToDatabase(listToInsert).blockingAwait()
    }
}
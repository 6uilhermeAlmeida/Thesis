package com.example.benchmark.coroutines.database

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.IDatabaseBenchmark
import com.example.benchmark.coroutines.CoroutinesBenchmark
import com.example.benchmark.mock.getMockEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class CoroutinesDatabaseBenchmark : CoroutinesBenchmark(), IDatabaseBenchmark {

    /**
     * Inserts
     */

    @Test
    override fun insert_two_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {
            val movieEntities = List(2) { getMockEntity(it) }
            localSource.suspendInsert(movieEntities)
        }
    }

    @Test
    override fun insert_ten_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {
            val movieEntities = List(10) { getMockEntity(it) }
            localSource.suspendInsert(movieEntities)
        }
    }

    @Test
    override fun insert_twenty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {
            val movieEntities = List(20) { getMockEntity(it) }
            localSource.suspendInsert(movieEntities)
        }
    }

    @Test
    override fun insert_fifty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {
            val movieEntities = List(50) { getMockEntity(it) }
            localSource.suspendInsert(movieEntities)
        }
    }

    @Test
    override fun insert_one_hundred_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled {
            runBlocking { localSource.suspendNuke() }
        }
        runBlocking {
            val movieEntities = List(100) { getMockEntity(it) }
            localSource.suspendInsert(movieEntities)
        }
    }

    @Test
    override fun clear_and_insert_twenty_movies() = benchmarkRule.measureRepeated {
        clearAndInsertMovies(size = 20)
    }

    /**
     * Queries
     */

    @Test
    override fun query_twenty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 20) }
        runBlocking { localSource.allSuspending() }
    }

    @Test
    override fun query_fifty_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 50) }
        runBlocking { localSource.allSuspending() }
    }

    @Test
    override fun query_one_hundred_movies() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 100) }
        runBlocking { localSource.allSuspending() }
    }

    @Test
    override fun query_twenty_movies_in_parallel() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 20) }
        runBlocking { List(20) { async { localSource.allSuspending() } }.awaitAll() }
    }

    @Test
    override fun query_twenty_movies_reactive() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { clearAndInsertMovies(size = 20) }
        runBlocking { localSource.allByFlow().first() }
    }

    private fun clearAndInsertMovies(size: Int = 20) {
        runBlocking {
            val listToInsert = List(size) { getMockEntity(it) }
            repository.insertMoviesToDatabase(listToInsert)
        }
    }
}
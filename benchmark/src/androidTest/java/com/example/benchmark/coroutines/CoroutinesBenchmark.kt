package com.example.benchmark.coroutines

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.kitprotocol.db.MovieDatabase
import org.junit.Rule

open class CoroutinesBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    private val database = MovieDatabase.getInstance(context)

    protected val localSource = database.movieDao
    protected val remoteSource = MovieWebServiceCoroutines.mock()
    protected val repository = CoroutinesRepository(remoteSource, localSource)
}

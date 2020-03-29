package com.example.benchmark.rxjava

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.kitprotocol.db.MovieDatabase
import com.example.rxjavakit.repository.RxJavaRepository
import com.example.rxjavakit.rest.MovieWebServiceRxJava
import org.junit.Rule

open class RxJavaBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context: Context = InstrumentationRegistry.getInstrumentation().targetContext
    protected val remoteSource = MovieWebServiceRxJava.mock()
    protected val localSource = MovieDatabase.getInstance(context).movieDao
    protected val repository = RxJavaRepository(remoteSource, localSource)
}
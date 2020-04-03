package com.example.benchmark.rxjava.integration

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.rxjava.RxJavaBenchmark
import com.example.kitprotocol.transformer.toEntity
import io.reactivex.Completable
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxJavaIntegrationBenchmark : RxJavaBenchmark() {

    @Test
    fun integration_1() = benchmarkRule.measureRepeated {
        remoteSource.getTrendingMovies()
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMapCompletable { repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }) }
            .blockingAwait()
    }

    @Test
    fun integration_2() = benchmarkRule.measureRepeated {
        remoteSource.getTrendingMovies()
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMapCompletable {
                repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() })
                    .andThen(repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }))
            }.blockingAwait()
    }

    @Test
    fun integration_3() = benchmarkRule.measureRepeated {
        remoteSource.getTrendingMovies()
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMapCompletable {
                repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() })
                    .andThen(repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }))
                    .andThen(repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }))
                    .andThen(Completable.complete())
            }.blockingAwait()
    }
}
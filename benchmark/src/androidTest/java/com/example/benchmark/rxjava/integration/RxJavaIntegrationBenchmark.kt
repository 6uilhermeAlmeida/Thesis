package com.example.benchmark.rxjava.integration

import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.benchmark.IIntegrationBenchmark
import com.example.benchmark.rxjava.RxJavaBenchmark
import com.example.kitprotocol.rest.model.MovieResponse
import com.example.kitprotocol.transformer.toEntity
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RxJavaIntegrationBenchmark : RxJavaBenchmark(), IIntegrationBenchmark {

    @Test
    override fun integration_1() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        remoteSource.getTrendingMovies()
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMapCompletable { repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }) }
            .blockingAwait()
    }

    @Test
    override fun integration_2() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
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
    override fun integration_3() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
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

    @Test
    override fun integration_4() = benchmarkRule.measureRepeated {
        runWithTimingDisabled { localSource.nukeAsCompletable().blockingAwait() }
        remoteSource.getTrendingMovies()
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { remoteSource.getTrendingMovies() }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMap { repository.getMoviesDetail(List(20) { it }) }
            .flatMapCompletable {
                repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() })
                    .andThen(repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }))
                    .andThen(repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }))
                    .andThen(repository.insertMoviesToDatabase(it.mapNotNull { movieDetails -> movieDetails.toEntity() }))
                    .andThen(Completable.complete())
            }.blockingAwait()
    }

    @Test
    override fun integration_reactive() = benchmarkRule.measureRepeated {

        val flowable = Flowable.create<MovieResponse>({ emitter ->
            emitter.onNext(remoteSource.getTrendingMovies().blockingGet())
        }, BackpressureStrategy.LATEST)
            .flatMapSingle { remoteSource.getMoviesNowPlayingForRegion("PT") }
            .subscribeOn(Schedulers.io())

        flowable.blockingFirst()
    }
}
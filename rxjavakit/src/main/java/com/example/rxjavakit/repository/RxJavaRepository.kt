package com.example.rxjavakit.repository

import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.rest.model.idList
import com.example.kitprotocol.transformer.toEntityList
import com.example.rxjavakit.rest.IMovieWebServiceRxJava
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers

class RxJavaRepository(private val remoteSource: IMovieWebServiceRxJava, private val localSource: MovieDao) {

    val movies: Flowable<List<MovieEntity>>
        get() = localSource.allByFlowable()

    fun fetchTrendingMovies(): Completable {

        return remoteSource.getTrendingMovies() // Get trending movies
            .flatMap { moviesResponse -> getMoviesDetail(moviesResponse.results.idList()) }
            .flatMapCompletable { detailedMovies -> insertMoviesToDatabase(detailedMovies.toEntityList()) }
    }

    fun fetchMoviesNowPlaying(countryCode: String): Completable {

        return remoteSource.getMoviesNowPlayingForRegion(countryCode) // Get movies now playing
            .flatMap { localMoviesResponse -> getMoviesDetail(localMoviesResponse.results.idList()) }
            .flatMapCompletable { detailedMovies -> insertMoviesToDatabase(detailedMovies.toEntityList()) }
    }

    fun getMoviesDetail(moviesIds: List<Int>): Single<List<MovieDetails>> {
        // Get the details for trending movies in parallel
        val singlesToZip = moviesIds.map { remoteSource.getMovieDetails(it).subscribeOn(Schedulers.io()) }
        return Single.zip(singlesToZip) { it.toList() as List<MovieDetails> }
    }

    fun insertMoviesToDatabase(movieEntities: List<MovieEntity>): Completable {
        // Insert in local database
        return Completable.fromAction { localSource.nukeAndInsert(movieEntities) }
    }
}
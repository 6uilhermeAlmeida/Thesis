package com.example.rxjavakit.repository

import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.rest.model.Movie
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.transformer.toEntity
import com.example.rxjavakit.rest.IMovieWebServiceRxJava
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RxJavaRepository(private val remoteSource: IMovieWebServiceRxJava, private val localSource: MovieDao) {

    val movies: Flowable<List<MovieEntity>>
        get() = localSource.allByFlowable()

    fun fetchTrendingMovies(): Completable {

        return remoteSource.getTrendingMovies() // Get trending movies
            .flatMap { moviesResponse -> getMoviesDetail(moviesResponse.results) }
            .flatMapCompletable { detailedMovies -> insertMoviesToDatabase(detailedMovies) }
    }

    fun fetchMoviesNowPlaying(countryCode: String): Completable {

        return remoteSource.getMoviesNowPlayingForRegion(countryCode) // Get movies now playing
            .flatMap { localMoviesResponse -> getMoviesDetail(localMoviesResponse.results) }
            .flatMapCompletable { detailedMovies -> insertMoviesToDatabase(detailedMovies) }
    }

    private fun insertMoviesToDatabase(detailedMovies: List<MovieDetails>): Completable {
        // Insert in local database
        return localSource.nukeAsCompletable()
            .andThen(localSource.insertAllAsCompletable(detailedMovies.mapNotNull { it.toEntity() }))
            .andThen(Completable.complete())
    }

    private fun getMoviesDetail(movieList: List<Movie>): Single<List<MovieDetails>> {
        // Get the details for trending movies in parallel
        val singlesToZip = movieList.map { remoteSource.getMovieDetails(it.id) }
        return Single.zip(singlesToZip) { it.toList() as List<MovieDetails> }
    }
}
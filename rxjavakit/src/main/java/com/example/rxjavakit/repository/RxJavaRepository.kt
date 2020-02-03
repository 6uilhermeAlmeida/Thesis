package com.example.rxjavakit.repository

import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitRepository
import com.example.kitprotocol.rest.model.Movie
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.transformer.toEntity
import com.example.rxjavakit.rest.IMovieWebServiceRxJava
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

class RxJavaRepository(
    private val remoteService: IMovieWebServiceRxJava,
    private val movieDao: MovieDao
) : KitRepository {

    val movies: Flowable<List<MovieEntity>>
        get() = movieDao.allByFlowable()

    fun fetchTrendingMovies(): Completable {

        return remoteService.getTrendingMovies() // Get trending movies
            .flatMap { moviesResponse -> getMoviesDetail(moviesResponse.results) }
            .flatMapCompletable { detailedMovies -> insertMoviesToDatabase(detailedMovies) }
    }

    fun fetchMoviesNowPlaying(countryCode: String): Completable {

        return remoteService.getMoviesNowPlayingForRegion(countryCode) // Get trending movies
            .flatMap { localMoviesResponse -> getMoviesDetail(localMoviesResponse.results) }
            .flatMapCompletable { detailedMovies -> insertMoviesToDatabase(detailedMovies) }
    }

    private fun insertMoviesToDatabase(detailedMovies: List<MovieDetails>): Completable {
        // Insert in local database
        return movieDao.nukeAsCompletable()
            .andThen(movieDao.insertAllAsCompletable(detailedMovies.mapNotNull { it.toEntity() }))
            .andThen(Completable.complete())
    }

    private fun getMoviesDetail(movieList: List<Movie>): Single<List<MovieDetails>> {
        // Get the details for trending movies in parallel
        val singlesToZip = movieList.map { remoteService.getMovieDetails(it.id) }
        return Single.zip(singlesToZip) { it.toList() as List<MovieDetails> }
    }
}
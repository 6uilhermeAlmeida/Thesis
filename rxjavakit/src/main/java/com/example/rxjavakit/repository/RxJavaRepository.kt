package com.example.rxjavakit.repository

import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitRepository
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

    fun fetchMovies(): Completable {

        return remoteService.getTrendingMovies() // Get trending movies
            .flatMap { trendingMoviesResponse ->

                // Get the details for trending movies in parallel
                val trendingMovies = trendingMoviesResponse.results
                val singlesToZip = trendingMovies.map { remoteService.getMovieDetails(it.id) }
                Single.zip(singlesToZip) { it.toList() as List<MovieDetails> }
            }
            .flatMapCompletable { detailedMovies ->

                // Insert in local database
                movieDao.nukeAsCompletable()
                    .andThen(
                        movieDao.insertAllAsCompletable(detailedMovies.mapNotNull { it.toEntity() })
                    ).andThen(Completable.complete())
            }
    }

}
package com.example.coroutineskit.repository

import com.example.coroutineskit.rest.IMovieServiceCoroutines
import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.rest.model.idList
import com.example.kitprotocol.transformer.toEntityList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class CoroutinesRepository(private val remoteSource: IMovieServiceCoroutines, private val localSource: MovieDao) {

    val movies: Flow<List<MovieEntity>>
        get() = localSource.allByFlow()

    suspend fun fetchTrendingMovies() = coroutineScope {

        // Get trending movies
        val trendingMovies = remoteSource.getTrendingMovies().results.idList()
        val detailedMovies = getMoviesDetails(trendingMovies).awaitAll()
        insertMoviesToDatabase(detailedMovies.toEntityList())
    }

    suspend fun fetchMoviesNowPlaying(countryCode: String) = coroutineScope {

        // Get now playing movies
        val nowPlayingMovies = remoteSource.getMoviesNowPlayingForRegion(countryCode).results.idList()
        val detailedMovies = getMoviesDetails(nowPlayingMovies).awaitAll()
        insertMoviesToDatabase(detailedMovies.toEntityList())
    }

    fun CoroutineScope.getMoviesDetails(moviesIds: List<Int>): List<Deferred<MovieDetails>> {
        // Get the details for trending movies in parallel
        return moviesIds.map { async { remoteSource.getMovieDetails(it) } }
    }

    suspend fun insertMoviesToDatabase(movieEntities: List<MovieEntity>) {
        // Insert in local database
        localSource.suspendNukeAndInsert(movieEntities)
    }
}
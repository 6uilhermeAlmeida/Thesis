package com.example.coroutineskit.repository

import android.util.Log
import com.example.coroutineskit.rest.IMovieWebServiceCoroutines
import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitRepository
import com.example.kitprotocol.kitinterface.KitRepository.Companion.LOG_TAG
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.kitprotocol.rest.model.Movie
import com.example.kitprotocol.transformer.toEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow

class CoroutinesRepository(
    private val remoteServiceCoroutines: IMovieWebServiceCoroutines,
    private val movieDao: MovieDao
) : KitRepository {

    val movies: Flow<List<MovieEntity>>
        get() = movieDao.allByFlow()

    suspend fun fetchTrendingMovies() = coroutineScope {

        // Get trending movies
        val trendingMovies: List<Movie> = remoteServiceCoroutines.getTrendingMovies().results

        // Get the details for trending movies in parallel
        val detailedMovies = trendingMovies
            .map { async { remoteServiceCoroutines.getMovieDetails(it.id) } }
            .awaitAll()

        // Insert in local database
        detailedMovies.mapNotNull { it.toEntity() }.let {
            movieDao.fresh(it)
        }
    }

    suspend fun fetchMoviesNowPlaying(countryCode: String) = coroutineScope {

        val movies = remoteServiceCoroutines.getMoviesNowPlayingForRegion(countryCode).results

        Log.d(LOG_TAG, "Fetched ${movies.size} movies for $countryCode")

        // Get the details for trending movies in parallel
        val detailedMovies = movies
            .map { async { remoteServiceCoroutines.getMovieDetails(it.id) } }
            .awaitAll()

        // Insert in local database
        detailedMovies.mapNotNull { it.toEntity() }.let {
            movieDao.fresh(it)
        }

    }
}
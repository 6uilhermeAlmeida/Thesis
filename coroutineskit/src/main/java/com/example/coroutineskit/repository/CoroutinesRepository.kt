package com.example.coroutineskit.repository

import com.example.coroutineskit.rest.IMovieWebServiceCoroutines
import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitRepository
import com.example.kitprotocol.rest.model.Movie
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.transformer.toEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
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
        val detailedMovies = getMoviesDetails(trendingMovies).awaitAll()
        insertMoviesToDatabase(detailedMovies)
    }

    suspend fun fetchMoviesNowPlaying(countryCode: String) = coroutineScope {

        // Get now playing movies
        val nowPlayingMovies = remoteServiceCoroutines.getMoviesNowPlayingForRegion(countryCode).results
        val detailedMovies = getMoviesDetails(nowPlayingMovies).awaitAll()
        insertMoviesToDatabase(detailedMovies)
    }

    private fun CoroutineScope.getMoviesDetails(movieList: List<Movie>): List<Deferred<MovieDetails>> {
        // Get the details for trending movies in parallel
        return movieList.map { async { remoteServiceCoroutines.getMovieDetails(it.id) } }
    }

    private suspend fun insertMoviesToDatabase(detailedMovies: List<MovieDetails>) {
        // Insert in local database
        detailedMovies.mapNotNull { it.toEntity() }.let { movieDao.fresh(it) }
    }
}
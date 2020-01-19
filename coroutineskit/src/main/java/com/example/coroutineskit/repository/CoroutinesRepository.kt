package com.example.coroutineskit.repository

import androidx.lifecycle.LiveData
import com.example.coroutineskit.rest.IMovieWebServiceCoroutines
import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitRepository
import com.example.kitprotocol.rest.model.Movie
import com.example.kitprotocol.transformer.toEntity
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

class CoroutinesRepository(
    private val remoteServiceCoroutines: IMovieWebServiceCoroutines,
    private val movieDao: MovieDao
) :
    KitRepository {

    override val movies: LiveData<List<MovieEntity>>
        get() = movieDao.all()


    suspend fun fetchTrendingMovies() = coroutineScope {

        // Get trending movies
        val trendingMovies: List<Movie> = remoteServiceCoroutines.getTrendingMovies().results

        // Get the details for trending movies in parallel
        val detailedMovies = trendingMovies
            .map { async { remoteServiceCoroutines.getMovieDetails(it.id) } }
            .awaitAll()

        // Insert in local database
        detailedMovies.mapNotNull { it.toEntity() }.let {
            movieDao.nuke()
            movieDao.insertAll(it)
        }
    }

    suspend fun fetchMoviesNowPlaying(countryCode: String) = coroutineScope {
       remoteServiceCoroutines.getMoviesNowPlayingForRegion(countryCode)
    }
}
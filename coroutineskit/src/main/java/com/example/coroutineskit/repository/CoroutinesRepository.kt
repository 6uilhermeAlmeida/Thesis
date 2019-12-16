package com.example.coroutineskit.repository

import com.example.coroutineskit.rest.IMovieWebService
import com.example.coroutineskit.rest.MovieWebService
import com.example.kitprotocol.model.MovieListResponse
import com.example.kitprotocol.model.Movie

class CoroutinesRepository {

    private val remoteService: IMovieWebService = MovieWebService.service

    suspend fun getTrendingMovies(): MovieListResponse {
        return try {
            val movies: List<Movie> = remoteService.getTrendingMovies().results
            MovieListResponse.Success(movies)
        } catch (throwable: Throwable) {
            MovieListResponse.Error(throwable)
        }
    }
}
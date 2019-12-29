package com.example.coroutineskit.repository

import com.example.coroutineskit.rest.IMovieWebService
import com.example.coroutineskit.rest.MovieWebService
import com.example.kitprotocol.model.Movie
import com.example.kitprotocol.model.MovieDetails

class CoroutinesRepository {
    private val remoteService: IMovieWebService = MovieWebService.service
    suspend fun getTrendingMovies(): List<Movie> = remoteService.getTrendingMovies().results
    suspend fun getMovieDetails(movieId: Int): MovieDetails = remoteService.getMovieDetails(movieId)
}
package com.example.kitprotocol.model

sealed class MovieListResponse {
    data class Success(val movies: List<Movie>) : MovieListResponse()
    data class Error(val reason: Throwable) : MovieListResponse()
}

sealed class MovieDetailsResponse {
    data class Success(val details: MovieDetails) : MovieDetailsResponse()
    data class Error(val reason: Throwable) : MovieDetailsResponse()
}

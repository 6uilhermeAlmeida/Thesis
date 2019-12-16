package com.example.kitprotocol.model

sealed class MovieListResponse {
    data class Success(val movies: List<Movie>) : MovieListResponse()
    data class Error(val reason: Throwable) : MovieListResponse()
}

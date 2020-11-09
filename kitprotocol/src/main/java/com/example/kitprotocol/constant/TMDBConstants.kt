package com.example.kitprotocol.constant

object TMDBConstants {

    // TMDB constants
    const val TMDB_API_KEY = "TMDB_API_KEY_HERE"
    const val TMDB_BASE_URL = "https://api.themoviedb.org/3/"
    const val SITE_YOUTUBE: String = "youtube"
    const val TYPE_TRAILER: String = "trailer"

    val APPEND_TO_RESPONSE = listOf("videos")
    fun getImageUrl(specific: String?) = "https://image.tmdb.org/t/p/w500$specific"
}
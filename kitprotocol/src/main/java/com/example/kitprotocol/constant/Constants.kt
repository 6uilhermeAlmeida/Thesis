package com.example.kitprotocol.constant

object Constants {

    // TMDB constants
    const val TMDB_API_KEY = "ff6e1ad273035da941466acbb0513c1b"
    const val TMDB_BASE_URL = "http://api.themoviedb.org/3/"
    const val SITE_YOUTUBE: String = "youtube"
    const val TYPE_TRAILER: String = "trailer"

    val APPEND_TO_RESPONSE = listOf("videos")
    fun getImageUrl(specific: String?) = "https://image.tmdb.org/t/p/w500$specific"

    // Firebase
    const val REFRESH_EVENT_KEY = "refresh_movies"

}
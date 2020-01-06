package com.example.kitprotocol.rest.model


import com.google.gson.annotations.SerializedName

data class Videos(
    @SerializedName("results")
    val results: List<Result>?
) {
    fun getOneTrailerForYoutubeOrNull() = results?.firstOrNull { it.isTrailerFromYoutube() }?.key
}
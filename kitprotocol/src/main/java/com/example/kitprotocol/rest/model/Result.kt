package com.example.kitprotocol.rest.model


import com.example.kitprotocol.constant.TMDBConstants
import com.google.gson.annotations.SerializedName
import java.util.Locale

data class Result(
    @SerializedName("id")
    val id: String?,
    @SerializedName("key")
    val key: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("site")
    val site: String?,
    @SerializedName("type")
    val type: String?
) {
    fun isTrailerFromYoutube() =
        site?.toLowerCase(Locale.getDefault()) == TMDBConstants.SITE_YOUTUBE && type?.toLowerCase(Locale.getDefault()) == TMDBConstants.TYPE_TRAILER
}
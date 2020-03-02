package com.example.kitprotocol.transformer

import android.util.Log
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.rest.model.MovieDetails

fun MovieDetails.toEntity(): MovieEntity? {

    val result: Result<MovieEntity> = runCatching {
        MovieEntity(
            id!!,
            title ?: originalTitle!!,
            genres!!.joinToString(", ") { it.name },
            voteAverage,
            runtime,
            backdropPath,
            posterPath!!,
            overview?.takeIf { it.isNotBlank() }!!,
            videos?.getOneTrailerForYoutubeOrNull()
        )
    }

    return result.getOrNull().also {
        if (it == null) Log.d("MovieDetailTransformer", "Error during transform", result.exceptionOrNull())
    }
}

fun List<MovieDetails>.toEntityList(): List<MovieEntity> = mapNotNull { it.toEntity() }
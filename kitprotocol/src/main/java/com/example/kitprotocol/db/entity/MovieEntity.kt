package com.example.kitprotocol.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieEntity(
    @PrimaryKey
    val id: Int,
    val title: String?,
    val genres: String?,
    val voteAverage: Double?,
    val runtime: Int?,
    val backdropPath: String?,
    val posterPath: String?,
    val overview: String,
    // Youtube key
    val trailerKey: String?
)
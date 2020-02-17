package com.example.kitprotocol.protocol

import android.view.View
import com.example.kitprotocol.db.entity.MovieEntity

interface MovieProtocol {
    fun onMovieClicked(view: View, movieEntity: MovieEntity)
    fun onPlayTrailer(key: String)
    sealed class Item {
        data class MovieItem(val movieEntity: MovieEntity) : Item()
        data class FooterItem(val message: String) : Item()
    }
}
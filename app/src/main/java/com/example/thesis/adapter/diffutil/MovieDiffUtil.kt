package com.example.thesis.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.kitprotocol.model.MovieDetails

class MovieDiffUtil : DiffUtil.ItemCallback<MovieDetails>(){
    override fun areItemsTheSame(oldItem: MovieDetails, newItem: MovieDetails): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MovieDetails, newItem: MovieDetails): Boolean = oldItem.id == newItem.id
}
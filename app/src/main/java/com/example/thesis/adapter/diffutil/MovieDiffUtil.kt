package com.example.thesis.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.kitprotocol.db.entity.MovieEntity

class MovieDiffUtil : DiffUtil.ItemCallback<MovieEntity>(){
    override fun areItemsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: MovieEntity, newItem: MovieEntity): Boolean = oldItem == newItem
}
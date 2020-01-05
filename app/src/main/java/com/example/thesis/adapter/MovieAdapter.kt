package com.example.thesis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.thesis.R
import com.example.thesis.adapter.diffutil.MovieDiffUtil
import com.example.thesis.adapter.viewholder.MovieViewHolder

class MovieAdapter : ListAdapter<MovieEntity, MovieViewHolder>(MovieDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieViewHolder {
        return MovieViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.movie_item, parent, false))
    }

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}
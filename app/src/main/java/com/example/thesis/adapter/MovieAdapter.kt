package com.example.thesis.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.kitprotocol.protocol.MovieProtocol
import com.example.kitprotocol.protocol.MovieProtocol.Item.FooterItem
import com.example.kitprotocol.protocol.MovieProtocol.Item.MovieItem
import com.example.thesis.R
import com.example.thesis.adapter.diffutil.MovieItemDiffUtil
import com.example.thesis.adapter.viewholder.FooterItemViewHolder
import com.example.thesis.adapter.viewholder.MovieItemViewHolder

class MovieAdapter(private val protocol: MovieProtocol) :
    ListAdapter<MovieProtocol.Item, RecyclerView.ViewHolder>(MovieItemDiffUtil()) {

    companion object {
        private const val MOVIE_ITEM_TYPE = 1
        private const val FOOTER_ITEM_TYPE = 2
    }

    override fun getItemViewType(position: Int): Int = when (getItem(position)) {
        is MovieItem -> MOVIE_ITEM_TYPE
        is FooterItem -> FOOTER_ITEM_TYPE
        else -> throw IllegalStateException()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = when (viewType) {

        MOVIE_ITEM_TYPE -> MovieItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.movie_item,
                parent,
                false
            )
        )

        FOOTER_ITEM_TYPE -> FooterItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.footer_item,
                parent,
                false
            )
        )

        else -> throw IllegalStateException()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MovieItemViewHolder -> holder.bind(getItem(position) as MovieItem, protocol)
            is FooterItemViewHolder -> holder.bind(getItem(position) as FooterItem)
        }
    }
}
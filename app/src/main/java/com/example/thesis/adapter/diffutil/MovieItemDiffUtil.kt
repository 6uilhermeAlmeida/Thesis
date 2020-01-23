package com.example.thesis.adapter.diffutil

import androidx.recyclerview.widget.DiffUtil
import com.example.kitprotocol.kitinterface.MovieProtocol.Item

class MovieItemDiffUtil : DiffUtil.ItemCallback<Item>() {

    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {

        if (oldItem is Item.MovieItem && newItem is Item.MovieItem){
            return oldItem.movieEntity.id == newItem.movieEntity.id
        }

        if (oldItem is Item.FooterItem && newItem is Item.FooterItem){
            return oldItem.message == newItem.message
        }

        return false
    }

    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {

        if (oldItem is Item.MovieItem && newItem is Item.MovieItem){
            return oldItem.movieEntity == newItem.movieEntity
        }

        if (oldItem is Item.FooterItem && newItem is Item.FooterItem){
            return oldItem.message == newItem.message
        }

        return false
    }
}
package com.example.thesis.adapter.viewholder

import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.example.kitprotocol.constant.Constants
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.thesis.extensions.load
import kotlinx.android.synthetic.main.movie_item.view.imageView_backdrop
import kotlinx.android.synthetic.main.movie_item.view.imageView_poster
import kotlinx.android.synthetic.main.movie_item.view.imageView_shade
import kotlinx.android.synthetic.main.movie_item.view.textView_genres
import kotlinx.android.synthetic.main.movie_item.view.textView_popularity
import kotlinx.android.synthetic.main.movie_item.view.textView_runtime
import kotlinx.android.synthetic.main.movie_item.view.textView_title

class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(movie: MovieEntity) = view.apply {

        textView_title.text = movie.title
        textView_genres.text = movie.genres
        textView_runtime.text = "${movie.runtime}M"
        textView_popularity.text = movie.voteAverage.toString()
        imageView_backdrop.load(Constants.getImageUrl(movie.backdropPath))
        imageView_poster.load(Constants.getImageUrl(movie.posterPath), { drawable ->

            val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return@load
            Palette.from(bitmap).generate { palette ->
                val swatch = palette?.darkMutedSwatch ?: palette?.darkVibrantSwatch
                swatch?.rgb?.let { color ->
                    imageView_shade.setColorFilter(color)
                    imageView_poster.setBackgroundColor(color)
                }
            }

        })
    }
}
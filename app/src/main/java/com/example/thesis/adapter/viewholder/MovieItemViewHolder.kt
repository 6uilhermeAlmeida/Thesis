package com.example.thesis.adapter.viewholder

import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.example.kitprotocol.constant.TMDBConstants
import com.example.kitprotocol.protocol.MovieProtocol
import com.example.thesis.R
import com.example.thesis.extensions.load
import kotlinx.android.synthetic.main.movie_item.view.button_play_trailer
import kotlinx.android.synthetic.main.movie_item.view.imageView_backdrop
import kotlinx.android.synthetic.main.movie_item.view.imageView_poster
import kotlinx.android.synthetic.main.movie_item.view.imageView_shade
import kotlinx.android.synthetic.main.movie_item.view.textView_genres
import kotlinx.android.synthetic.main.movie_item.view.textView_popularity
import kotlinx.android.synthetic.main.movie_item.view.textView_runtime
import kotlinx.android.synthetic.main.movie_item.view.textView_title

class MovieItemViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(movieItem: MovieProtocol.Item.MovieItem, protocol: MovieProtocol) = with(view) {

        val movie = movieItem.movieEntity

        setOnClickListener { protocol.onMovieClicked(this, movie) }

        textView_title.text = movie.title
        textView_genres.text = movie.genres

        movie.runtime?.let {
            textView_runtime.text = view.context.getString(R.string.runtime, it)
            textView_runtime.visibility = View.VISIBLE
        } ?: run {
            textView_runtime.visibility = View.GONE
        }

        movie.voteAverage?.let {
            textView_popularity.text = movie.voteAverage.toString()
            textView_popularity.visibility = View.VISIBLE
        } ?: run {
            textView_popularity.visibility = View.GONE
        }

        movieItem.movieEntity.trailerKey?.let { key ->
            button_play_trailer.visibility = View.VISIBLE
            button_play_trailer.setOnClickListener { protocol.onPlayTrailer(key) }
        } ?: run {
            button_play_trailer.visibility = View.GONE
        }

        imageView_backdrop.load(TMDBConstants.getImageUrl(movie.backdropPath))
        imageView_poster.load(TMDBConstants.getImageUrl(movie.posterPath), { drawable ->
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
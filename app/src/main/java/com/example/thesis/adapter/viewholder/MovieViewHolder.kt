package com.example.thesis.adapter.viewholder

import android.graphics.drawable.BitmapDrawable
import android.view.View
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutineskit.rest.MovieWebService
import com.example.kitprotocol.model.MovieDetails
import com.example.thesis.extensions.glide
import kotlinx.android.synthetic.main.movie_item.view.imageView_backdrop
import kotlinx.android.synthetic.main.movie_item.view.imageView_poster
import kotlinx.android.synthetic.main.movie_item.view.imageView_shade
import kotlinx.android.synthetic.main.movie_item.view.textView_genres
import kotlinx.android.synthetic.main.movie_item.view.textView_popularity
import kotlinx.android.synthetic.main.movie_item.view.textView_runtime
import kotlinx.android.synthetic.main.movie_item.view.textView_title

class MovieViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

    fun bind(details: MovieDetails) = view.apply {

        textView_title.text = details.title
        textView_genres.text = details.genres?.chunked(3)?.get(0)?.joinToString(", ") { it.name }
        textView_popularity.text = details.voteAverage.toString()
        textView_runtime.text = "${details.runtime.toString()}M"

        imageView_backdrop.glide(MovieWebService.getImageUrl(details.backdropPath))
        imageView_poster.glide(MovieWebService.getImageUrl(details.posterPath), { drawable ->

            val bitmap = (drawable as? BitmapDrawable)?.bitmap ?: return@glide
            Palette.from(bitmap).generate().darkVibrantSwatch?.rgb?.let { color ->
                imageView_shade.setColorFilter(color)
                imageView_poster.setBackgroundColor(color)
            }
        })
    }
}
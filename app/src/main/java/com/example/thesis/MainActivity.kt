package com.example.thesis

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutineskit.viewmodel.CoroutinesViewModel
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.rxjavakit.viewmodel.RxJavaViewModel
import com.example.thesis.adapter.MovieAdapter
import com.example.thesis.command.OpenYoutubeCommand
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.recyclerView_main
import kotlinx.android.synthetic.main.activity_main.swipe_refresh_layout

class MainActivity : AppCompatActivity(), MovieAdapter.Protocol {

    private lateinit var viewModel: KitViewModel
    private val movieAdapter = MovieAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[RxJavaViewModel::class.java]

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setupObservers()
        setupMovieList()
        setupSwipeToRefresh()
    }

    private fun setupSwipeToRefresh() {
        swipe_refresh_layout.setProgressBackgroundColorSchemeColor(Color.DKGRAY)
        swipe_refresh_layout.setColorSchemeColors(Color.WHITE)
        swipe_refresh_layout.setOnRefreshListener {
            viewModel.fetchTrendingMovies()
        }
    }

    private fun setupMovieList() {
        recyclerView_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.getMovies().observe(this, Observer { movies ->
            if (movies.isEmpty()) return@Observer
            movieAdapter.submitList(movies) {
                recyclerView_main.scrollToPosition(0)
            }
        })

        viewModel.getMessage().observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.resetMessage()
            }
        })

        viewModel.getIsLoading().observe(this, Observer { isLoading ->
            swipe_refresh_layout.isRefreshing = isLoading
        })
    }

    override fun onMovieClicked(view: View, movieEntity: MovieEntity) {

        view.scaleX = 0.98f
        view.scaleY = 0.98f
        view.animate()
            .scaleY(1f)
            .scaleX(1f)
            .setInterpolator(OvershootInterpolator())
            .setDuration(500L)
            .start()

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(movieEntity.title)
            .setMessage(movieEntity.overview)

        movieEntity.trailerKey?.let {
            dialog.setPositiveButton("Play trailer") { _, _ -> OpenYoutubeCommand(this).open(it) }
        }

        dialog.show()
    }
}

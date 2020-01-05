package com.example.thesis

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutineskit.viewmodel.CoroutinesViewModel
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.rxjavakit.viewmodel.RxJavaViewModel
import com.example.thesis.adapter.MovieAdapter
import kotlinx.android.synthetic.main.activity_main.recyclerView_main
import kotlinx.android.synthetic.main.activity_main.swipe_refresh_layout

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: KitViewModel
    private val movieAdapter = MovieAdapter()

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
}

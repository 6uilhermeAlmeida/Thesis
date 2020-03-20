package com.example.thesis

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitprotocol.db.entity.MovieEntity
import com.example.kitprotocol.protocol.KitViewModel
import com.example.kitprotocol.protocol.MovieProtocol
import com.example.thesis.adapter.MovieAdapter
import com.example.thesis.command.OpenYoutubeCommand
import com.example.thesis.config.MainConfig
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_main.recyclerView_main
import kotlinx.android.synthetic.main.activity_main.swipe_refresh_layout
import kotlinx.android.synthetic.main.activity_main.toolbar

class MainActivity : AppCompatActivity(), MovieProtocol {

    companion object {
        const val LOCATION_PERMISSION = Manifest.permission.ACCESS_FINE_LOCATION
        const val LOCATION_PERMISSION_REQ_CODE = 1
    }

    private lateinit var viewModel: KitViewModel
    private val movieAdapter = MovieAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        viewModel = MainConfig.getViewModel(this, application)

        setupObservers()
        setupMovieList()
        setupSwipeToRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        val gcItem = menu?.getItem(1)?.apply { isVisible = false }
        toolbar.setOnLongClickListener {
            gcItem?.apply { isVisible = !isVisible }
            return@setOnLongClickListener true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.local_movies -> {
                if (isLocationPermissionGranted()) {
                    viewModel.onLocalMoviesClick()
                } else {
                    requestPermissions(arrayOf(LOCATION_PERMISSION), LOCATION_PERMISSION_REQ_CODE)
                }
                true
            }

            R.id.garbage_collector -> {
                System.gc()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupSwipeToRefresh() {
        swipe_refresh_layout.setProgressBackgroundColorSchemeColor(Color.DKGRAY)
        swipe_refresh_layout.setColorSchemeColors(Color.WHITE)
        swipe_refresh_layout.setOnRefreshListener { viewModel.onRefresh() }
    }

    private fun setupMovieList() {
        recyclerView_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = movieAdapter
        }
    }

    private fun setupObservers() {
        viewModel.getMovies().observe(this, Observer { movies ->
            if (movies.isNotEmpty()) movieAdapter.submitList(movies) {
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

        viewModel.getIsLocalMovies().observe(this, Observer { isLocalMovies ->
            val menuItem = toolbar.menu.findItem(R.id.local_movies) ?: return@Observer
            menuItem.icon.setTint(getColor(if (isLocalMovies) R.color.colorAccent else R.color.colorPrimary))
        })
    }

    override fun onMovieClicked(view: View, movieEntity: MovieEntity) {

        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle(movieEntity.title)
            .setMessage(movieEntity.overview)

        movieEntity.trailerKey?.let {
            dialog.setPositiveButton(getString(R.string.play_trailer)) { _, _ -> onPlayTrailer(it) }
        }

        dialog.show()
    }

    override fun onPlayTrailer(key: String) {
        OpenYoutubeCommand(this).open(key)
    }

    private fun isLocationPermissionGranted(): Boolean =
        ContextCompat.checkSelfPermission(this, LOCATION_PERMISSION) == PackageManager.PERMISSION_GRANTED

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQ_CODE) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                viewModel.onLocalMoviesClick()
            } else if (!shouldShowRequestPermissionRationale(LOCATION_PERMISSION)) {
                viewModel.onLocationPermissionDeniedIndefinitely()
            }
        }
    }
}

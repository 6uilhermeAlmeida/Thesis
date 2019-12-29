package com.example.thesis

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.coroutineskit.viewmodel.CoroutinesViewModel
import com.example.kitprotocol.kitinterface.KitViewModel
import com.example.thesis.adapter.MovieAdapter
import kotlinx.android.synthetic.main.activity_main.recyclerView_main

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: KitViewModel
    private val movieAdapter = MovieAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this)[CoroutinesViewModel::class.java]

        viewModel.movies.observe(this, Observer { movies -> movieAdapter.submitList(movies) })

        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.resetMessage()
            }
        })

        recyclerView_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity, RecyclerView.VERTICAL, false)
            adapter = movieAdapter
        }
    }
}

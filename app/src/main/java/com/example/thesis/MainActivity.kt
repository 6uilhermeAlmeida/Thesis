package com.example.thesis

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.coroutineskit.viewmodel.CoroutinesViewModel
import com.example.kitprotocol.kitinterface.KitViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: KitViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this)[CoroutinesViewModel::class.java]

        viewModel.movies.observe(this, Observer { movies ->
            Toast.makeText(this, "Found ${movies.size} movies!", Toast.LENGTH_SHORT).show()
        })

        viewModel.message.observe(this, Observer { message ->
            message?.let {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
                viewModel.resetMessage()
            }
        })
    }
}

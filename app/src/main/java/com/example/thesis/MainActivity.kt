package com.example.thesis

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.coroutineskit.repository.CoroutinesRepository
import com.example.kitprotocol.repository.KitRepository

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val repository : KitRepository = CoroutinesRepository()
        Toast.makeText(this, repository.message, Toast.LENGTH_SHORT).show()
    }
}

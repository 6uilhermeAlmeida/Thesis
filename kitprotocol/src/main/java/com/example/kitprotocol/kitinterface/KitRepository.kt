package com.example.kitprotocol.kitinterface

import androidx.lifecycle.LiveData
import com.example.kitprotocol.db.entity.MovieEntity

interface KitRepository {
    val movies : LiveData<List<MovieEntity>>
}
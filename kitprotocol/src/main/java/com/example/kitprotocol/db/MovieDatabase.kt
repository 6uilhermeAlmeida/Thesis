package com.example.kitprotocol.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.kitprotocol.db.dao.MovieDao
import com.example.kitprotocol.db.entity.MovieEntity

@Database(entities = [MovieEntity::class], version = 1, exportSchema = false)
abstract class MovieDatabase : RoomDatabase() {

    companion object {
        private var instance: MovieDatabase? = null
        fun getInstance(context: Context): MovieDatabase {
            return instance ?: Room.databaseBuilder(context, MovieDatabase::class.java, "movie-db")
                .build()
                .also { instance = it }
        }
    }

    abstract val movieDao: MovieDao
}
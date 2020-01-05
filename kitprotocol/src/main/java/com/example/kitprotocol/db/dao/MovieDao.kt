package com.example.kitprotocol.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.kitprotocol.db.entity.MovieEntity
import io.reactivex.Completable

@Dao
interface MovieDao {

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    fun all(): LiveData<List<MovieEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movieEntities: List<MovieEntity>)

    @Query("DELETE FROM MovieEntity")
    suspend fun nuke()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsCompletable(movieEntities: List<MovieEntity>) : Completable

    @Query("DELETE FROM MovieEntity")
    fun nukeAsCompletable() : Completable
}
package com.example.kitprotocol.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.kitprotocol.db.entity.MovieEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    fun allByFlow(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    fun allByFlowable(): Flowable<List<MovieEntity>>

    @Transaction
    suspend fun fresh(movieEntities: List<MovieEntity>) {
        nuke()
        insertAll(movieEntities)
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movieEntities: List<MovieEntity>)

    @Query("DELETE FROM MovieEntity")
    suspend fun nuke()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllAsCompletable(movieEntities: List<MovieEntity>) : Completable

    @Query("DELETE FROM MovieEntity")
    fun nukeAsCompletable() : Completable
}
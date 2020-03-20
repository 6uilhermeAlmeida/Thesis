package com.example.kitprotocol.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.kitprotocol.db.entity.MovieEntity
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {

    /**
     * Fetching movies.
     */

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    fun allByFlow(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    suspend fun allSuspending(): List<MovieEntity>

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    fun allByFlowable(): Flowable<List<MovieEntity>>

    @Query("SELECT * FROM MovieEntity ORDER BY voteAverage DESC")
    fun allBySingle(): Single<List<MovieEntity>>

    /**
     * Inserting movies.
     */

    // Suspending methods for Kotlin Coroutines
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun suspendInsert(movieEntities: List<MovieEntity>)

    @Query("DELETE FROM MovieEntity")
    suspend fun suspendNuke()

    @Transaction
    suspend fun suspendNukeAndInsert(movieEntities: List<MovieEntity>) {
        suspendNuke()
        suspendInsert(movieEntities)
    }

    // Completable methods for RxJava (demo)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAsCompletable(movieEntities: List<MovieEntity>) : Completable

    @Query("DELETE FROM MovieEntity")
    fun nukeAsCompletable() : Completable

    // Regular functions
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(movieEntities: List<MovieEntity>)

    @Query("DELETE FROM MovieEntity")
    fun nuke()

    @Transaction
    fun nukeAndInsert(movieEntities: List<MovieEntity>) {
        nuke()
        insert(movieEntities)
    }
}
package com.example.benchmark

import org.junit.Test

interface IDatabaseBenchmark {
    /**
     * Inserts
     */

    @Test
    fun insert_two_movies()

    @Test
    fun insert_ten_movies()

    @Test
    fun insert_twenty_movies()

    @Test
    fun insert_fifty_movies()

    @Test
    fun insert_one_hundred_movies()

    @Test
    fun clear_and_insert_twenty_movies()

    /**
     * Queries
     */

    @Test
    fun query_twenty_movies()

    @Test
    fun query_fifty_movies()

    @Test
    fun query_one_hundred_movies()

    @Test
    fun query_twenty_movies_in_parallel()

    @Test
    fun query_twenty_movies_reactive()
}
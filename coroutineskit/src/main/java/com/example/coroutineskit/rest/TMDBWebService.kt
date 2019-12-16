package com.example.coroutineskit.rest

import com.example.kitprotocol.constant.Constants
import com.example.kitprotocol.model.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface IMovieWebService {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies( @Query("api_key") apiKey: String = Constants.TMDB_API_KEY) : MovieResponse
}

object MovieWebService {

    val service: IMovieWebService by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IMovieWebService::class.java)
    }

}
package com.example.coroutineskit.rest

import com.example.kitprotocol.constant.Constants
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.rest.model.MovieResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieWebServiceCoroutines {

    @GET("trending/movie/week")
    suspend fun getTrendingMovies(@Query("api_key") apiKey: String = Constants.TMDB_API_KEY): MovieResponse

    @GET("movie/{movie_id}")
    suspend fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: List<String> = Constants.APPEND_TO_RESPONSE
    ): MovieDetails

    @GET("movie/now_playing")
    suspend fun getMoviesNowPlayingForRegion(@Query("region") countryCode : String) : MovieResponse

}

object MovieWebServiceCoroutines {

    val service: IMovieWebServiceCoroutines by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(IMovieWebServiceCoroutines::class.java)
    }
}
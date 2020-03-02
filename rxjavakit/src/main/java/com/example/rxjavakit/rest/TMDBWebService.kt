package com.example.rxjavakit.rest

import com.example.kitprotocol.constant.Constants
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.rest.model.MovieResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieWebServiceRxJava {

    @GET("trending/movie/week")
    fun getTrendingMovies(@Query("api_key") apiKey: String = Constants.TMDB_API_KEY): Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: List<String> = Constants.APPEND_TO_RESPONSE
    ): Single<MovieDetails>

    @GET("movie/now_playing")
    fun getMoviesNowPlayingForRegion(
        @Query("region") countryCode: String,
        @Query("api_key") apiKey: String = Constants.TMDB_API_KEY
    ): Single<MovieResponse>
}

object MovieWebServiceRxJava {

    val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(Constants.TMDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())

    val service: IMovieWebServiceRxJava by lazy {
        builder.build().create(IMovieWebServiceRxJava::class.java)
    }
}
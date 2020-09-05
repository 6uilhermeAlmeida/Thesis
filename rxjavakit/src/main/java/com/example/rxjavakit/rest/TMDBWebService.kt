package com.example.rxjavakit.rest

import com.example.kitprotocol.constant.TMDBConstants
import com.example.kitprotocol.rest.http.MovieHttpClient
import com.example.kitprotocol.rest.model.MovieDetails
import com.example.kitprotocol.rest.model.MovieResponse
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface IMovieServiceRxJava {

    @GET("trending/movie/week")
    fun getTrendingMovies(@Query("api_key") apiKey: String = TMDBConstants.TMDB_API_KEY): Single<MovieResponse>

    @GET("movie/{movie_id}")
    fun getMovieDetails(
        @Path("movie_id") movieId: Int,
        @Query("api_key") apiKey: String = TMDBConstants.TMDB_API_KEY,
        @Query("append_to_response") appendToResponse: List<String> = TMDBConstants.APPEND_TO_RESPONSE
    ): Single<MovieDetails>

    @GET("movie/now_playing")
    fun getMoviesNowPlayingForRegion(
        @Query("region") countryCode: String,
        @Query("api_key") apiKey: String = TMDBConstants.TMDB_API_KEY
    ): Single<MovieResponse>
}

object MovieWebServiceRxJava {

    private val builder: Retrofit.Builder = Retrofit.Builder()
        .baseUrl(TMDBConstants.TMDB_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.createAsync())

    val service: IMovieServiceRxJava by lazy {
        builder
            .client(MovieHttpClient.instance)
            .build()
            .create(IMovieServiceRxJava::class.java)
    }

    fun mock(): IMovieServiceRxJava = builder
        .client(MovieHttpClient.mock())
        .build()
        .create(IMovieServiceRxJava::class.java)
}
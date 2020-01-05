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
    fun getMovieDetails(@Path("movie_id") movieId: Int, @Query("api_key") apiKey: String = Constants.TMDB_API_KEY): Single<MovieDetails>

}

object MovieWebServiceRxJava {

    val service: IMovieWebServiceRxJava by lazy {

        Retrofit.Builder()
            .baseUrl(Constants.TMDB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(IMovieWebServiceRxJava::class.java)
    }
}
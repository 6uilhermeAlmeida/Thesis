package mock.services

import com.example.coroutineskit.rest.IMovieServiceCoroutines
import com.example.coroutineskit.rest.MovieWebServiceCoroutines
import com.example.rxjavakit.rest.IMovieWebServiceRxJava
import com.example.rxjavakit.rest.MovieWebServiceRxJava

object MockedMovieServices {

    val coroutineService: IMovieServiceCoroutines = MovieWebServiceCoroutines.builder
        .client(mockHttpClient)
        .build()
        .create(IMovieServiceCoroutines::class.java)

    val rxJavaService: IMovieWebServiceRxJava = MovieWebServiceRxJava.builder
        .client(mockHttpClient)
        .build()
        .create(IMovieWebServiceRxJava::class.java)
}
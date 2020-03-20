package com.example.kitprotocol.mock

import okhttp3.MediaType
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody

fun Request.getMockedResponse(): Response {

    val url = this.url().toString()
    val mockedResponse = when {
        url.contains("trending") || url.contains("now_playing") -> MockedResponse.MovieListResponse
        url.contains("movie") -> MockedResponse.MovieDetailsResponse(url().pathSegments().last())
        else -> throw IllegalArgumentException("There are no mocked responses for this URL : $url")
    }

    val body = ResponseBody.create(MediaType.get("application/json"), mockedResponse.body)
    return Response.Builder()
        .request(this)
        .code(200)
        .body(body)
        .message(url)
        .protocol(Protocol.HTTP_1_1)
        .build()
}
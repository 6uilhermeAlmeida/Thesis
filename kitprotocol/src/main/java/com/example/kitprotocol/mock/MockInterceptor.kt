package com.example.kitprotocol.mock

import okhttp3.Interceptor
import okhttp3.Response

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        Thread.sleep(50)
        return chain.request().getMockedResponse()
    }
}
package mock.services

import mock.MockInterceptor
import okhttp3.OkHttpClient

internal val mockHttpClient by lazy {
    OkHttpClient.Builder()
        .addInterceptor(MockInterceptor())
        .build()
}
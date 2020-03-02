package mock

import okhttp3.Interceptor
import okhttp3.Response

class MockInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response = chain.request().getMockedResponse()
}
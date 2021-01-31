package com.zhangyf.draftbottle.manager.base

import okhttp3.Interceptor
import okhttp3.Response

var token: String? = null

class HeaderInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = if (token != null) {
            chain.request().newBuilder()
                //.header("Connection", "close")
                .header("Authorization", "Bearer ${token}")
                .build()
        } else chain.request().newBuilder().build()
        return chain.proceed(request)
    }

}
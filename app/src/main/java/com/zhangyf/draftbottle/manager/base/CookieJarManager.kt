package com.zhangyf.draftbottle.manager.base

import android.util.Log
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import java.util.*

/**
 * OkHttp框架Cookie管理类
 */

var cachedCookies: List<Cookie>? = null

class CookieJarManager : CookieJar {
    private val TAG = "CookieJarManager"
    private val cookieStore =
        HashMap<String, List<Cookie>>()

    override fun saveFromResponse(
        url: HttpUrl,
        cookies: List<Cookie>
    ) {
        if (null == url || null == cookies || cookies.size <= 0) {
            return
        }
        cookieStore[url.host()] = cookies
        for (cookie in cookies) {
            Log.d(TAG, "cookie Name:" + cookie.name())
            Log.d(TAG, "cookie Path:" + cookie.path())
        }
        //只从第一个请求拿cookie
        if(cachedCookies == null){
            cachedCookies = cookies
        }
    }

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return if (null != url) {
            val cookies = cookieStore[url.host()]
            cookies ?: ArrayList()
        } else {
            ArrayList()
        }
    }
}
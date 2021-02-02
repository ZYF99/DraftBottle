package com.zhangyf.draftbottle.manager.base

import android.annotation.SuppressLint
import com.zhangyf.draftbottle.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.Interceptor
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.security.SecureRandom
import java.security.cert.CertificateException
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager
import kotlin.reflect.KClass

class OauthClient private constructor(val retrofit: Retrofit, val okHttpClient: OkHttpClient) {
	
	fun <S> createService(serviceClass: Class<S>): S = retrofit.create(serviceClass)

	fun <S : Any> createService(serviceClass: KClass<S>): S = createService(serviceClass.java)
	
	
	class Builder(
		val apiAuthorizations: MutableMap<String, Interceptor> = LinkedHashMap(),
		val okBuilder: OkHttpClient.Builder = OkHttpClient.Builder(),
		val adapterBuilder: Retrofit.Builder = Retrofit.Builder()
	) {
		
		private val allowAllSSLSocketFactory: Pair<SSLSocketFactory, X509TrustManager>?
			get() {
				return try {
					val sslContext = SSLContext.getInstance("TLS")
					val trustManager = trustManagerAllowAllCerts
					sslContext.init(
						null,
						arrayOf<TrustManager>(trustManager),
						SecureRandom()
					)
					
					sslContext.socketFactory to trustManager
				} catch (e: Exception) {
					Timber.e(e, "allowAllSSLSocketFactory has error")
					null
				}
			}
		

		private val trustManagerAllowAllCerts: X509TrustManager
			get() = object : X509TrustManager {
				override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
				
				@SuppressLint("TrustAllX509TrustManager")
				@Throws(CertificateException::class)
				override fun checkClientTrusted(
					chain: Array<X509Certificate>, authType: String
				) {
				}
				
				@SuppressLint("TrustAllX509TrustManager")
				@Throws(CertificateException::class)
				override fun checkServerTrusted(
					chain: Array<X509Certificate>, authType: String
				) {
				}
			}
		
		fun setAllowAllCerTificates(): Builder {
			allowAllSSLSocketFactory?.apply {
				okBuilder.sslSocketFactory(first, second)
				okBuilder.hostnameVerifier(HostnameVerifier { _, _ -> true })
			}
			
			return this
		}
		
		fun build(baseUrl: String = BuildConfig.OAUTHURL): OauthClient {			adapterBuilder
			.baseUrl(baseUrl)
			//.addConverterFactory(WireConverterFactory.create())
			.addCallAdapterFactory(RxJava2CallAdapterFactory.create())
			.addConverterFactory(LenientGsonConverterFactory.create())

			val client = okBuilder.build()

			setAllowAllCerTificates()

			val retrofit = adapterBuilder.client(client).build()
			return OauthClient(retrofit, client)
		}

	}
	
	companion object {
		val defaultClient: OauthClient
			get() = Builder().build()
	}
}


package com.zhangyf.draftbottle.manager.base

import com.zhangyf.draftbottle.BuildConfig
import com.zhangyf.draftbottle.manager.api.*
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.Kodein
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.singleton
import java.util.concurrent.TimeUnit

/**
 * @author Zhangyf
 * @version 1.0
 * @date 2019/10/20 8：12
 */
val apiModule = Kodein.Module {
	//SSxxapi
	bind<SSxxClient>() with singleton { provideSSxxClient() }

	bind<SSxxService>() with singleton { instance<SSxxClient>().createService(SSxxService::class.java) }

	//Oatuthapi
	bind<OauthClient>() with singleton { provideOauthClient() }

	bind<OauthService>() with singleton { instance<OauthClient>().createService(OauthService::class.java) }

}


fun provideSSxxClient(): SSxxClient {
	val client = SSxxClient.Builder()
	val logInterceptor = HttpLoggingInterceptor()
	logInterceptor.level = HttpLoggingInterceptor.Level.BODY
	
	client.okBuilder
		.addInterceptor(HeaderInterceptor())
		.addInterceptor(NetErrorInterceptor())
		.cookieJar(CookieJarManager())
		.apply {
			if (BuildConfig.DEBUG)
				addInterceptor(logInterceptor)
		}
		.readTimeout(10, TimeUnit.SECONDS)
	
	return client.build()
}

fun provideOauthClient(): OauthClient {
	val client = OauthClient.Builder()
	val logInterceptor = HttpLoggingInterceptor()
	logInterceptor.level = HttpLoggingInterceptor.Level.BODY

	client.okBuilder
		//.addInterceptor(HeaderInterceptor())
		.addInterceptor(NetErrorInterceptor())
		.cookieJar(CookieJarManager())
		.apply {
			if (BuildConfig.DEBUG)
				addInterceptor(logInterceptor)
		}
		.readTimeout(10, TimeUnit.SECONDS)

	return client.build()
}
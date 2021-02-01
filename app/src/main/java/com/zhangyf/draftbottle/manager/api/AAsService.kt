package com.zhangyf.draftbottle.manager.api

import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface AAsService {

    @FormUrlEncoded
    @POST("login.php")
    fun selectUserBalance(
        @Field("type") type: Int = 2,
        @Field("name") name: String?,
        @Field("phone") phone: String?,
        @Field("school") school: String?,
        @Field("uid") uid: String? = null,
        @Field("ed") ed: Int = 1
    ): Single<ResponseBody>

    @FormUrlEncoded
    @POST("login.php")
    fun uploadUserBalance(
        @Field("type") type: Int = 3,
        @Field("name") name: String?,
        @Field("phone") phone: String?,
        @Field("school") school: String?,
        @Field("uid") uid: String? = null,
        @Field("ed") ed: Int
    ): Single<ResponseBody>

}
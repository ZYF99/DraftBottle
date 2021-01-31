package com.zhangyf.draftbottle.manager.api

import com.zhangyf.draftbottle.model.api.ssss.*
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface SSxxService {


    @GET("clientLogin?redirect=%2Fclient%2Fdetail%2F5f71e934bcdbf3a8c3ba5061")
    fun getoginSession(): Single<ResponseBody>

    @GET("cgi-bin/authorize/token/?activity_id=5f71e934bcdbf3a8c3ba5061")
    fun getUserToken(
        @Query("t") time: Long? = System.currentTimeMillis(),
        @Query("uid") uid: String? = null,
        @Query("avatar") avatar: String? = null
    ): Single<UserTokenResultModel>

    //获取题目id和raceId
    @GET("cgi-bin/race/beginning/")
    fun getQuestionIdAndRaceId(
        @Header("Referer") refer: String? = null,
        @Query("t") time: Long? = System.currentTimeMillis(),
        @Query("activity_id") activityId: String? = "5f71e934bcdbf3a8c3ba5061",
        @Query("mode_id") modeId: String? = "5f71e934bcdbf3a8c3ba51d5",
        @Query("way") way: Int = 1
    ): Single<QuestionIdListResultModel>

    //根据题目Id获取选项Id
    @GET("cgi-bin/race/question/")
    fun getQuestionOptions(
        @Query("t") time: Long? = System.currentTimeMillis(),
        @Query("activity_id") activityId: String? = "5f71e934bcdbf3a8c3ba5061",
        @Query("question_id") questionId: String? = null,
        @Query("mode_id") modeId: String? = "5f71e934bcdbf3a8c3ba51d5",
        @Query("way") way: Int = 1
    ): Single<QuestionAnswerResultModel>

    //提交答案
    @POST("cgi-bin/race/answer/")
    fun submitAnswer(@Body submitAnswerRequestModel: SubmitAnswerRequestModel): Single<ResponseBody>

    //停止答题
    @POST("cgi-bin/race/finish/")
    fun finishAnswer(@Body finishAnswerRequestModel: FinishAnswerRequestModel): Single<ResponseBody>

}
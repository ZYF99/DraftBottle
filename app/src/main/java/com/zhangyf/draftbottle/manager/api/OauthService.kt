package com.zhangyf.draftbottle.manager.api

import com.zhangyf.draftbottle.model.api.ssss.LoginConfirmResultModel
import com.zhangyf.draftbottle.model.api.ssss.QrResultModel
import io.reactivex.Single
import okhttp3.ResponseBody
import retrofit2.http.*

interface OauthService {
	

	@GET("qrcode/5f582dd3683c2e0ae3aaacee?useSelfWxapp=true&enableFetchPhone=false")
	fun getPicturlUrl(@Query("random")random:String): Single<QrResultModel>


	@POST("confirm/qr?useSelfWxapp=true")
	fun checkIfLogin(@Query("random")radom:String): Single<LoginConfirmResultModel>
	
}
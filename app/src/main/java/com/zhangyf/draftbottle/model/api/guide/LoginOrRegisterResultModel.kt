package com.zhangyf.draftbottle.model.api.guide

import com.zhangyf.draftbottle.model.api.mine.UsrProfile

data class LoginOrRegisterResultModel(
	val uin:Long,
	val openId:String,
	val token: String,
	val profileCompletion:Int,
	val usrProfile: UsrProfile,
	val usrSetting: UserSetting,
	val usrStatusFlag: UsrStatusFlag,
	val lastLoginInfo: LastLoginInfo
) {
	
	
	data class UserSetting(
		val notification: Boolean,
		val voiceNotification: Boolean,
		val theme: String
	)
	
	data class UsrStatusFlag(
		val emailVerifiedFlag: Boolean,
		val disableFlag: Boolean,
		val needMoreInfoFlag: Boolean
	)
	
	data class LastLoginInfo(
		val device: String,
		val lastLoginTime: String
	)
	
}
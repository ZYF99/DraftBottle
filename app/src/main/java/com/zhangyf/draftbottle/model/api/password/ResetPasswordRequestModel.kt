package com.zhangyf.draftbottle.model.api.password

data class ResetPasswordRequestModel(
	val code: String,
	val email: String,
	val password: String
)
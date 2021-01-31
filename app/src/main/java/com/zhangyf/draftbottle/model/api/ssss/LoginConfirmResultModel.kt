package com.zhangyf.draftbottle.model.api.ssss

data class LoginConfirmResultModel(
    val data:ConfirResultModel
)

data class ConfirResultModel(
    val code:Int,
    val message:String,
    val data:ContentData
)

data class ContentData(
    val photo:String,
    val _id:String
)
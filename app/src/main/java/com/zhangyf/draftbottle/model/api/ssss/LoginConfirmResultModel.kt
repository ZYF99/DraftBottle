package com.zhangyf.draftbottle.model.api.ssss

data class LoginConfirmResultModel(
    val data:ConfirmResultModel
)

data class ConfirmResultModel(
    val code:Int,
    val message:String,
    val data:ContentData
)

data class ContentData(
    val photo:String,
    val nickName:String,
    val company:String,
    val phone:String,
    val _id:String
)
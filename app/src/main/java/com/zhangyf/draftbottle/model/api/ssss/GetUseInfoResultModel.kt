package com.zhangyf.draftbottle.model.api.ssss

data class GetUseInfoResultModel(
    val code:Int? = null,
    val data:UserInfo? = null
)

data class UserInfo(
val name:String?=null,
val university_name:String?=null,
val integral:Int? = null
)
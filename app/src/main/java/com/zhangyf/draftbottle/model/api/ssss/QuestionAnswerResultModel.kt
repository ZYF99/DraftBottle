package com.zhangyf.draftbottle.model.api.ssss

data class QuestionAnswerResultModel(
    val code: Int? = null,
    val data: QuestionAnswerModel? = null
)

data class QuestionAnswerModel(
    val id:String?=null,
    val title:String?=null,
    val options: List<OptionModel>?=null
)

data class OptionModel(
    val id: String? = null,
    val title: String? = null
)
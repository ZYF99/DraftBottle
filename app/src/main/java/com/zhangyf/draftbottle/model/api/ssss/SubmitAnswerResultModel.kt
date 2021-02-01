package com.zhangyf.draftbottle.model.api.ssss

data class SubmitAnswerResultModel(
    val code: Int? = null,
    val data: AnswerResultModel? = null
)

data class AnswerResultModel(
    val correct: Boolean? = null
)
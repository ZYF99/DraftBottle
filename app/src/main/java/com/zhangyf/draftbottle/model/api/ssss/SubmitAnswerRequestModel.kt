package com.zhangyf.draftbottle.model.api.ssss

data class SubmitAnswerRequestModel(
    val activity_id: String = "5f71e934bcdbf3a8c3ba5061",
    val question_id: String,
    val answer: List<String>,
    val mode_id: String = "5f71e934bcdbf3a8c3ba51d5",
    val way: String = "1"
)
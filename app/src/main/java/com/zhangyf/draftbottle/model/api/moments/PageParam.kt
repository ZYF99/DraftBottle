package com.zhangyf.draftbottle.model.api.moments

data class PageParam(
	val pageNum: Int,
	val pageSize: Int = 10,
	val limitKey: String = ""
)
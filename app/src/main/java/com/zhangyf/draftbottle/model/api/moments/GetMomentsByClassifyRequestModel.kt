package com.zhangyf.draftbottle.model.api.moments

data class GetMomentsByClassifyRequestModel(
	val classify: Int,
	val pageParam: PageParam?
)
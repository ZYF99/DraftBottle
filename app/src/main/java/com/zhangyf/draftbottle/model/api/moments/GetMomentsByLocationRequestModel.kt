package com.zhangyf.draftbottle.model.api.moments

import com.zhangyf.draftbottle.model.api.LocationReq

data class GetMomentsRequestModel(
	val locationReq: LocationReq,
	val pageParam: PageParam?
)


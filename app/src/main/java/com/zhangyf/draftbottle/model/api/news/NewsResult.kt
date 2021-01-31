package com.zhangyf.draftbottle.model.api.news

import com.zhangyf.draftbottle.model.api.SimpleProfileResp

data class NewsResult(
	val newsDetailList: List<News>?
) {
	data class News(
		val newsId: Long,
		val title: String,
		val createDate: Long,
		val category: String,
		val authorProfile: SimpleProfileResp,
		val newsType: Int,
		val payloadType: Int,
		val payload: String,
		val frontCoverImages: List<String>
	) {
		val isTEXT
			get() = payloadType == 1
		val isURL
			get() = payloadType == 2
		val isMD
			get() = payloadType == 3
		val isHTML
			get() = payloadType == 4
		val isBanner
			get() = newsType == 1
	}
	
}

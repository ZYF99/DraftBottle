package com.zhangyf.draftbottle.model.api.search

import com.google.gson.annotations.Expose

data class SearchKeyConclusion(
	val name:String,
	val sortId:Int,
	@Expose(serialize = true,deserialize = false)
	var category: Category
)
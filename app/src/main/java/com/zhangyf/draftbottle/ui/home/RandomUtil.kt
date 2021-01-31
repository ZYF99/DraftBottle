package com.zhangyf.draftbottle.ui.home

private fun getRandom(count: Int): Int {
    return Math.round(Math.random() * (count)).toInt()
}

private val string = "1234567890abcdefghijklmnopqrstuvwxyz"

fun getRandomString(length: Int): String {
    val sb = StringBuffer()
    val len = string.length
    for (i in 0..length - 1) {
        sb.append(string[getRandom(len - 1)])
    }
    return sb.toString()
}
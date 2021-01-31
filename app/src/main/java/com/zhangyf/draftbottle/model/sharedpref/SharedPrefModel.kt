package com.zhangyf.draftbottle.model.sharedpref

import com.chibatching.kotpref.KotprefModel

object SharedPrefModel : KotprefModel() {
    override val kotprefName: String = "DraftBottleCache"
    var draftSelectedImgList by stringPref()
    var accessToken by stringPref()
    var tokenInfo : String by stringPref()
  

}
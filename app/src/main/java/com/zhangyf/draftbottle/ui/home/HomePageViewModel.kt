package com.zhangyf.draftbottle.ui.home

import android.app.Application
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import com.zhangyf.draftbottle.MyApplication
import com.zhangyf.draftbottle.manager.api.OauthService
import com.zhangyf.draftbottle.manager.api.SSxxService
import com.zhangyf.draftbottle.manager.base.token
import com.zhangyf.draftbottle.model.api.ssss.FinishAnswerRequestModel
import com.zhangyf.draftbottle.model.api.ssss.QuestionAnswerModel
import com.zhangyf.draftbottle.model.api.ssss.SubmitAnswerRequestModel
import com.zhangyf.draftbottle.ui.base.BaseViewModel
import com.zhangyf.draftbottle.utils.switchThread
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.Single
import io.reactivex.SingleSource
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.kodein.di.generic.instance
import java.util.HashMap
import java.util.concurrent.TimeUnit

class HomePageViewModel(application: Application) : BaseViewModel(application) {
    private val ssxxService by instance<SSxxService>()
    private val oAuthService by instance<OauthService>()

    val imageUrlMutableLiveData = MutableLiveData<String>()
    val randomStringMutableLiveData = MutableLiveData<String>()

    val photoUrlMutableLiveData = MutableLiveData<String>()
    val uidMutableLiveData = MutableLiveData<String>()


    val raceIdMutableLiveData = MutableLiveData<String>()
    val questionOptionPairListMutableLiveData = MutableLiveData<List<Pair<Pair<String, String>, List<Pair<String,String>>>>>()

    fun getLoginSessionAndQRCode() {
        randomStringMutableLiveData.value = getRandomString(20)

        ssxxService.getoginSession()
            .doOnApiSuccess {
                Log.d("GET LOGIN SESSION", it.string())
            }
        oAuthService.getPicturlUrl(randomStringMutableLiveData.value ?: "")
            .doOnApiSuccess {
                imageUrlMutableLiveData.postValue(it.data.qrcode)
                checkIfLogin()
            }
    }

    var checkObservable: Observable<*>? = null

    fun checkIfLogin() {
        checkObservable = Observable.interval(1, 2, TimeUnit.SECONDS)
            .flatMapSingle {
                oAuthService.checkIfLogin(randomStringMutableLiveData.value ?: "")
            }.switchThread()
            .doOnNext {
                if (it.data.code != 500) {
                    compositeDisposable.clear()
                    photoUrlMutableLiveData.value = it.data.data.photo
                    uidMutableLiveData.value = it.data.data._id
                    getUserTokenAndQuestionAndAnswer()
                }
            }.doOnError {
                if (it is LoginSuccessException) {
                    MyApplication.showWarning("登陆成功！")
                    Log.d("LoginSuccess", "登陆成功!")
                }

            }
        checkObservable?.bindLife()
    }

    private fun getUserTokenAndQuestionAndAnswer() {
        //获取用户token
        ssxxService.getUserToken(
            avatar = photoUrlMutableLiveData.value,
            uid = uidMutableLiveData.value
        ).flatMap {
            token = it.token
            //获取题目ID列表
            ssxxService.getQuestionIdAndRaceId(
                refer = "https://ssxx.univs.cn/client/exam/5f71e934bcdbf3a8c3ba5061/1/1/${uidMutableLiveData.value}"
            )
        }.doOnApiSuccess {
            raceIdMutableLiveData.value = it.race_code
            getQuestionOptionsPairList(it.question_ids)
        }
    }

    //获取题目选项
    private fun getQuestionOptionsPairList(questionIdList: List<String>?) {

        //解析选项的中文出来
        fun parseOptionChinese(questionAnswerModel: QuestionAnswerModel?): Single<Pair<Pair<String, String>, List<Pair<String,String>>>> {
            val questionTitleDoc = Jsoup.parse(questionAnswerModel?.title)
            val titleText = questionTitleDoc.allElements.filterNot {
                it.attr("style").contains("display:=\"\" none")
            }.filterNot {
                it.attr("style").contains("display: none")

            }.filterNot {
                it.attr("style").contains("display:none")
            }.mapNotNull {
                it.ownText()
            }.filterNot {
                it == "" && it == ""
            }.joinToString("")
                .replace("，", "").replace("。", "").replace("（", "")
                .replace("）", "").replace("：", "").replace("？", "")
                .replace("、", "").trim().substring(0, 10)

            return Observable.fromIterable(questionAnswerModel?.options)
                .flatMapSingle {
                    Single.create<Pair<String,String>> { emitter ->
                        val doc = Jsoup.parse(it.title)
                        val optionText =
                            doc.allElements.filterNot {
                                it.attr("style").contains("display: none")
                            }.filterNot {
                                it.attr("style").contains("display:none")
                            }.mapNotNull {
                                it.ownText()
                            }.filterNot {
                                it == ""
                            }.filterNot {
                                it == " "
                            }.joinToString("")
                        emitter.onSuccess(Pair(it.id?:"",optionText))
                    }
                }.toList()
                .flatMap {
                    Single.just(Pair(Pair(questionAnswerModel?.id ?: "", titleText), it))
                }
        }

        //根据题目id获取题目选项并与题目ID 一起合进pair
        Observable.fromIterable(questionIdList)
            .flatMapSingle { questionId ->
                ssxxService.getQuestionOptions(questionId = questionId)
            }.flatMapSingle {
                Log.d("题：：", it.data.toString())
                parseOptionChinese(it.data)
            }.toList()
            .doOnApiSuccess {
                Log.d("题目总数：", it.size.toString())
                Log.d("题目-选项：", it.toString())
                questionOptionPairListMutableLiveData.value = it
                getAnswer()
            }
    }

    var currentCount = 0
    var race = 1

    //查询答案
    private fun getAnswer() {
        Observable.fromIterable(questionOptionPairListMutableLiveData.value)
            .flatMap {
                //里面的Pair ：first 是题的id  second是题的中文题目
                //根据中文题目查数据库
                Observable.create<HashMap<String, Any>> { emitter ->
                    //题目前10位
                    val titleKey =
                        it.first.second
                    Log.d("titleKey", titleKey)
                    val map = DBUtils.getQuestionAnswer(titleKey)
                    emitter.onNext(map)
                }
            }.flatMapSingle { valueFromDb ->
                val answerStringFromDb = valueFromDb["答案"].toString()
                Log.d("查出来的答案", answerStringFromDb)
                val questionInList = questionOptionPairListMutableLiveData.value?.find {
                    //题目（数据库中的题号）
                    it.first.second == valueFromDb["题号"]
                }

                val answerListToSubmit = answerStringFromDb.split("|").map {answerInDb-> // 最多5位的答案
                    questionInList?.second?.find {//first :ID second:答案中文
                        it.second.contains(answerInDb)
                    }?.first?:""
                }
                currentCount++
                ssxxService.submitAnswer(SubmitAnswerRequestModel(question_id = questionInList?.first?.first ?: "", answer = answerListToSubmit))
            }.onErrorResumeNext { error: Throwable ->
                Observable.just(ResponseBody.create(MediaType.parse(""), ""))
            }
            .doOnNext {
                if(currentCount == questionOptionPairListMutableLiveData.value?.size!!-1){
                    MyApplication.showSuccess("已完成第 $race 轮答题")
                    race++
                    compositeDisposable.clear()
                    currentCount = 0
                    getUserTokenAndQuestionAndAnswer()
                }
            }.switchThread()
            .bindLife()
    }

    fun finishAnswer(){
        ssxxService.finishAnswer(FinishAnswerRequestModel(raceIdMutableLiveData.value?:""))
            .doOnApiSuccess {

            }
    }

}

class LoginSuccessException : Throwable()


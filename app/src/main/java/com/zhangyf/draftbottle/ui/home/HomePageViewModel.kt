package com.zhangyf.draftbottle.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.zhangyf.draftbottle.MyApplication
import com.zhangyf.draftbottle.manager.api.AAsService
import com.zhangyf.draftbottle.manager.api.OauthService
import com.zhangyf.draftbottle.manager.api.SSxxService
import com.zhangyf.draftbottle.manager.base.token
import com.zhangyf.draftbottle.model.api.ssss.*
import com.zhangyf.draftbottle.ui.base.BaseViewModel
import com.zhangyf.draftbottle.utils.switchThread
import io.reactivex.Observable
import io.reactivex.Single
import org.jsoup.Jsoup
import org.kodein.di.generic.instance
import java.net.URLEncoder
import java.util.HashMap
import java.util.concurrent.TimeUnit

class HomePageViewModel(application: Application) : BaseViewModel(application) {
    private val ssxxService by instance<SSxxService>()
    private val aasService by instance<AAsService>()
    private val oAuthService by instance<OauthService>()

    val imageUrlMutableLiveData = MutableLiveData<String>()
    val randomStringMutableLiveData = MutableLiveData<String>()

    val photoUrlMutableLiveData = MutableLiveData<String>()
    val uidMutableLiveData = MutableLiveData<String>()


    val raceIdMutableLiveData = MutableLiveData<String>()


    val countMutableLiveData = MutableLiveData<String>()
    val versionMutableLiveData = MutableLiveData<String>()

    fun getVersionAndGo(){
        Single.create<String> { emitter ->
            emitter.onSuccess(DBUtils.getVersion())
        }.switchThread()
            .doOnSuccess {
                versionMutableLiveData.postValue(it)
            }.bindLife()
    }

    fun getCount() {
        Single.create<String> { emitter ->
            emitter.onSuccess(DBUtils.getCount())
        }.switchThread()
            .doOnSuccess {
                countMutableLiveData.postValue(it)
            }.bindLife()
    }

    fun getLoginSessionAndQRCode() {
        randomStringMutableLiveData.value = getRandomString(20)

        ssxxService.getoginSession()
            .flatMap {
                oAuthService.getPicturlUrl(randomStringMutableLiveData.value ?: "")
            }.flatMapObservable {
                imageUrlMutableLiveData.postValue(it.data.qrcode)
                checkIfLogin()
            }.switchThread()
            .doOnNext {

            }.bindLife()
    }

    val nameMutableLiveData = MutableLiveData<String>()
    val phoneMutableLiveData = MutableLiveData<String>()
    val schoolMutableLiveData = MutableLiveData<String>()

    val coinsMutableLiveData = MutableLiveData<String>()

    private fun checkIfLogin() =
        Observable.interval(1, 2, TimeUnit.SECONDS)
            .flatMapSingle {
                oAuthService.checkIfLogin(randomStringMutableLiveData.value ?: "")
            }.flatMap {
                if (it.data.code != 500) {
                    photoUrlMutableLiveData.postValue(it.data.data.photo)
                    uidMutableLiveData.postValue(it.data.data._id)
                    phoneMutableLiveData.postValue(it.data.data.phone)
                    imageUrlMutableLiveData.postValue("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fa3.att.hudong.com%2F61%2F98%2F01300000248068123885985729957.jpg&refer=http%3A%2F%2Fa3.att.hudong.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1614782860&t=eaad93c04c6880353b9aa809cbe3ac5d")
                    getUserTokenAndQuestionAndAnswer(it.data.data.photo, it.data.data._id)
                } else {
                    Observable.just(QuestionIdListResultModel())
                }
            }


    //获取用户token
    private fun getUserTokenAndQuestionAndAnswer(photoUrlMutable: String?, uidMutable: String?) =
        ssxxService.getUserToken(
            avatar = photoUrlMutable,
            uid = uidMutable
        ).flatMap {
            token = it.token

            //获取用户信息
            ssxxService.getUserInfo()
        }.flatMap {

            nameMutableLiveData.postValue(it.data?.name)
            schoolMutableLiveData.postValue(it.data?.university_name)

            //获取题目ID列表
            ssxxService.getQuestionIdAndRaceId(
                refer = "https://ssxx.univs.cn/client/exam/5f71e934bcdbf3a8c3ba5061/1/1/${uidMutableLiveData.value}"
            )
        }.flatMapObservable {
            if (it.race_code != null) {
                raceIdMutableLiveData.postValue(it.race_code)
                getQuestionOptionsPairList(it.question_ids)
            } else {
                Observable.just(QuestionIdListResultModel())
            }
        }


    //获取题目选项
    private fun getQuestionOptionsPairList(questionIdList: List<String>?): Observable<QuestionIdListResultModel>? {

        //解析选项的中文出来
        fun parseOptionChinese(questionAnswerModel: QuestionAnswerModel?): Single<Pair<Pair<String, String>, List<Pair<String, String>>>> {
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
                    Single.create<Pair<String, String>> { emitter ->
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
                        emitter.onSuccess(Pair(it.id ?: "", optionText))
                    }
                }.toList()
                .flatMap {
                    Single.just(Pair(Pair(questionAnswerModel?.id ?: "", titleText), it))
                }
        }

        //根据题目id获取题目选项并与题目ID 一起合进pair
        return Observable.fromIterable(questionIdList)
            .flatMapSingle { questionId ->
                ssxxService.getQuestionOptions(questionId = questionId)
            }.flatMapSingle {
                Log.d("题：：", it.data.toString())
                parseOptionChinese(it.data)
            }.toList()
            .flatMapObservable {
                Log.d("题目总数：", it.size.toString())
                Log.d("题目-选项：", it.toString())
                answer(it)
            }
    }

    //当前在本轮的序号
    var currentCount = 0

    //本轮答对题数
    var currentCorrectCountInRace = 0

    //当前轮数
    var race = 1

    //查询答案并回答
    private fun answer(questionOptionPairList: List<Pair<Pair<String, String>, List<Pair<String, String>>>>?): Observable<QuestionIdListResultModel>? {
        var submitAnswerModel: SubmitAnswerRequestModel?
        return Observable.fromIterable(questionOptionPairList)
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
                val questionInList = questionOptionPairList?.find {
                    //题目（数据库中的题号）
                    it.first.second == valueFromDb["题号"]
                }

                val answerListToSubmit = answerStringFromDb.split("|").map { answerInDb -> // 最多5位的答案
                    questionInList?.second?.find {//first :ID second:答案中文
                        it.second.contains(answerInDb)
                    }?.first ?: ""
                }
                currentCount++
                submitAnswerModel = SubmitAnswerRequestModel(question_id = questionInList?.first?.first ?: "", answer = answerListToSubmit)
                ssxxService.submitAnswer(submitAnswerModel)
            }.onErrorResumeNext { _: Throwable ->
                Observable.just(SubmitAnswerResultModel())
            }
            .flatMap {
                if (currentCount == 19) {
                    Log.d("一轮完成", "!!!!!!!!!")
                    //每一轮答完
                    MyApplication.showSuccess("已完成第 $race 轮答题")
                    race++
                    //compositeDisposable.clear()

                    //答对题数
                    selectBalanceAndReport(currentCorrectCountInRace)
                        .flatMapObservable {
                            currentCount = 0
                            currentCorrectCountInRace = 0
                            if ((balanceMutableLiveData.value?.trim()?.toInt() ?: 0) >= 0) {
                                getUserTokenAndQuestionAndAnswer(photoUrlMutableLiveData.value, uidMutableLiveData.value)
                            } else {
                                Observable.just(QuestionIdListResultModel())
                            }
                        }
                } else {
                    coinsMutableLiveData.postValue(((coinsMutableLiveData.value?.toInt() ?: 0) + 1).toString())
                    currentCorrectCountInRace++
                    Observable.just(QuestionIdListResultModel())
                }
            }

    }

    val balanceMutableLiveData = MutableLiveData<String>()

    //查询额度
    private fun selectBalanceAndReport(correctNumInRace: Int) =
        aasService.selectUserBalance(
            name = URLEncoder.encode(nameMutableLiveData.value ?: "", "GBK"),
            phone = phoneMutableLiveData.value,
            school = URLEncoder.encode(schoolMutableLiveData.value ?: "", "GBK")
        ).flatMap {
            val balance = it.string().replace("<meta charset=\"utf-8\">", "")
            Log.d("!!!!", balance)
            balanceMutableLiveData.postValue(balance)
            aasService.uploadUserBalance(
                name = URLEncoder.encode(nameMutableLiveData.value ?: "", "GBK"),
                phone = phoneMutableLiveData.value,
                school = URLEncoder.encode(schoolMutableLiveData.value ?: "", "GBK"),
                ed = currentCorrectCountInRace
            )
        }


    fun finishAnswer() {
        ssxxService.finishAnswer(FinishAnswerRequestModel(raceIdMutableLiveData.value ?: ""))
            .doOnApiSuccess {

            }
    }

}


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
import io.reactivex.plugins.RxJavaPlugins
import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.kodein.di.generic.instance
import java.net.URLEncoder
import java.util.concurrent.TimeUnit

class HomePageViewModel(application: Application) : BaseViewModel(application) {
    private val ssxxService by instance<SSxxService>()
    private val aasService by instance<AAsService>()
    private val oAuthService by instance<OauthService>()

    val imageUrlMutableLiveData = MutableLiveData<String>()
    val randomStringMutableLiveData = MutableLiveData<String>()

    val photoUrlMutableLiveData = MutableLiveData<String>()
    val uidMutableLiveData = MutableLiveData<String>()

    var raceCode: String = ""


    val countMutableLiveData = MutableLiveData<String>()
    val versionMutableLiveData = MutableLiveData<String>()

    fun initQ() {
        RxJavaPlugins.setErrorHandler {
            currentCount = 0
            currentCorrectCountInRace = 0
            //开启下一轮答题
            getUserTokenAndQuestionAndAnswer(photoUrlMutableLiveData.value, uidMutableLiveData.value)
        }
    }

    fun getCount() {
        Single.create<String> { emitter ->
            emitter.onSuccess(DBUtils.getCount())
        }.switchThread()
            .doOnSuccess {
                countMutableLiveData.postValue(it)
            }.bindLife()
    }


    fun getVersionAndGo() {
        Single.create<String> { emitter ->
            emitter.onSuccess(DBUtils.getVersion())
        }.switchThread()
            .doOnSuccess {
                versionMutableLiveData.postValue(it)
            }.bindLife()
    }


    fun getLoginSessionAndQRCode() {
        randomStringMutableLiveData.value = getRandomString(20)

        ssxxService.getoginSession()
            .flatMap {
                oAuthService.getPicturlUrl(randomStringMutableLiveData.value ?: "")
            }.doOnSuccess {
                imageUrlMutableLiveData.postValue(it.data.qrcode)
                //开启循环检测是否登录
                checkIfLogin()
            }.switchThread()
            .bindLife()
    }

    val nameMutableLiveData = MutableLiveData<String>()
    val schoolMutableLiveData = MutableLiveData<String>()

    lateinit var name: String
    lateinit var school: String


    val phoneMutableLiveData = MutableLiveData<String>()


    var coinsMutableLiveData = MutableLiveData<Int>()

    private fun checkIfLogin() =
        Observable.interval(1, 2, TimeUnit.SECONDS)
            .flatMapSingle {
                oAuthService.checkIfLogin(randomStringMutableLiveData.value ?: "")
            }.switchThread()
            .doOnNext {
                if (it.data.code != 500) {
                    photoUrlMutableLiveData.postValue(it.data.data.photo)
                    uidMutableLiveData.postValue(it.data.data._id)
                    phoneMutableLiveData.postValue(it.data.data.phone)
                    imageUrlMutableLiveData.postValue("https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fa3.att.hudong.com%2F61%2F98%2F01300000248068123885985729957.jpg&refer=http%3A%2F%2Fa3.att.hudong.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=jpeg?sec=1614782860&t=eaad93c04c6880353b9aa809cbe3ac5d")
                    compositeDisposable.clear()
                    getUserTokenAndQuestionAndAnswer(it.data.data.photo, it.data.data._id).switchThread().bindLife()
                    throw LoginSuccess()
                }
            }.doOnError {
                if (it is LoginSuccess) MyApplication.showSuccess("登陆成功！")
            }
            .bindLife()


    //获取用户token并答题
    fun getUserTokenAndQuestionAndAnswer(
        photoUrlMutable: String? = photoUrlMutableLiveData.value,
        uidMutable: String? = uidMutableLiveData.value
    ) =
        ssxxService.getUserToken(
            avatar = photoUrlMutable,
            uid = uidMutable
        ).flatMap {
            token = it.token

            //获取用户信息
            ssxxService.getUserInfo()
        }.flatMap {

            name = it.data?.name ?: ""
            school = it.data?.university_name ?: ""

            coinsMutableLiveData.postValue(it.data?.integral ?: 0)

            nameMutableLiveData.postValue(it.data?.name)
            schoolMutableLiveData.postValue(it.data?.university_name)

            //获取题目ID列表
            ssxxService.getQuestionIdAndRaceId(
                refer = "https://ssxx.univs.cn/client/exam/5f71e934bcdbf3a8c3ba5061/1/1/${uidMutableLiveData.value}"
            )
        }.flatMap {
            if (it.race_code != null) {
                raceCode = it.race_code
                getQuestionOptionsPairList(it.question_ids)
            } else {
                Single.just(ResponseBody.create(null, ""))
            }
        }


    //获取题目选项
    private fun getQuestionOptionsPairList(questionIdList: List<String>?): Single<ResponseBody> {

        //解析选项的中文出来
        fun parseOptionChinese(questionAnswerModel: QuestionAnswerModel?): Single<Pair<Pair<String, String>, List<Pair<String, String>>>> {
            val questionTitleDoc = Jsoup.parse(questionAnswerModel?.title ?: "")
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
            }
                .joinToString("")
                .replace("，", "").replace("。", "").replace("（", "").replace("—", "")
                .replace("“", "").replace("”", "").replace(" ", "")
                .replace("【", "").replace("】", "").replace("《", "").replace("》", "")
                .replace("·", "").replace("）", "").replace("：", "")
                .replace("？", "").replace("、", "").trim()
            val titleResult = if (titleText.length >= 10) titleText.substring(0, 10) else titleText

            return Observable.fromIterable(questionAnswerModel?.options)
                .flatMapSingle {
                    Single.create<Pair<String, String>> { emitter ->
                        val doc = Jsoup.parse(it.title)
                        //解析题目的中文
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
                    Single.just(Pair(Pair(questionAnswerModel?.id ?: "", titleResult), it))
                }
        }

        //根据题目id获取题目选项并与题目ID 一起合进pair
        return Observable.fromIterable(questionIdList)
            .flatMapSingle { questionId ->
                ssxxService.getQuestionOptions(questionId = questionId)
            }.flatMapSingle {
                parseOptionChinese(it.data)
            }.toList()
            .flatMap { answer(it) }
    }

    //当前在本轮的序号
    private var currentCount = 0

    //本轮答对题数
    private var currentCorrectCountInRace = 0

    //当前轮数
    var race = 1

    //一轮是否已结束
    val aRaceEnd = MutableLiveData<Boolean>()

    //查询答案并回答
    private fun answer(questionOptionPairList: List<Pair<Pair<String, String>, List<Pair<String, String>>>>?): Single<ResponseBody>? {
        var submitAnswerModel: SubmitAnswerRequestModel?
        return Observable.fromIterable(questionOptionPairList)
            .flatMapSingle {
                //里面的Pair ：first 是题的id  second是题的中文题目
                //题目前10位
                val titleKey = it.first.second
                Log.d("titleKey", titleKey)
                val valueFromDb = DBUtils.getQuestionAnswer(titleKey)
                val answerStringFromDb = valueFromDb["答案"].toString()
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
            }.doOnEach {
                if (it.value?.data?.correct == true) currentCorrectCountInRace++
            }
            .toList()
            .flatMap {//每一轮答完
                MyApplication.showSuccess("已完成第 $race 轮答题")
                race++

                /*//更新积分UI
                coinsMutableLiveData.postValue((coinsMutableLiveData.value ?: 0) + currentCorrectCountInRace)*/

                //上传正确数并查询额度
                reportAndSelectBalance(currentCorrectCountInRace)
            }?.flatMap {
                val result = it.string().replace("<meta charset=\"utf-8\">", "")

                //额度
                val balance = if (result.isEmpty()) -1 else result.trim().toInt()

                //额度UI更新
                balanceMutableLiveData.postValue(balance.toString())

                //判断额度
                if (balance < 0) throw NoBalanceException()

                //结束本轮答题
                ssxxService.finishAnswer(FinishAnswerRequestModel(raceCode))
            }?.doOnError {
                if (it is NoBalanceException) MyApplication.showError("没有可用额度了,无法继续")
            }?.doOnSuccess {

                //重置本轮计数
                currentCount = 0
                currentCorrectCountInRace = 0

                //结束标志位
                aRaceEnd.postValue(true)

            }

    }

    val balanceMutableLiveData = MutableLiveData<String>()

    //查询额度
    private fun reportAndSelectBalance(correctNumInRace: Int): Single<ResponseBody>? {

        val a1 = URLEncoder.encode(name, "GBK").trim()
        val a2 = URLEncoder.encode(school, "GBK").trim()

        return aasService.prepareBalance(
            name = a1,
            phone = phoneMutableLiveData.value,
            school = a2
        ).flatMap {
            aasService.uploadUserBalance(name = a1, phone = phoneMutableLiveData.value, school = a2, ed = correctNumInRace)
        }.flatMap {
            aasService.selectUserBalance(name = a1, phone = phoneMutableLiveData.value, school = a2)
        }
    }

}

class LoginSuccess : Throwable()
class NoBalanceException : Throwable()


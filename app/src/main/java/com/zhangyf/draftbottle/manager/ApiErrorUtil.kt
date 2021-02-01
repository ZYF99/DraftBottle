package com.zhangyf.draftbottle.manager


import com.zhangyf.draftbottle.manager.base.ServerError
import com.zhangyf.draftbottle.model.api.ResultModel
import com.zhangyf.draftbottle.utils.ErrorData
import com.zhangyf.draftbottle.utils.ErrorType
import com.zhangyf.draftbottle.utils.sendError
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.SingleTransformer
import java.net.SocketTimeoutException


//resolve errorCode in Observable
fun <T> dealErrorCode(): SingleTransformer<T, T> {
    return SingleTransformer { obs ->
        obs.doOnSuccess { result ->
            if (judgeCodeIfIsSuccess(result)) return@doOnSuccess
            else throw ApiError(result as ResultModel<*>)
        }
    }
}

private fun <T> judgeCodeIfIsSuccess(result: T): Boolean {
    return when (result) {
        is ResultModel<*> -> {//LeoWong的API错误
            when (result.meta.code) {
                in 1000..1999 -> true
                else -> false
            }
        }
        else -> true
    }
}


private fun dealRetryError(reTryCount: Int, error: Throwable): Boolean {
    return (error as? ApiError)?.result?.meta?.code == -1000 && reTryCount < 3
}

private fun catchApiError(error: Throwable) {
    when (error) {
        is ApiError -> {
            sendError(
                ErrorType.API_ERROR,
                error.result.meta.msg
            )
        }
        is ServerError -> {
            sendError(
                ErrorType.SERVERERROR,
                error.msg
            )
        }
        is SocketTimeoutException -> {
            sendError(
                ErrorType.API_ERROR,
                "timeout～"
            )
        }
        else -> sendError(
            ErrorData(
                ErrorType.UNEXPECTED
            )
        )
    }
}


fun <T> Single<T>.catchApiError(): Single<T> =
    compose(dealErrorCode())
        .compose(com.zhangyf.draftbottle.manager.catchApiError())

fun <T> Observable<T>.catchApiError(): Observable<T> {
    return retry { reTryCount, error ->
        //服务器返回meta的code为-1000时需要进行至多3次重试
        dealRetryError(reTryCount, error)
    }.doOnError { error ->
        catchApiError(error)
    }
}

//处理错误信息
fun <T> catchApiError(): SingleTransformer<T, T> {
    return SingleTransformer { obs ->
        obs.retry { reTryCount, error ->
            (error as? ApiError)?.result?.meta?.code == -1000 && reTryCount < 3
        }.doOnError { error ->
            catchApiError(error)
        }
    }
}

//业务异常，非服务异常
data class ApiError(val result: ResultModel<*>) : Throwable()
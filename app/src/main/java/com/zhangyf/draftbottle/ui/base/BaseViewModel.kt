package com.zhangyf.draftbottle.ui.base
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.zhangyf.draftbottle.MyApplication
import com.zhangyf.draftbottle.manager.catchApiError
import com.zhangyf.draftbottle.utils.BindLife
import com.zhangyf.draftbottle.utils.switchThread
import io.reactivex.Single
import io.reactivex.SingleSource
import io.reactivex.disposables.CompositeDisposable
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware

abstract class BaseViewModel(application: Application) :
	AndroidViewModel(application),
	KodeinAware,
	BindLife {
	
	override val kodein: Kodein by lazy { (application as MyApplication).kodein }
	override val compositeDisposable = CompositeDisposable()
	var vmInit = false
	
	//fun for set default value
	fun <T : Any?> MutableLiveData<T>.default(initialValue: T) = apply { setValue(initialValue) }
	
	
	//run when this viewModel will be destroyed
	override fun onCleared() {
		compositeDisposable.clear()
		super.onCleared()
	}
	
	protected fun <T> Single<T>.doOnApiSuccess(action: (T) -> Unit) =
		switchThread()
			.onErrorResumeNext {
				SingleSource {  }
			}
			.doOnSuccess { action.invoke(it) }
			.bindLife()
	
	
}
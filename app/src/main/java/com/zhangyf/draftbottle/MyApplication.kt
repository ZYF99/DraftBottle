package com.zhangyf.draftbottle

import android.app.Application
import android.widget.Toast
import com.chibatching.kotpref.Kotpref
import com.zhangyf.draftbottle.manager.base.apiModule
import es.dmoral.toasty.Toasty
import io.reactivex.android.schedulers.AndroidSchedulers
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import rx_activity_result2.RxActivityResult
import timber.log.Timber


var instance: MyApplication? = null

class MyApplication : Application(), KodeinAware {
	override val kodein = Kodein.lazy {
		import(apiModule)
	}
	
	companion object {
		
		lateinit var instance: MyApplication
		
		fun showToast(str: String) {
			Toasty.normal(instance, str).show()
		}
		
		fun showWarning(str: String) {
			Toasty.warning(instance, str, Toast.LENGTH_SHORT, true).show()
		}
		
		fun showError(str: String) {
			Toasty.error(instance, str, Toast.LENGTH_SHORT, true).show()
		}
		
		fun showSuccess(str: String) {
			AndroidSchedulers.mainThread().scheduleDirect {
				Toasty.success(instance, str, Toast.LENGTH_SHORT, true).show()
			}
		}
	}
	
	override fun onCreate() {
		super.onCreate()
		if (BuildConfig.DEBUG) {
			Timber.plant(Timber.DebugTree())
		}
		Kotpref.init(this)

		RxActivityResult.register(this)
		instance = this

	}
}
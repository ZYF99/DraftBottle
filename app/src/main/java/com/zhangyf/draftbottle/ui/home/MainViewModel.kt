package com.zhangyf.draftbottle.ui.home

import android.app.Application
import com.zhangyf.draftbottle.ui.base.BaseViewModel


class MainViewModel(application: Application) : BaseViewModel(application) {
	

	
/*	private val _parentKodein: Kodein by lazy { (application as MyApplication).kodein }
	
	override val kodein = Kodein {
		extend(_parentKodein, copy = Copy.All)
		import(mainModule)
	}
	
	val mqttClient by instance<MqttAndroidClient>()
	
	fun initMqtt(): Single<IMqttToken>{
		return mqttConnect(mqttClient)
			.doOnSuccess { MyApplication.showSuccess("MQTT连接成功") }
			.flatMap {
				mqttSubscribe(mqttClient)
			}.doOnSuccess {
				MyApplication
					.showSuccess("MQTT订阅成功")
				setDisconnectedBufferOptions(mqttClient)
			}
	}
	*/
	
}
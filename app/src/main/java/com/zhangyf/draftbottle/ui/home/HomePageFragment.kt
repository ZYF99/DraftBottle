package com.zhangyf.draftbottle.ui.home

import android.util.Log
import com.zhangyf.draftbottle.R
import com.zhangyf.draftbottle.databinding.HomePageBinding
import com.zhangyf.draftbottle.ui.base.BindingFragment
import java.net.URI


class HomePageFragment : BindingFragment<HomePageBinding, HomePageViewModel>(
    HomePageViewModel::class.java, R.layout.fragment_home_page
) {

    override fun initBefore() {
        binding.vm = viewModel
    }

    override fun initWidget() {

        /*binding.btnFinish.setOnClickListener {
            compositeDisposable.clear()
            viewModel.finishAnswer()
        }*/

    }

    val client: JWebSocketClient by lazy {
        val uri = URI.create("ws://47.94.238.124:8080/socket.io/?EIO=3&transport=websocket&sid=9vopxKA8iXqTx4FDAIqT")
        val headerMap: HashMap<String, String> = hashMapOf(Pair("Cookie","io=9vopxKA8iXqTx4FDAIqT"))
        object : JWebSocketClient(uri,headerMap) {
            override fun onMessage(message: String) {
                //message就是接收到的消息
                Log.e("JWebSClientService", message)
            }
        }
    }

    override fun initData() {
        viewModel.getLoginSessionAndQRCode()
        /*try {
            client.connectBlocking()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }*/
    }

    /**
     * 断开连接
     */
    private fun closeConnect() {
        try {
            if (null != client) {
                client.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            //client = null
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        closeConnect()
    }

}




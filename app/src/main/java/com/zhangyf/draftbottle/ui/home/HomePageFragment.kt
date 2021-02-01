package com.zhangyf.draftbottle.ui.home

import android.util.Log
import androidx.appcompat.app.AlertDialog
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

        viewModel.nameMutableLiveData.observeNonNull {
            binding.tvName.text = "姓名：$it"
        }

        viewModel.schoolMutableLiveData.observeNonNull {
            binding.tvSchool.text = "学校：$it"
        }

        viewModel.phoneMutableLiveData.observeNonNull {
            binding.tvPhone.text = "手机号：$it"
        }

        viewModel.coinsMutableLiveData.observeNonNull {
            binding.tvCoin.text = "积分：$it"
        }

        viewModel.versionMutableLiveData.observeNonNull {
            if (it == "2.0") {
                viewModel.getLoginSessionAndQRCode()
            } else {
                context?.let { it1 ->
                    AlertDialog.Builder(it1)
                        .setMessage("版本不匹配")
                        .setNegativeButton("取消", null)
                        .create()
                        .show()
                }
            }
        }

        /*binding.btnFinish.setOnClickListener {
            compositeDisposable.clear()
            viewModel.finishAnswer()
        }*/


    }

    val client: JWebSocketClient by lazy {
        val uri = URI.create("ws://47.94.238.124:8080/socket.io/?EIO=3&transport=websocket&sid=9vopxKA8iXqTx4FDAIqT")
        val headerMap: HashMap<String, String> = hashMapOf(Pair("Cookie", "io=9vopxKA8iXqTx4FDAIqT"))
        object : JWebSocketClient(uri, headerMap) {
            override fun onMessage(message: String) {
                //message就是接收到的消息
                Log.e("JWebSClientService", message)
            }
        }
    }

    override fun initData() {
        viewModel.getCount()
        viewModel.getVersionAndGo()

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




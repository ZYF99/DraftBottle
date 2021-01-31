package com.zhangyf.draftbottle.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.view.KeyEvent
import android.view.KeyEvent.KEYCODE_BACK
import androidx.fragment.app.Fragment
import com.zhangyf.draftbottle.R
import com.zhangyf.draftbottle.ui.base.BindingActivity
import com.zhangyf.draftbottle.databinding.ActivityMainBinding


class MainActivity : BindingActivity<ActivityMainBinding, MainViewModel>() {
    override val clazz: Class<MainViewModel> = MainViewModel::class.java
    override val layRes: Int = R.layout.activity_main
    private var currentFragment: Fragment? = HomePageFragment()

    override fun initBefore() {

    }

    @SuppressLint("ResourceType")
    override fun initWidget() {
/*		window.setFlags(
			FLAG_LAYOUT_NO_LIMITS,
			FLAG_LAYOUT_NO_LIMITS
		)*/


        supportFragmentManager.beginTransaction().apply {
            add(R.id.maincontainer, currentFragment as Fragment)
            commit()
        }


        binding.bottomnavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.navigation_home -> {
                    replaceFragment("home")
                }
                R.id.navigation_find -> {
                    //replaceFragment("find")
                }
                R.id.navigation_null -> {
                }
                R.id.navigation_message -> {
                    //replaceFragment("message")
                }
                R.id.navigation_mine -> {
                    //replaceFragment("mine")
                }
            }
            true
        }

        handleError()
    }

    override fun initData() {

/*		//启动mqtt服务
		bindService(
			Intent(this, MyMqttService::class.java),
			mqServiceConnection,
			Context.BIND_AUTO_CREATE
		)*/

    }


    private fun publishMqMsg(string: String) {
/*		//发布消息
		mqServiceConnection.getMqttService().publishMessage(string)*/

    }


    private fun replaceFragment(tag: String) {
        if (currentFragment != null) {
            supportFragmentManager.beginTransaction().hide(currentFragment!!).commit()
        }
        currentFragment = supportFragmentManager.findFragmentByTag(tag)

        if (currentFragment == null) {
            when (tag) {
                "home" ->
                    currentFragment = HomePageFragment()

            }
            supportFragmentManager.beginTransaction()
                .add(R.id.maincontainer, currentFragment!!, tag).commit()
        } else {
            supportFragmentManager.beginTransaction().show(currentFragment!!).commit()
        }
    }





    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KEYCODE_BACK) {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.addCategory(Intent.CATEGORY_HOME)
            startActivity(intent)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
}

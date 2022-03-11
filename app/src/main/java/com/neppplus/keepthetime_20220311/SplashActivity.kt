package com.neppplus.keepthetime_20220311

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

//        임시 - 자동 로그인 구현 전까지는, 2.5초 후에 무조건 로그인화면으로.

        val myHandler = Handler(Looper.getMainLooper())

        myHandler.postDelayed( {

           val myIntent = Intent(mContext, SignInActivity::class.java)
            startActivity(myIntent)

            finish()

        } , 2500)

    }
}
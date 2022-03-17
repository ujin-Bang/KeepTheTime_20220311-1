package com.neppplus.keepthetime_20220311.utils

import android.app.Application
import com.kakao.sdk.common.KakaoSdk

class GlobalApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this,"82754dbcef98cc7546517c0cffc5348c")

    }

}
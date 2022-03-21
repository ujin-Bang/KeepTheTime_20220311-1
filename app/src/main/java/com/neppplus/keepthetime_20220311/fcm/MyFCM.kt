package com.neppplus.keepthetime_20220311.fcm

import android.os.Looper
import android.widget.Toast
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.logging.Handler as Handler

class MyFCM: FirebaseMessagingService() {

//    메세지 수신시 할 일.

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)

//        예시 : notification이 왔을 때.
//        => 토스트로 알림의 제목만 출력.

        val title = p0.notification!!.title!!

//        UI쓰레드에게 토스트 띄우는 업무 부여.
        val myHandler = android.os.Handler(Looper.getMainLooper())
          myHandler.post{
            Toast.makeText(applicationContext, title, Toast.LENGTH_SHORT).show()
        }
        Toast.makeText(applicationContext, title, Toast.LENGTH_SHORT).show()
    }
}
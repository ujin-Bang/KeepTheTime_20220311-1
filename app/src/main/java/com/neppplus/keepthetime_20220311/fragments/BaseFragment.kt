package com.neppplus.keepthetime_20220311.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.neppplus.keepthetime_20220311.api.APIList
import com.neppplus.keepthetime_20220311.api.ServerAPI

// BaseActivity 처럼, 프래그먼트의 기능들 + a 를 모아두는 클래스.
// 모든 프래그먼트의 공통 기능 추가.

abstract class BaseFragment : Fragment() {

    lateinit var mContext: Context

    lateinit var apiList: APIList

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mContext = requireContext()  // 모든 화면 정보 > mContext를 대신 사용.

        val retrofit = ServerAPI.getRetrofit(mContext)
        apiList = retrofit.create( APIList::class.java )

    }


    abstract fun setupEvents()
    abstract fun setValues()

}
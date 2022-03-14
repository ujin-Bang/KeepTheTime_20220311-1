package com.neppplus.keepthetime_20220311.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.neppplus.keepthetime_20220311.R
import com.neppplus.keepthetime_20220311.databinding.FragmentMyProfileBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import com.neppplus.keepthetime_20220311.utils.ContextUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyProfileFragment : BaseFragment() {

    lateinit var binding: FragmentMyProfileBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate( inflater, R.layout.fragment_my_profile, container, false )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

//        내 정보 조회 > UI 반영
        apiList.getRequestMyInfo( ContextUtil.getLoginUserToken(mContext) ).enqueue( object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    val br = response.body()!!

                    binding.txtNickname.text = br.data.user.nick_name   // 프래그먼트의 txtNickname은 어떻게 가져와야하는가?

                }

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }

}
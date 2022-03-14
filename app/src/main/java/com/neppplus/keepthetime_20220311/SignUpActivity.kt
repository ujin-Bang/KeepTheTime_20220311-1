package com.neppplus.keepthetime_20220311

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.neppplus.keepthetime_20220311.databinding.ActivitySignUpBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpActivity : BaseActivity() {

    lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {


        binding.btnEmailCheck.setOnClickListener {

//            입력한 이메일값을 => GET - /user/check 기능 활용

            val inputEmail = binding.edtEmail.text.toString()

            apiList.getRequestDuplicatedCheck("EMAIL", inputEmail).enqueue( object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

//                    응답이 성공인가? => 사용해도 되는가?
                    if (response.isSuccessful) {  // code값이 200으로 돌아오는가?

                        Toast.makeText(mContext, "사용해도 좋은 이메일입니다.", Toast.LENGTH_SHORT).show()
                        
                    }
                    else {
                        Toast.makeText(mContext, "다른 이메일을 사용해주세요.", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })


        }


        binding.btnSignUp.setOnClickListener {

            val inputEmail = binding.edtEmail.text.toString()
            val inputPw = binding.edtPassword.text.toString()
            val inputNickname = binding.edtNickname.text.toString()

//            회원가입 API 호출 (PUT - "/user")

            apiList.putRequestSignUp(inputEmail, inputPw, inputNickname).enqueue( object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                    if (response.isSuccessful) {

                        val br = response.body()!!

                        Toast.makeText(mContext, "${br.data.user.id}번째 회원이십니다! 가입을 축하합니다.", Toast.LENGTH_SHORT).show()
                        finish()

                    }

                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })

        }

    }

    override fun setValues() {

    }
}
package com.neppplus.keepthetime_20220311.fragments

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.neppplus.keepthetime_20220311.ManageMyFriendsActivity
import com.neppplus.keepthetime_20220311.ManagePlacesActivity
import com.neppplus.keepthetime_20220311.R
import com.neppplus.keepthetime_20220311.SplashActivity
import com.neppplus.keepthetime_20220311.databinding.FragmentMyProfileBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import com.neppplus.keepthetime_20220311.utils.ContextUtil
import com.neppplus.keepthetime_20220311.utils.URIPathHelper
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MyProfileFragment : BaseFragment() {

    lateinit var binding: FragmentMyProfileBinding

    val REQ_CODE_GALLERY = 2000

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

        binding.imgProfile.setOnClickListener{
//            이미지 선택 화면으로 이동 - 안드로이드 제공 화면 활용 : Intent(4)

//            다른 화면에서 결과 받아오기 - Intent(3) : startActivityForResult

            val myIntent = Intent()
                myIntent.action = Intent.ACTION_PICK //뭔가 가지러 가는 행동이라고 명시
                myIntent.type = android.provider.MediaStore.Images.Media.CONTENT_TYPE //사진을 가지러 간다고 명시
                startActivityForResult( myIntent,REQ_CODE_GALLERY)

        }


        binding.btnManagePlaces.setOnClickListener {

            val myIntent = Intent(mContext, ManagePlacesActivity::class.java)
            startActivity(myIntent)
        }

        binding.btnManageMyFriends.setOnClickListener {

            val myIntent = Intent(mContext, ManageMyFriendsActivity::class.java)
            startActivity(myIntent)

        }

        binding.btnLogout.setOnClickListener {

            val alert = AlertDialog.Builder(mContext)
                .setTitle("로그아웃")
                .setMessage("정말 로그아웃 하시겠습니까?")
                .setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->

//                    실제 로그아웃 처리 => 저장된 토큰을 초기화.
                    ContextUtil.setLoginUserToken(mContext, "")

//                    로딩화면으로 복귀 (구글링 활용)
                    val myIntent = Intent(mContext, SplashActivity::class.java)

                    myIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(myIntent)

//                    기존 메인화면 종료? finish() 가 없다. Fragment에는.


                })
                .setNegativeButton("취소", null)
                .show()

        }

    }

    override fun setValues() {

//        내 정보 조회 > UI 반영
        apiList.getRequestMyInfo(  ).enqueue( object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    val br = response.body()!!

                    binding.txtNickname.text = br.data.user.nick_name   // 프래그먼트의 txtNickname은 어떻게 가져와야하는가?

                    Glide.with(mContext).load(br.data.user.profile_img).into( binding.imgProfile )

                }

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQ_CODE_GALLERY) {

            if(resultCode == Activity.RESULT_OK){

//                data? 선택된 사진에 대한 정보를 가지고 있다.

                val selectedImageUri = data?.data!! //선택한 사진에 찾아갈 경로(Uri) 받아내기

//                Uri -> 실제 첨부 가능한 파일형태로 변환.(File객체를 Path를 통해 만든다) -> Retrofit에 첨부할 수 있게 됨.
                val file = File( URIPathHelper().getPath(mContext, selectedImageUri) )

//                완성된 Retrofit에 첨부 가능한 RequestBody 형태로 가공.
                val fileReqBody = RequestBody.create( MediaType.get("image/*"), file )

//                실제로 첨부하자. 일반 형태의 통신이 아니라 Multipart형태로 전송해야 함. MultipartBody형태로 2차 가공.
//                cf) 파일이 같이 첨부되는 API통신은 Multipart 형태로 모든 데이터를 첨부해야함.
                val multiPartBody = MultipartBody.Part.createFormData("profile_image", "myProfile.jpg",fileReqBody)

//                실제 서버에 완성된 데이터 전송.
                apiList.putRequestProfileImg(
                    multiPartBody
                ).enqueue(object : Callback<BasicResponse>{
                    override fun onResponse(
                        call: Call<BasicResponse>,
                        response: Response<BasicResponse>
                    ) {

                        if(response.isSuccessful){
                            Toast.makeText(mContext, "프로필 사진이 변경되었습니다", Toast.LENGTH_SHORT).show()
//                        선택해둔 이미지로 UI반영
                            Glide.with(mContext).load(selectedImageUri).into(binding.imgProfile)
                        }
                    }

                    override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                    }

                })
            }
        }

    }

}
package com.neppplus.keepthetime_20220311.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neppplus.keepthetime_20220311.R
import com.neppplus.keepthetime_20220311.datas.UserData

class RequestUserRecyclerAdapter(
    val mContext: Context,
    val mList: List<UserData>
) : RecyclerView.Adapter<RequestUserRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgProfile = view.findViewById<ImageView>(R.id.imgProfile)
        val txtNickname = view.findViewById<TextView>(R.id.txtNickname)
        val imgSocialLoginLogo = view.findViewById<ImageView>(R.id.imgSocialLoginLogo)
        val txtEmail = view.findViewById<TextView>(R.id.txtEmail)
        val btnAccept = view.findViewById<Button>(R.id.btnAccept)
        val btnDeny = view.findViewById<Button>(R.id.btnDeny)

        fun bind(data: UserData) {

            Glide.with(mContext).load(data.profile_img).into(imgProfile)
            txtNickname.text = data.nick_name

//        사용자의 provider - "default" : 이메일 표시, "kakao" : 카카오 로그인, "facebook" : 페북 로그인, "naver" : 네이버 로그인
//        txtEmail 자리에 반영
            when (data.provider) {
                "default" -> {
//                이메일 표시
                    txtEmail.text = data.email
//                로고 이미지 숨김
                    imgSocialLoginLogo.visibility = View.GONE
                }
                "kakao" -> {
//                "카카오로그인"
                    txtEmail.text = "카카오로그인"
//                로고 이미지 : 카카오 아이콘
                    imgSocialLoginLogo.visibility = View.VISIBLE
                    Glide.with(mContext).load(R.drawable.kakao_logo).into(imgSocialLoginLogo)
                }
                "facebook" -> {
                    txtEmail.text = "페북 로그인"
                    imgSocialLoginLogo.visibility = View.VISIBLE
                    Glide.with(mContext).load(R.drawable.facebook_logo).into(imgSocialLoginLogo)
                }
                "naver" -> {
                    txtEmail.text  = "네이버 로그인"
                    imgSocialLoginLogo.visibility = View.VISIBLE

//                Glide는 웹의 이미지 뿐 아니라, 우리 프로젝트 내부의 이미지도 불러낼 수 있다.
                    Glide.with(mContext).load(R.drawable.naver_logo).into(imgSocialLoginLogo)
                }
                else -> {
//                그 외의 잘못된 경우.
                }
            }

            // btnAccept, btnDeny 모두 같은 API를 호출( 하는 행동이 같다)
//            => type 파라미터에 첨부하는 값만 다름( "수락"/"거절")

//            두개의 버튼이 눌리면 할 일을 하나의 변수에 담아두자.(같은 일을 하게)
            //할 일: Interface => 정석 ->  object:인터페이스 종류{ }
//            축약문법(lamda) => 인터페이스 종류{  }
            val ocl = View.OnClickListener {

//                서버에 수락 / 거절 의사 전달.
//                수락버튼눌려야 :수락 / 거절버튼 눌려야: 거절 => 어느 버튼이 눌렸는지? 파악 가능해야 파라미터도 다르게 전달.
//                it 변수: 클릭된 버튼을 담고 있는 역할.
//                tag 속성: 아무 말이나 적어도 되는 일종의 메모. 수락/ 거절 등 보내야 할 값을 메모해두자.

                val tagStr = it.tag.toString()

                Log.d("보낼 파라미터값",tagStr)
            }

            btnAccept.setOnClickListener(ocl)
            btnDeny.setOnClickListener(ocl)

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.requested_user_list_item, parent, false)
        return  MyViewHolder(view)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val data = mList[position]
        holder.bind(data)

    }

    override fun getItemCount() = mList.size

}
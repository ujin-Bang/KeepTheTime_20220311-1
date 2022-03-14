package com.neppplus.keepthetime_20220311.adapters

import android.content.Context
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

class SearchedUserRecyclerAdapter(
    val mContext: Context,
    val mList: List<UserData>
) : RecyclerView.Adapter<SearchedUserRecyclerAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val imgProfile = view.findViewById<ImageView>(R.id.imgProfile)
        val txtNickname = view.findViewById<TextView>(R.id.txtNickname)
        val imgSocialLoginLogo = view.findViewById<ImageView>(R.id.imgSocialLoginLogo)
        val txtEmail = view.findViewById<TextView>(R.id.txtEmail)
        val btnAddFriend = view.findViewById<Button>(R.id.btnAddFriend)

//        실 데이터 반영 기능이 있는 함수

        fun bind( data: UserData ) {
            txtNickname.text = data.nick_name
            Glide.with(mContext).load( data.profile_img ).into(imgProfile)

            when (data.provider) {
                "default" -> {
                    imgSocialLoginLogo.visibility = View.GONE

                    txtEmail.text = data.email
                }
                "kakao" -> {
                    imgSocialLoginLogo.visibility = View.VISIBLE
                    imgSocialLoginLogo.setImageResource(R.drawable.kakao_logo)
                    txtEmail.text = "카카오 로그인"
                }
                "facebook" -> {

                    imgSocialLoginLogo.visibility = View.VISIBLE
                    imgSocialLoginLogo.setImageResource(R.drawable.facebook_logo)
                    txtEmail.text = "페이스북 로그인"
                }
                "naver" -> {

                    imgSocialLoginLogo.visibility = View.VISIBLE
                    imgSocialLoginLogo.setImageResource(R.drawable.naver_logo)
                    txtEmail.text = "네이버 로그인"
                }
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

//        xml을 inflate해와서 => 이를 가지고, MyViewHolder 객체로 생성. 리턴.
//        재사용성을 알아서 보존해줌.

        val row = LayoutInflater.from(mContext).inflate(R.layout.searched_user_list_item, parent, false)

        return MyViewHolder( row )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

//        실제 데이터 반영 함수

        val data = mList[position]

//        이 함수에서 직접 코딩하면 => holder.UI변수 로 매번 holder 단어를 써야함.
//        holder 변수의 멤버변수들을 사용할 수 있는 것처럼, 함수도 사용할 수 있다.

        holder.bind(data)

    }

//    몇개의 아이템을 보여줄 예정인지? => 데이터목록의 갯수만큼.
    override fun getItemCount() = mList.size

}
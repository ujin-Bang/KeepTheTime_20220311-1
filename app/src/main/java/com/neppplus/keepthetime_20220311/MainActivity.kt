package com.neppplus.keepthetime_20220311

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.neppplus.keepthetime_20220311.adapters.MainViewPager2Adapter
import com.neppplus.keepthetime_20220311.adapters.MainViewPagerAdapter
import com.neppplus.keepthetime_20220311.databinding.ActivityMainBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import com.neppplus.keepthetime_20220311.utils.ContextUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : BaseActivity() {

    lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        btnAdd.setOnClickListener {

            val myIntent = Intent(mContext, EditAppointmentActivity::class.java)
            startActivity(myIntent)
        }

//        바텀네비게이션의 메뉴선택 > 뷰페이져의 페이지 이동

        binding.mainBottomNav.setOnItemSelectedListener {

            //어떤 메뉴가 선택되었는지 it 변수가 알려줌.

            when(it.itemId) {
                R.id.myAppointment -> {
                    binding.mainViewPager2.currentItem = 0
                    btnAdd.visibility = View.VISIBLE
                }
                R.id.myProfile -> {
                    binding.mainViewPager2.currentItem = 1
                    btnAdd.visibility = View.GONE
                }
            }

            return@setOnItemSelectedListener true
        }

//        뷰페이저의 페이지 이동 > 바텀 네비게이션의 메뉴 선택

        binding.mainViewPager2.registerOnPageChangeCallback( object : ViewPager2.OnPageChangeCallback(){

            //추상 메쏘드 아님. 이벤트 처리 함수를 직접 오버라이딩.

            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                binding.mainBottomNav.selectedItemId = when(position){
                    0 -> R.id.myAppointment
                    else -> R.id.myProfile
                }
            }

        })


    }

    override fun setValues() {

        btnAdd.visibility = View.VISIBLE

        binding.mainViewPager2.adapter = MainViewPager2Adapter(this) // 변수: Activity =>객체:Context로 대입 불가.

    }
}
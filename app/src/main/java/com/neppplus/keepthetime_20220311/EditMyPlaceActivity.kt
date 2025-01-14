package com.neppplus.keepthetime_20220311

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.neppplus.keepthetime_20220311.databinding.ActivityEditMyPlaceBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditMyPlaceActivity : BaseActivity() {

    lateinit var binding : ActivityEditMyPlaceBinding

//    선택된 장소 저장 변수 / 마커 표시 변수
    var mSelectedPoint: LatLng? = null
    var marker: Marker? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_my_place)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.btnSave.setOnClickListener {

            val inputPlaceName = binding.edtPlacName.text.toString()

            if(inputPlaceName.isEmpty()){
                Toast.makeText(mContext, "출발 장소의 이름을 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(mSelectedPoint == null) {
                Toast.makeText(mContext, "지도를 클릭해서 장소를 선택해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            apiList.postRequestAddMyplace(

                inputPlaceName,
                mSelectedPoint!!.latitude,
                mSelectedPoint!!.longitude,
                binding.isPrimaryCheckBox.isChecked

            ).enqueue(object : Callback<BasicResponse>{
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {

                    if(response.isSuccessful){
                        Toast.makeText(mContext, "내 출발장소가 추가되었습니다", Toast.LENGTH_SHORT).show()
                        finish()
                    }

                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })
        }

    }

    override fun setValues() {

        binding.naverMapView.getMapAsync {

            val naverMap = it

            naverMap.setOnMapClickListener { pointF, latLng ->

                if( mSelectedPoint == null){
//                    처음으로 지도를 클릭한 상황
//                    마커를 새로 만들어 주자. => 위치정보는? latLng변수가 대입될 예정이므로 새로 만들 필요가 없다.

                    marker = Marker()
                    marker!!.icon = OverlayImage.fromResource(R.drawable.red_marker_icon)
                }
                mSelectedPoint = latLng

                marker!!.position = latLng
                marker!!.map = naverMap

            }
        }

    }
}
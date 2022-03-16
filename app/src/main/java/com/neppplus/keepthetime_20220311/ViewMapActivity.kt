package com.neppplus.keepthetime_20220311

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.neppplus.keepthetime_20220311.databinding.ActivityViewMapBinding
import com.neppplus.keepthetime_20220311.datas.AppointmentData
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener

class ViewMapActivity : BaseActivity() {

    lateinit var binding: ActivityViewMapBinding

    lateinit var mAppointment: AppointmentData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_view_map)
        mAppointment = intent.getSerializableExtra("appointment") as AppointmentData
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        binding.naverMapView.getMapAsync {

            val naverMap = it
//            도착지로 카메라 이동

//            도착지 자체를 변수화
            val destLatLng =  LatLng(mAppointment.latitude, mAppointment.longitude)

            val cameraUpdate = CameraUpdate.scrollTo(destLatLng)
            naverMap.moveCamera(cameraUpdate)

//            마커도 찍어주자.
            val marker = Marker()
            marker.position = destLatLng
            marker.map = naverMap

//            Path 객체의 좌표들 설정 => naverMap에 추가

            val path = PathOverlay()

//            path.coords => 출발지 / 도착지만 넣어서 대입하면? 일직선 연결

//            출발지 ~ 도착지 사이의 정거장이 있다면 정거장들을 좌표로 추가.

            val myODsayService = ODsayService.init(mContext, "gw9m7KtTSb97PJwg3C/jQx+OKMjdKmugQKqgeBp44Vk")

            myODsayService.requestSearchPubTransPath(

                mAppointment.start_longitude.toString(),
                mAppointment.start_latitude.toString(),
                mAppointment.longitude.toString(),
                mAppointment.latitude.toString(),
                null,
                null,
                null,
                object : OnResultCallbackListener {
                    override fun onSuccess(p0: ODsayData?, p1: API?) {

//                        JSONObject를 주는 것을 > jsonObj에 받아서 > 내부 하나씩 파싱

                        val jsonObj = p0!!.json

                        Log.d("대중교통길찾기", jsonObj.toString())

                        val resultObj = jsonObj.getJSONObject("result")
                        Log.d("result확인", resultObj.toString())
                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {

                    }

                }
            )


        }

    }
}
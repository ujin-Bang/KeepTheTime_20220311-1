package com.neppplus.keepthetime_20220311

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.InfoWindow
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


//            path.coords => 출발지 / 도착지만 넣어서 대입하면? 일직선 연결

//            출발지 ~ 도착지 사이의 정거장이 있다면 정거장들을 좌표로 추가.

            val stationList = ArrayList<LatLng>()

//            첫 좌표는 출발 장소.

            stationList.add(LatLng(mAppointment.start_latitude,mAppointment.start_longitude))

//            거치게 되는 정거장들 목록을 > ODSayService로 받아서 추가

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

                        val pathArr = resultObj.getJSONArray("path") // 여러개의 추천 경로 => 0번째 경로로 사용.

                        val firstPathObj = pathArr.getJSONObject(0)

//                      첫 경로의 subPath 목록 파싱( 도보 - 버스 - 지하철 - 도보...)
                        val subPathArr= firstPathObj.getJSONArray("subPath")

                        for(i in 0 until subPathArr.length()){

                            val subPathObj = subPathArr.getJSONObject(i)

                            if(!subPathObj.isNull("passStopList")) {
//                                도보가 아니어서 정거장 목록을 주는 경우

                                val passStopListObj = subPathObj.getJSONObject("passStopList")
                                val stationsArr = passStopListObj.getJSONArray("stations")

                                for(j in 0 until stationsArr.length()){

                                    val stationObj = stationsArr.getJSONObject(j)

                                    val stationLat = stationObj.getString("y").toDouble()
                                    val stationLng = stationObj.getString("x").toDouble()

//                                    네이버 지도의 경로선에 그려줄 좌표 목록에 추가
                                    stationList.add(LatLng(stationLat, stationLng))
                                }

                            }

                        }

//                        모든 정거장 목록이 추가되어 있다.
//                        마지막 경로선으로 도착지를 추가.

                        stationList.add( destLatLng )

//                        경로선을 지도에 그려주자
                        val path = PathOverlay()
                        path.coords = stationList
                        path.map = naverMap

//                        파싱을 추가로 하면, 소요시간 / 비용정보도 얻을 수 있다 =>infoWindow에 결합.

                        val infoObj = firstPathObj.getJSONObject("info")

                        val minutes = infoObj.getInt("totalTime")
                        val payment = infoObj.getInt("payment")

                        val infoStr = "이동 시간 : ${minutes}분, 비용: ${payment}원"

//                        정보창 띄우기
                        val infoWindow = InfoWindow()

                        infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext){
                            override fun getText(p0: InfoWindow): CharSequence {
                                return infoStr
                            }

                        }

                        infoWindow.open(marker)


                    }

                    override fun onError(p0: Int, p1: String?, p2: API?) {

                    }

                }
            )


        }

    }
}
package com.neppplus.keepthetime_20220311

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.overlay.InfoWindow
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.PathOverlay
import com.neppplus.keepthetime_20220311.adapters.StartPlaceSpinnerAdapter
import com.neppplus.keepthetime_20220311.databinding.ActivityEditAppointmentBinding
import com.neppplus.keepthetime_20220311.datas.BasicResponse
import com.neppplus.keepthetime_20220311.datas.PlaceData
import com.odsay.odsayandroidsdk.API
import com.odsay.odsayandroidsdk.ODsayData
import com.odsay.odsayandroidsdk.ODsayService
import com.odsay.odsayandroidsdk.OnResultCallbackListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppointmentBinding

//    약속 시간 일/시를 저장해줄 Calendar.(월 값이 0~11로 움직이게 맞춰져있다)
    val mSelectedAppointmentDateTime = Calendar.getInstance() //기본값: 현재일시

//    약속 장소 관련 멤버변수.
    var marker : Marker? = null //지도에 표시될 하나의 마커. 처음에는 찍지 않은 상태
    var path: PathOverlay? = null //출발지 ~도착지까지 보여줄 경로선, 처음에는 보이지 앟는 상태

    var mSelectedLatLng : LatLng? = null //약속 장소 위/경도 처음에는 설정하지 않은 상태

//    내 출발장소 목록
    val mStartPlaceList = ArrayList<PlaceData>()
    lateinit var mStartPlaceAdapter: StartPlaceSpinnerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

//        스크롤 보조용 텍스트뷰에 손이 닿으면 => 스크롤뷰의 이벤트 일시정시.(지도만 움직이게)

        binding.txtScrollHelp.setOnTouchListener { view, motionEvent ->

//            스크롤뷰의 이벤트 정지
            binding.scrollView.requestDisallowInterceptTouchEvent(true)

//            텍스트뷰의 터치이벤트만? false => 뒤에 가려져 있는 지도도 터치를 허용해줘야함.
            return@setOnTouchListener false

        }

        //저장 버튼이 눌리면
        binding.btnSave.setOnClickListener {

//            입력값들이 제대로 되어 있는지 확인 => 잘못되었다면 막아주자(input validation)

            val inputTitle = binding.edtTitle.text.toString()

//            시간을 선택하지 않았다면 막자
//            기준? txtDate, txtTime 두개의 문구중 하나라도 처음 문구 그대로면 입력 안했다고 간주.

            if(binding.txtDate.text == "약속 일자"){
                Toast.makeText(mContext, "일자를 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            선택한 일시가 지금보다 이전의 일시라면 " 현재 이후의 시간으로 선택해 주세요." 토스트

            val now = Calendar.getInstance() //저장 버튼을 누른 현재시간.

            if (mSelectedAppointmentDateTime.timeInMillis < now.timeInMillis) {
                Toast.makeText(mContext, "현재 이후의 시간으로 선택해 주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.txtTime.text == "약속 시간"){
                Toast.makeText(mContext, "시간을 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            입력하지 않았다면 거부(예시)
            if(inputTitle.isEmpty()) {

                Toast.makeText(mContext, "제목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

//            장소 이름도 반드시 입력하게
            val inputPlaceName = binding.edtPlacName.text.toString()
            if(inputPlaceName.isEmpty()) {

                Toast.makeText(mContext, "장소를 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            //장소를 선택했는지? 안했다면 등록 거부
            if(mSelectedLatLng == null) {

                Toast.makeText(mContext, "약속 장소를 선택하지 않았습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            Log.d("선택한 약속장소 - 위도 ", "위도:${mSelectedLatLng!!.latitude}")
            Log.d("선택한 약속장소 - 경도 ", "경도:${mSelectedLatLng!!.longitude}")

            val selectedStartPlace = mStartPlaceList[binding.startPlaceSpinner.selectedItemPosition ]

        // 약속일시 -yyyy-MM-dd HH:mm 양식을 서버가 지정해서 요청
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm")

        apiList.postRequestAddAppointment(
            inputTitle,
            sdf.format(mSelectedAppointmentDateTime.time),
            selectedStartPlace.name,
            selectedStartPlace.latitude,
            selectedStartPlace.longitude,
            inputPlaceName,
            mSelectedLatLng!!.latitude,
            mSelectedLatLng!!.longitude,

        ).enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if(response.isSuccessful){
                    Toast.makeText(mContext, "약속을 등록했습니다.", Toast.LENGTH_SHORT).show()
                    finish()
                }

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })
        }
//        날짜 선택 텍스트뷰 클릭 이벤트 - DatePickerDialog
        binding.txtDate.setOnClickListener {

            val dsl = object : DatePickerDialog.OnDateSetListener{
                override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {

//                    연/월/일은 JAVA /Kotlin 언어의 기준(0~11)으로 월 값을 줌( 사람은 1~12월)
//                    주는 그대로 Calenda에서 set 하게 되면 올바른 월로 세팅됨

                    mSelectedAppointmentDateTime.set(year, month, dayOfMonth) // 연월일 한번에 세팅함수.

//                    약속일자의 문구를 22/03/08등의 형식으로 바꿔서 보여주자.
//                    SimpleDateFormat을 활용하면 =>월값도 알아서 보정

                    val sdf = SimpleDateFormat("yy/MM/dd")

                    binding.txtDate.text = sdf.format(mSelectedAppointmentDateTime.time)
                }

            }

            val dpd = DatePickerDialog(
                mContext,
                dsl,
                mSelectedAppointmentDateTime.get(Calendar.YEAR),
                mSelectedAppointmentDateTime.get(Calendar.MONTH),
                mSelectedAppointmentDateTime.get(Calendar.DAY_OF_MONTH)
            ).show()

        }

        // 시간 선택 텍스트뷰 클릭 이벤트 - TimePickDialog

        binding.txtTime.setOnClickListener {

            val tsl = object : TimePickerDialog.OnTimeSetListener{
                override fun onTimeSet(p0: TimePicker?, hourOfDay: Int, minute: Int) {

//                    약속일시의 시간으로 설정.
                    mSelectedAppointmentDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay)
                    mSelectedAppointmentDateTime.set(Calendar.MINUTE, minute)

                    val sdf = SimpleDateFormat("a h시 m분")
                    binding.txtTime.text = sdf.format(mSelectedAppointmentDateTime.time)

                }

            }

            val tpd = TimePickerDialog(
                mContext,
                tsl,
                18,
                0,
                false
            ).show()
        }

    }

    override fun setValues() {

        // 네이버 지도 객체 얻어오기 => 얻어와지면 할 일(Interface)코딩
        binding.naverMapView.getMapAsync {

            //지도 로딩이 끝나고 난 후에 얻어낸 온전한 지도 객체
            val naverMap = it

//            지도 시작지점 : 학원 위/경도
            val coord = LatLng( 37.577992654134604, 127.03362625538472)

            //coord에 설정한 좌표로 > 네이버지도의 카메라 이동

            val cameraUpdate = CameraUpdate.scrollTo(coord)

            naverMap.moveCamera(cameraUpdate)

            // 첫 마커 좌표 -> 학원위치 -> null

         //  val marker = Marker() =>멤버변수로 하나의 마커만 만들어서 관리하자.
//            marker = Marker()
//            marker!!.position = coord
//            marker!!.map = naverMap

//            처음 선택된 좌표 -> 학원 위치
//            mSelectedLatLng = coord

            //지도 클릭 이벤트
            naverMap.setOnMapClickListener { pointF, latLng ->

//                Log.d("클릭된 위/경도","위도:${latLng.latitude},경도:${latLng.longitude}")

                // (찍혀있는 마커가 없다면)마커를 새로 추가
                if(marker == null) {
                    marker = Marker()
                }

//                그 마커의 위치와 지도 적용
                marker!!.position = latLng
                marker!!.map = naverMap

                // 약속 장소도 새 좌표로 설정
                mSelectedLatLng = latLng



//                coord ~ 선택한 latLng까지 대중교통 경로를 그려보자(PathOverlay 기능 활용) + ODSay라이브러리 활용

                val myODSayService = ODsayService.init(mContext, "gw9m7KtTSb97PJwg3C/jQx+OKMjdKmugQKqgeBp44Vk")

                myODSayService.requestSearchPubTransPath(

                    coord.longitude.toString(),
                    coord.latitude.toString(),
                    latLng.longitude.toString(),
                    latLng.latitude.toString(),
                    null,
                    null,
                    null,
                    object : OnResultCallbackListener {
                        override fun onSuccess(p0: ODsayData?, p1: API?) {

                            val jsonObj = p0!!.json!!
                            Log.d("길찾기 응답", jsonObj.toString())

                            val resultObj = jsonObj.getJSONObject("result")
                            Log.d("result", resultObj.toString())

                            val pathArr = resultObj.getJSONArray("path")//여러 추천 경로 중 첫번째만 사용해보자

                            val firstPathObj = pathArr.getJSONObject(0) //무조건 0번째 경로 추출
                            Log.d("첫번째경로", firstPathObj.toString())


//                                첫번째 경로를 지나는 모든 정거장들의 위경도 값을 담을 목록
                            val stationLatLngList = ArrayList<LatLng>()

//                            출발지 좌표를 정거장 목록에 먼저 추가.
                            stationLatLngList.add(coord)

//                            불광~강남: 도보 5분 /지하철 30분 /버스 30분 / 도보5분
                            val subPathArr = firstPathObj.getJSONArray("subPath")

                            for (i in 0 until subPathArr.length()){
                                val subPathObj = subPathArr.getJSONObject(i)

//                                둘러보려는 경로가 정거장 목록을 내려준다면(지하철 or 버스) => 내부 파싱

                                if( !subPathObj.isNull("passStopList")){

                                    val passStopLisObj = subPathObj.getJSONObject("passStopList")
                                    val stationsArr = passStopLisObj.getJSONArray("stations")


//                                    실제 정거장 목록 파싱 => 각정거장의 위도/ 경도 추출 가능 => ArrayList에 담아서 경로선의 좌표로 활용.
                                    for (j in 0 until stationsArr.length()){

                                        val stationObj = stationsArr.getJSONObject(j)

//                                        위도(y좌표), 경도(x좌표) 추출
                                        val lat = stationObj.getString("y").toDouble()
                                        val lng = stationObj.getString("x").toDouble()

//                                        네이버 지도의 좌표로 만들어서 > ArrayList에 담자
                                        stationLatLngList.add(LatLng(lat, lng))
                                    }
                                }
                            }

//                            최종 정거장 ~ 도착지까지 직선
                            stationLatLngList.add( latLng)

//                            완성된 정거장 경로들을 =>path의 경로로 재설정. 지도에 새로 반영.

                            path!!.coords = stationLatLngList
                            path!!.map = naverMap

//                            (첫번째 추천 경로의) 정보 항목도 파싱
//                            예상 소요시간 파싱 => 임시로 토스트 출력.
                            
                            val infoObj = firstPathObj.getJSONObject("info")
                            
                            val totalTime = infoObj.getInt("totalTime") //소요 분
                            
                            val payment = infoObj.getInt("payment") //소요 비용

//                            네이버 지도 라이브러리의 InfoWindow 기능 활용
                            val infoWindow = InfoWindow()
                            infoWindow.adapter = object : InfoWindow.DefaultTextAdapter(mContext){

                                override fun getText(p0: InfoWindow): CharSequence {

                                    return "이동시간 : ${totalTime}분, 비용: ${payment}원"
                                }

                            }

                            infoWindow.open(marker!!)

                            marker!!.setOnClickListener {

                                if (marker!!.infoWindow == null) {
                                    infoWindow.open(marker!!)
                                }
                                else {
                                    infoWindow.close()
                                }

                                return@setOnClickListener true
                            }

//                            연습문제 - 카메라를 latLng(클릭한 위치)가 가운데로 오도록 세팅.
//                          공식 문서 활용 연습문제
                            val cameraUpdate = CameraUpdate.scrollTo(latLng)
                            naverMap.moveCamera(cameraUpdate)


                        }

                        override fun onError(p0: Int, p1: String?, p2: API?) {

                        }

                    }

                )

                if(path == null){
                    path = PathOverlay()
                }

//                ArrayList를 만들어서 출발지와 도착지를 추가.
                val coordList = ArrayList<LatLng>()

                coordList.add(coord)// 출발지를 임시로 학원으로
                coordList.add(latLng)// 클릭 된 좌표

                path!!.coords = coordList

                path!!.map = naverMap

            }

        }

//        내 출발장소 목록 불러오기
        getMyStartPlaceListFromServer()

//        스피너 어댑터 연결 -> 리스트뷰와 동일함.
        mStartPlaceAdapter = StartPlaceSpinnerAdapter(mContext, R.layout.start_place_spinner_list_item, mStartPlaceList)
        binding.startPlaceSpinner.adapter = mStartPlaceAdapter

    }
    fun getMyStartPlaceListFromServer(){

        apiList.getRequestMyPlaceList().enqueue(object : Callback<BasicResponse>{
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if(response.isSuccessful){

                    val br = response.body()!!

                    mStartPlaceList.clear()

                    mStartPlaceList.addAll(br.data.places)

                    mStartPlaceAdapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }
}
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
import com.naver.maps.map.overlay.Marker
import com.neppplus.keepthetime_20220311.databinding.ActivityEditAppointmentBinding
import java.text.SimpleDateFormat
import java.util.*

class EditAppointmentActivity : BaseActivity() {

    lateinit var binding: ActivityEditAppointmentBinding

//    약속 시간 일/시를 저장해줄 Calendar.(월 값이 0~11로 움직이게 맞춰져있다)
    val mSelectedAppointmentDateTime = Calendar.getInstance() //기본값: 현재일시

//    약속 장소 관련 멤버변수.
    val marker : Marker? = null //지도에 표시될 하나의 마커. 처음에는 찍지 않은 상태

    var mSelectedLatLng : LatLng? = null //약속 장소 위/경도 처음에는 설정하지 않은 상태

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_appointment)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        //저장 버튼이 눌리면
        binding.btnSave.setOnClickListener {
            //장소를 선택했는지? 안했다면 등록 거부
            if(mSelectedLatLng == null) {

                Toast.makeText(mContext, "약속 장소를 선택하지 않았습니다", Toast.LENGTH_SHORT).show()
                return@setOnClickListener

            }

            Log.d("선택한 약속장소 - 위도 ", "위도:${mSelectedLatLng!!.latitude}")
            Log.d("선택한 약속장소 - 경도 ", "경도:${mSelectedLatLng!!.longitude}")
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
            val coord = LatLng( 37.557934532,127.03360347 )

            //coord에 설정한 좌표로 > 네이버지도의 카메라 이동

            val cameraUpdate = CameraUpdate.scrollTo(coord)

            naverMap.moveCamera(cameraUpdate)

            // 첫 마커 좌표 -> 학원위치

         //  val marker = Marker() =>멤버변수로 하나의 마커만 만들어서 관리하자.
            marker = Marker()
            marker!!.position = coord
            marker!!.map = naverMap

//            처음 선택된 좌표 -> 학원 위치
            mSelectedLatLng = coord

            //지도 클릭 이벤트
            naverMap.setOnMapClickListener { pointF, latLng ->

                Log.d("클릭된 위/경도","위도:${latLng.latitude},경도:${latLng.longitude}")

                //마커를 새로 추가

                Marker!!.position = latLng
                Marker!!.map = naverMap
            }

        }

    }
}
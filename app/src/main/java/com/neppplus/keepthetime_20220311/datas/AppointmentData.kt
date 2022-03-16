package com.neppplus.keepthetime_20220311.datas

import java.util.*

class AppointmentData(
    val id: Int,
    val user_id: Int,
    val title: String,
    val datetime: Date, // 서버는 String으로 내려주지만, 파싱은 Date로 바꿔주고 싶다.
    val start_place: String,
    val start_latitude: Double,
    val start_longitude: Double,
    val place: String,
    val latitude: Double,
    val longitude: Double,
    val created_at: String,
    val user: UserData,
    val invited_friends: List<UserData>
) {
}
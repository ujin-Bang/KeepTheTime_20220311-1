package com.neppplus.keepthetime_20220311.datas

class AppointmentData(
    val id: Int,
    val user_id: Int,
    val title: String,
    val datetime: String,
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
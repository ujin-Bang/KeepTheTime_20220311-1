package com.neppplus.keepthetime_20220311.datas

import java.io.Serializable

class PlaceData(
    val id: Int,
    val user_Id: Int,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    val is_primay: Boolean,
):Serializable {
}
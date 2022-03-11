package com.neppplus.keepthetime_20220311.datas

// 서버응답 중, data: {} 를 파싱하기 위한 클래스

class DataResponse(
    val user: UserData,
    val token: String,
) {
}
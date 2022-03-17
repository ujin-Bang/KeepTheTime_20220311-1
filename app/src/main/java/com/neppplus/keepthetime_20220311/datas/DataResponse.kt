package com.neppplus.keepthetime_20220311.datas

// 서버응답 중, data: {} 를 파싱하기 위한 클래스

// 여러 API에서 data: {}를 내려줄 예정. => 내부 구성 모양이 각각 다를 수 있다.
//  ex.  user:{} 만 있다던지.

//  모든 경우에 대해 전부 변수 생성 => 활용시에만 주의하면 됨.

class DataResponse(
    val user: UserData,
    val token: String,

    val friends: List<UserData>, // 친구목록을 불러올때 사용할 변수

    val users: List<UserData>, // 검색 결과의 사용자 목록.

    val appointments: List<AppointmentData>,



) {
}
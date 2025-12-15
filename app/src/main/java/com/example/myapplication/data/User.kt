package com.example.myapplication.data

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String,
    // 추가적인 사용자 정보 필드를 여기에 정의할 수 있습니다.
)

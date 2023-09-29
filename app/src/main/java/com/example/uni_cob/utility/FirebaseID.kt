package com.example.uni_cob.utility

object FirebaseID {
    data class User(
        val name: String,
        val email: String,
        val phoneNumber: String,
        val password: String
    )

    // 나머지 상수 필드는 여기에 추가할 수 있습니다.
    const val studentNumber = "studentNumber"
    const val department = "department"
    const val universityName = "universityName"
    const val password = "password"
}

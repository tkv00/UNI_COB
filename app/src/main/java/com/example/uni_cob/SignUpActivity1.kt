package com.example.uni_cob


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.example.uni_cob.utility.FirebaseID
import java.util.regex.Pattern

class SignUpActivity1 : AppCompatActivity() {

    private lateinit var inputName: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPassword: EditText
    private lateinit var correctPassword: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var correctSchoolButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up01)

        // 뷰 초기화
        phoneNumber = findViewById(R.id.phoneNumber)
        inputName = findViewById(R.id.inputName)
        inputEmail = findViewById(R.id.et_stNumber)
        inputPassword = findViewById(R.id.inputPassword)
        correctPassword = findViewById(R.id.et_department)
        correctSchoolButton = findViewById(R.id.btn_signup)

        // "학교 인증하기" 버튼 클릭 리스너 설정
        correctSchoolButton.setOnClickListener {

            val email=inputEmail.text.toString().trim()
            isEmailAlreadyRegistered(email)
        }

        // EditText 필드 내용이 변경될 때마다 버튼 상태 업데이트
        inputName.addTextChangedListener(textWatcher)
        phoneNumber.addTextChangedListener(textWatcher)
        inputEmail.addTextChangedListener(textWatcher)
        inputPassword.addTextChangedListener(textWatcher)
        correctPassword.addTextChangedListener(textWatcher)

        // 전화번호 입력 상자의 포커스가 변경될 때 호출되는 리스너 설정
        phoneNumber.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val phone = phoneNumber.text.toString()
                if (!isPhoneNumberValid(phone)) {
                    phoneNumber.error = "올바른 전화번호 형식을 입력하세요."
                } else {
                    phoneNumber.error = null
                }
            }
        }

        // 이메일 입력 상자의 포커스가 변경될 때 호출되는 리스너 설정
        inputEmail.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val email = inputEmail.text.toString()
                if (!isEmailValid(email)) {
                    inputEmail.error = "올바른 이메일 형식을 입력하세요."
                } else {
                    inputEmail.error = null
                }
            }
        }
    }
    // 중복 이메일 체크 함수
    private fun isEmailAlreadyRegistered(email: String) {
        // Firebase에 이미 가입된 이메일인지 확인하는 코드 추가
        FirebaseAuth.getInstance().fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val methods = task.result?.signInMethods
                    if (methods == null || methods.isEmpty()) {
                        // 이메일이 중복되지 않는 경우
                        if (isRegistrationValid()) {
                            // 회원 가입 조건을 만족하면 SignUpActivity2로 이동
                            val intent = Intent(this, SignUpActivity2::class.java)
                            startActivity(intent)
                        }
                    } else {
                        // 이메일이 이미 가입되어 있는 경우
                        showMessage("이미 가입된 이메일 주소입니다.")
                    }
                } else {
                    showMessage("이메일 확인 중 오류가 발생했습니다.")
                }
            }
    }

    private fun isRegistrationValid(): Boolean {
        val name = inputName.text.toString()
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val userPhoneNumber = phoneNumber.text.toString()
        val confirmPassword = correctPassword.text.toString()

        // 이름, 이메일, 비밀번호, 비밀번호 확인, 전화번호 필드가 모두 비어 있지 않은 경우
        val isAllFieldsFilled =
            name.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                    confirmPassword.isNotBlank() && userPhoneNumber.isNotBlank()

        // 비밀번호의 조건 검사 (영문 대소문자, 숫자 또는 특수문자 포함, 8자 이상)
        val isPasswordValid =
            Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d|[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])\\S{8,}\$", password)

        return isAllFieldsFilled && isPasswordValid
    }

    // EditText 필드 내용이 변경될 때 호출되는 TextWatcher
    private val textWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            updateButtonState()
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            // 이 메서드는 사용하지 않음
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            // 이 메서드는 사용하지 않음
        }
    }

    // "학교 인증하기" 버튼 상태 업데이트 함수
    private fun updateButtonState() {
        val name = inputName.text.toString()
        val email = inputEmail.text.toString()
        val password = inputPassword.text.toString()
        val userPhoneNumber = phoneNumber.text.toString()
        val confirmPassword = correctPassword.text.toString()

        // 이름, 이메일, 비밀번호, 비밀번호 확인, 전화번호 필드가 모두 비어 있지 않은 경우 버튼 활성화
        val isAllFieldsFilled =
            name.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
                    confirmPassword.isNotBlank() && userPhoneNumber.isNotBlank()

        // 비밀번호의 조건 검사 (영문 대소문자, 숫자 또는 특수문자 포함, 8자 이상)
        val isPasswordValid =
            Pattern.matches("^(?=.*[A-Za-z])(?=.*\\d|[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?])\\S{8,}\$", password)

        // 버튼 상태 업데이트
        correctSchoolButton.isEnabled = isAllFieldsFilled && isPasswordValid

        // 버튼 배경색 업데이트 (검정색 또는 회색)
        if (isAllFieldsFilled && isPasswordValid) {
            correctSchoolButton.setBackgroundResource(R.drawable.black_button_background) // 검정색 배경
        } else {
            correctSchoolButton.setBackgroundResource(R.drawable.gray_button_background) // 회색 배경
        }
    }

    // 이메일 형식 검사 함수
    private fun isEmailValid(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,4}"
        return Pattern.matches(emailPattern, email)
    }

    // 전화번호 조건 검사 함수
    private fun isPhoneNumberValid(phoneNumber: String): Boolean {
        return phoneNumber.length == 11
    }

    // 메시지를 표시하는 함수
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }

    private fun registerUser() {
        val email = inputEmail.text.toString().trim()
        val password = inputPassword.text.toString().trim()
        val name = inputName.text.toString().trim()
        val confirmPassword = correctPassword.text.toString().trim()
        val userPhoneNumber = phoneNumber.text.toString().trim()

        if (password == confirmPassword) {
            FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // 사용자 등록 성공
                        val user = task.result?.user

                        // Firebase Realtime Database에 사용자 정보 저장
                        val database = FirebaseDatabase.getInstance()
                        val usersRef = database.getReference("users")

                        // 사용자 정보를 데이터베이스에 추가
                        val userInfo = FirebaseID.User(name, email, userPhoneNumber,password)
                        user?.uid?.let {
                            usersRef.child(it).setValue(userInfo)
                                .addOnSuccessListener {
                                    showMessage("회원가입이 완료되었습니다.")
                                    // 회원가입이 성공하면 로그인 페이지로 이동
                                    val intent = Intent(this, SignUpActivity2::class.java)
                                    intent.putExtra("name",name)
                                    intent.putExtra("email",email)
                                    intent.putExtra("userPhoneNumber",userPhoneNumber)
                                    intent.putExtra("password",password)

                                    startActivity(intent)
                                }
                                .addOnFailureListener { e ->
                                    showMessage("회원가입 실패: $e")
                                }
                        }
                    } else {
                        // 사용자 등록 실패
                        showMessage("회원가입 실패: ${task.exception?.message}")
                    }
                }
        } else {
            // 비밀번호와 비밀번호 확인이 일치하지 않는 경우
            showMessage("비밀번호가 일치하지 않습니다")
        }
    }
}



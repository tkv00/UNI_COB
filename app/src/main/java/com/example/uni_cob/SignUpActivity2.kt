package com.example.uni_cob
import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage


class SignUpActivity2 : AppCompatActivity() {

    private lateinit var et_department: EditText
    private lateinit var et_stNumber: EditText
    private lateinit var et_school: EditText
    private lateinit var btn_signup: Button
    private lateinit var btn_check_school: Button
    private lateinit var imageUri: Uri
    private var isSchoolVerified: Boolean = false

    private lateinit var launcher: ActivityResultLauncher<Intent>

    private val CAMERA_PERMISSION_REQUEST_CODE = 123 // 원하는 정수값으로 설정

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up2)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        val cameraPermission = Manifest.permission.CAMERA
        if (ContextCompat.checkSelfPermission(
                this,
                cameraPermission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 카메라 권한이 없을 경우 권한 요청
            ActivityCompat.requestPermissions(
                this,
                arrayOf(cameraPermission),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }

        et_department = findViewById(R.id.et_department)
        et_stNumber = findViewById(R.id.et_stNumber)
        et_school = findViewById(R.id.et_school)
        btn_signup = findViewById(R.id.btn_signup)
        btn_check_school = findViewById(R.id.btn_check_school)

        et_department.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        et_stNumber.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        et_school.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                updateButtonState()
                // 학교 정보가 변경되면 인증 상태 초기화
                isSchoolVerified = false
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        // registerForActivityResult를 사용하여 액티비티 실행 결과 처리
        launcher = registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intentData: Intent? = result.data
                intentData?.data?.let { uri ->
                    imageUri = uri
                    performTextRecognition(imageUri)
                }
            }
        }

        btn_check_school.setOnClickListener {
            checkSchoolAuthentication()
        }

        btn_signup.setOnClickListener {
            if (isSchoolVerified) {
                registerUser()
            } else {
                showMessage("학교가 인증되지 않았습니다.")
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우 처리할 작업 수행
            } else {
                // 권한이 거부된 경우 처리할 작업 수행 (예: 사용자에게 권한 필요 메시지 표시)
                AlertDialog.Builder(this)
                    .setTitle("권한 필요")
                    .setMessage("카메라 권한이 필요합니다. 설정에서 권한을 허용해주세요.")
                    .setPositiveButton("설정으로 이동") { _, _ ->
                        // 사용자를 설정으로 이동시키는 인텐트를 생성
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                        val uri = Uri.fromParts("package", packageName, null)
                        intent.data = uri
                        startActivity(intent)
                    }
                    .setNegativeButton("취소") { _, _ ->
                        // 권한이 거부되었을 때 취할 작업을 추가하세요.
                        // 예를 들어, 권한이 거부되었음을 사용자에게 알리거나 특정 동작을 비활성화할 수 있습니다.
                    }
                    .setCancelable(false)
                    .show()
            }
        }
    }

    private fun updateButtonState() {
        val department = et_department.text.toString()
        val stNumber = et_stNumber.text.toString()
        val schoolName = et_school.text.toString()
        val isDepartmentValid = department.isNotBlank()
        val isStNumberValid = stNumber.isNotBlank()
        val isSchoolNameValid = schoolName.isNotBlank()

        btn_check_school.isEnabled = isSchoolNameValid
        if (isSchoolNameValid) {
            btn_check_school.setBackgroundResource(R.drawable.black_button_background)
            btn_check_school.text = if (isSchoolVerified) "인증 완료" else "인증 하기"
        } else {
            btn_check_school.setBackgroundResource(R.drawable.gray_button_background)
            btn_check_school.text = "학교 이름 입력"
        }

        btn_signup.isEnabled = isDepartmentValid && isStNumberValid && isSchoolNameValid && isSchoolVerified
        if (isDepartmentValid && isStNumberValid && isSchoolNameValid && isSchoolVerified) {
            btn_signup.setBackgroundResource(R.drawable.black_button_background)
            btn_signup.text = "가입 완료"
        } else {
            btn_signup.setBackgroundResource(R.drawable.gray_button_background)
            btn_signup.text = "가입 하기"
        }
    }

    private fun performTextRecognition(imageUri: Uri) {
        val image = FirebaseVisionImage.fromFilePath(this, imageUri)
        val recognizer = FirebaseVision.getInstance().cloudTextRecognizer

        recognizer.processImage(image)
            .addOnSuccessListener { texts ->
                val extractedText = texts.text
                val schoolName = et_school.text.toString()

                // 대학 이름과 추출된 텍스트 비교
                if (extractedText.contains(schoolName, ignoreCase = true)) {
                    // 대학 이름이 일치하면 가입하기 버튼 활성화
                    isSchoolVerified = true
                    updateButtonState()
                    showMessage("학교가 인증되었습니다.")
                } else {
                    // 대학 이름이 일치하지 않으면 메시지 표시
                    showMessage("학교 인증 실패")
                }
            }
            .addOnFailureListener { e ->
                // 텍스트 인식 실패 시 처리
                showMessage("텍스트 인식 실패: ${e.message}")
            }
    }

    private fun registerUser() {
        // 사용자 정보를 Firebase에 저장하는 로직을 구현하세요.
        // 이 부분에서 Firebase Realtime Database 또는 Firestore를 사용하여 사용자 정보를 저장할 수 있습니다.
        // 사용자 정보에는 이름, 학과, 학번, 학교 이름 및 기타 필요한 정보가 포함될 것입니다.
        // 아래 코드는 예시로 사용자 정보를 저장하는 방법을 보여줍니다.

        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: ""
        val department = et_department.text.toString()
        val stNumber = et_stNumber.text.toString()
        val schoolName = et_school.text.toString()

        if (userId.isNotEmpty()) {
            val userRef = database.child("users").child(userId)
            userRef.child("department").setValue(department)
            userRef.child("stNumber").setValue(stNumber)
            userRef.child("schoolName").setValue(schoolName)

            showMessage("가입이 완료되었습니다.")
            finish() // 가입 완료 후 현재 화면 종료
        } else {
            showMessage("사용자 정보를 저장할 수 없습니다.")
        }
    }

    private fun checkSchoolAuthentication() {
        val schoolName = et_school.text.toString()
        val isSchoolNameValid = schoolName.isNotBlank()

        if (isSchoolNameValid) {
            // 학교 이름 확인 및 인증 로직 추가 (예: "대학교" 또는 "university"로만 인증되도록)
            if (schoolName.equals("대학교", ignoreCase = true) ||
                schoolName.equals("university", ignoreCase = true)) {
                isSchoolVerified = true
                showMessage("학교가 인증되었습니다.")
            } else {
                showMessage("학교 인증 실패")
            }
        } else {
            showMessage("학교 이름을 입력하세요.")
        }
        updateButtonState()
    }

    private fun showMessage(message: String) {
        // 적절한 방식으로 메시지 표시
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}



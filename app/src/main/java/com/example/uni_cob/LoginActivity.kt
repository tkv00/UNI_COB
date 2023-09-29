package com.example.uni_cob
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup.LayoutParams
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        val btn_signup=findViewById<Button>(R.id.signupButton)
        val emailLoginButton = findViewById<Button>(R.id.emailLoginButton)
        val phoneLoginButton = findViewById<Button>(R.id.phoneLoginButton)
        // 휴대폰 번호로 로그인 버튼 클릭 이벤트
        phoneLoginButton.setOnClickListener {
            showLogin_phone_Dialog()
        }

        // 이메일로 로그인 버튼 클릭 이벤트
        emailLoginButton.setOnClickListener {
            showLogin_email_Dialog()
        }


        fun moveToLogin01(){
            val intent=Intent(this,SignUpActivity1::class.java)
            startActivity(intent)
        }
        //페이지 이동(로그인->회원가입)
        btn_signup.setOnClickListener {moveToLogin01()}
    }

    private fun showLogin_email_Dialog() {
        val dialog = Dialog(this,R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.login_dialog_email)

        // 배경을 반투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 크기 설정
        val windowLayoutParams = dialog.window?.attributes
        windowLayoutParams?.width = (resources.displayMetrics.widthPixels * 0.8).toInt()
        dialog.window?.attributes = windowLayoutParams
        windowLayoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        // 중앙에 위치하도록 설정
        windowLayoutParams?.gravity = Gravity.CENTER
        dialog.window?.attributes = windowLayoutParams

        // 배경 어둡게 설정
        val layoutParams = window.attributes
        layoutParams.dimAmount = 0.8f
        dialog.window?.attributes = layoutParams

        // 포커스 설정
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
        //뒷배경 터치 불가
        dialog.setCancelable(false)
        dialog.show()

    }

    private fun showLogin_phone_Dialog() {
        val dialog = Dialog(this,R.style.CustomDialogTheme)
        dialog.setContentView(R.layout.login_dialog_phone)

        // 배경을 반투명하게 설정
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // 다이얼로그 크기 설정
        val windowLayoutParams = dialog.window?.attributes
        windowLayoutParams?.height = WindowManager.LayoutParams.WRAP_CONTENT
        windowLayoutParams?.width = (resources.displayMetrics.widthPixels *0.5).toInt()
        dialog.window?.attributes = windowLayoutParams

        // 중앙에 위치하도록 설정
        windowLayoutParams?.gravity = Gravity.CENTER
        dialog.window?.attributes = windowLayoutParams

       /* // 배경 어둡게 설정
        val layoutParams = window.attributes
        layoutParams.dimAmount = 0.5f
        dialog.window?.attributes = layoutParams*/

        // 포커스 설정
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        dialog.show()
    }

}
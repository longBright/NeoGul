package com.avengers.maskfitting.mafiafin.faceshape

import androidx.appcompat.app.AppCompatActivity
import android.content.Intent
import android.content.SharedPreferences

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.account.RegisterActivity

import com.avengers.maskfitting.mafiafin.databinding.ActivityFaceShapeOutputBinding
import com.avengers.maskfitting.mafiafin.main.MainActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import org.json.JSONObject

class FaceShapeOutputActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFaceShapeOutputBinding
    lateinit var preferences: SharedPreferences

    override fun onStart() {
        super.onStart()
        // 로그인한 사용자 정보
        val account = this.let { GoogleSignIn.getLastSignedInAccount(it) }
        if (account != null) {
            Log.d("얼굴형 결과 onStart", "${account?.email}")
            userEmail = account?.email.toString()
        }
        else {
            preferences = getSharedPreferences("userEmail", MODE_PRIVATE)
            userEmail = preferences.getString("email", "")
            if (userEmail == "") {
                Toast.makeText(this, "권한 거부! 로그인 정보가 확인되지 않습니다!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding =  ActivityFaceShapeOutputBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val result = intent.getStringExtra("FaceShape").toString()
        Log.d("얼굴형 분류 결과", result)

        val reClassification = intent.getBooleanExtra("reClassification", false)

        binding.faceShape.text = result
        val faceShapeResult: String

        when(result){
            "0" -> {
                binding.faceShape.text = "하트형 얼굴 입니다"
                binding.faceShapeImg.setImageResource(R.drawable.heart)
                faceShapeResult = "하트형"
            }
            "1" -> {
                binding.faceShape.text = "계란형 얼굴 입니다"
                binding.faceShapeImg.setImageResource(R.drawable.oval)
                faceShapeResult = "계란형"
            }
            "2" -> {
                binding.faceShape.text = "둥근형 얼굴 입니다"
                binding.faceShapeImg.setImageResource(R.drawable.round)
                faceShapeResult = "둥근형"
            }
            "3" -> {
                binding.faceShape.text = "사각형 얼굴 입니다"
                binding.faceShapeImg.setImageResource(R.drawable.square)
                faceShapeResult = "사각형"
            }
            else -> {
                binding.faceShape.text = "분류된 얼굴형이 없습니다"
                faceShapeResult = "선택안함"
            }
        }


        binding.btnNext.setOnClickListener {
            // 재분류인 경우 얼굴형 분류 정보 수정
            if (reClassification) {
                val responseListener: Response.Listener<String?> =
                    Response.Listener { response ->
                        try {
                            var jsonObject = JSONObject(response)
                            val success = jsonObject.getBoolean("success")
                            if (success) {
                                Toast.makeText(this, "얼굴형 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                Log.d("야 실행됐다", "이제 어디서 뒤지는지 찾아라")
                                // 메인 액티비티로 귀환
                                var intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            else {
                                Toast.makeText(this, "문제가 발생하였습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }

                Log.d("이메일이랑 얼굴형", "$userEmail $faceShapeResult")
                val faceShapeUpdateRequest = FaceShapeUpdateRequest(
                    userEmail.toString(),
                    faceShapeResult,
                    responseListener
                )
                val queue: RequestQueue = Volley.newRequestQueue(this)
                queue.add(faceShapeUpdateRequest)
            }
            // 재분류가 아닐 경우 회원 가입 화면으로 이동
            else {
                var intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("FaceShape", faceShapeResult)
                startActivity(intent)
                this.finish()
            }
        }
    }

    companion object {
        var userEmail: String? = ""
    }
}

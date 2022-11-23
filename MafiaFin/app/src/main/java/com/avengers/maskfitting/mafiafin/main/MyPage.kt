package com.avengers.maskfitting.mafiafin.main

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.Volley
import com.avengers.maskfitting.mafiafin.R
import com.avengers.maskfitting.mafiafin.account.LoginActivity
import com.avengers.maskfitting.mafiafin.databinding.FragmentMypageBinding
import com.avengers.maskfitting.mafiafin.faceshape.FaceShapeActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import org.json.JSONException
import org.json.JSONObject

class MyPage : Fragment() {
    private lateinit var binding: FragmentMypageBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 현재 로그인중인 계정 로드
        account = this.let { GoogleSignIn.getLastSignedInAccount(it.requireContext()) }
        Log.d("MyPage", "${account?.email}")
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMypageBinding.inflate(inflater, container, false)
        // 현재 로그인 중인 사용자의 이름 표시
        // Ma!fia 일반 회원
        if (account == null) {
            // 로그인 중인 사용자 정보 획득
            val nickname = (activity as MainActivity).preferences.getString("nickname", "")
            userEmail = (activity as MainActivity).preferences.getString("email", "")       // 사용자 이메일 초기화
            binding.helloText.text = nickname + binding.helloText.text
            Log.d("MyPage", "nickname : ${nickname.toString()}")
        }
        // 구글 회원
        else {
            userEmail = account?.email      // 사용자 이메일 초기화
            binding.helloText.text = account?.displayName + binding.helloText.text
        }

        getUserData(userEmail)   // 사용자 데이터 로드 & SharedPreferences 에 저장

        // 얼굴형 재분류 버튼
        binding.reClassifyButton.setOnClickListener {
            // 얼굴형 분류 액티비티 실행
            val intent = Intent(this.requireContext(), FaceShapeActivity::class.java)
            intent.putExtra("reClassification", true)
            startActivity(intent)
        }

        // 로그아웃 버튼
        binding.btnLogout.setOnClickListener {
            // 구글 로그인인 경우 구글 로그아웃
            if (account != null ){
                logout()
            }
            // 자체 로그인인 경우
            else {
                (activity as MainActivity).preferences.edit().clear().apply()  // SharedPreferences 데이터 삭제
                Toast.makeText(this.requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this.requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
        }
        return binding.root
    }

    // 구글 로그아웃 버튼
    private fun logout() {
        (activity as MainActivity).mGoogleSignInClient.signOut()
            .addOnCompleteListener(this.requireActivity()) {
                // 로그아웃 성공시 실행
                Toast.makeText(this.requireContext(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show()
                val intent = Intent(this.requireContext(), LoginActivity::class.java)
                startActivity(intent)
            }
    }

    // 텍스트 초기화 함수
    private fun initView() {
        /* 얼굴형 텍스트 */
        val faceShape = (activity as MainActivity).preferences.getString("faceShape", "미등록")
        binding.faceShapeTextKor.text = faceShape
        when (faceShape) {
            resources.getString(R.string.roundKor) -> {
                binding.faceShapeTextEng.text = resources.getString(R.string.roundEng)
                binding.faceShapeImg.setImageResource(R.drawable.round)

            }

            resources.getString(R.string.heartKor) -> {
                binding.faceShapeTextEng.text = resources.getString(R.string.heartEng)
                binding.faceShapeImg.setImageResource(R.drawable.heart)
            }

            resources.getString(R.string.squareKor) -> {
                binding.faceShapeTextEng.text = resources.getString(R.string.squareEng)
                binding.faceShapeImg.setImageResource(R.drawable.square)
            }

            resources.getString(R.string.ovalKor) -> {
                binding.faceShapeTextEng.text = resources.getString(R.string.ovalEng)
                binding.faceShapeImg.setImageResource(R.drawable.oval)
            }
            else -> "미등록"
        }
    }

    // 사용자 데이터 로드 및 SharedPreferences 에 저장
    private fun getUserData(email:String?) {
        val responseListener: Response.Listener<String?> = Response.Listener { response ->
            try {
                val jsonObject = JSONObject(response)
                val success = jsonObject.getBoolean("success")
                if (success) {
                    // 로그인한 회원 정보 SharedPreference 에 저장
                    val jsonFaceShape = jsonObject.getString("face_shape")
                    val edit = (activity as MainActivity).preferences.edit()
                    edit.putString("faceShape", jsonFaceShape)
                    edit.apply()

                    initView()      // 텍스트 초기화
                } else {
                    Toast.makeText(this.requireContext(),
                        "사용자 정보 로딩에 실패하였습니다. 관리자에게 문의해주세요.",
                        Toast.LENGTH_SHORT).show()
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        // 사용자 데이터 접근 요청
        val userDataRequest = email?.let {
            MyPageRequest(
                it,
                responseListener
            )
        }

        val queue: RequestQueue = Volley.newRequestQueue(this.requireContext())
        queue.add(userDataRequest)
    }

    companion object {
        var account: GoogleSignInAccount? = null
        var userEmail: String? = ""
    }
}
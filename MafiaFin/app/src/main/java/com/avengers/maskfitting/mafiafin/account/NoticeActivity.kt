package com.avengers.maskfitting.mafiafin.account

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.avengers.maskfitting.mafiafin.databinding.ActivityNoticeBinding
import com.avengers.maskfitting.mafiafin.faceshape.FaceShapeActivity

class NoticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 돌아가기 버튼 터치 시 현재 액티비티 종료
        binding.exitBtn.setOnClickListener {
            finish()
        }

        // 시작버튼 터치 시 얼굴형 분석 실행
        binding.startBtn.setOnClickListener {
            val intent = Intent(this, FaceShapeActivity::class.java)
            startActivity(intent)
        }
    }
}
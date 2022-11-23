package com.avengers.maskfitting.mafiafin.faceshape

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.avengers.maskfitting.mafiafin.faceshape.FaceShapeCamera
import com.avengers.maskfitting.mafiafin.faceshape.GraphicOverlay
import com.avengers.maskfitting.mafiafin.faceshape.FaceContourGraphic
import com.avengers.maskfitting.mafiafin.databinding.ActivityFaceShapeBinding


class FaceShapeActivity : AppCompatActivity() {
    var faceShapeResult : String = ""
    var reClassification : Boolean = false

    private lateinit var faceShapeCamera: FaceShapeCamera

    private val binding: ActivityFaceShapeBinding by lazy {
        ActivityFaceShapeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createCameraManager()       // 카메라 매니저 생성
        val view = binding.root
        setContentView(view)

        if (intent.hasExtra("reClassification")) {
            reClassification = intent.getBooleanExtra("reClassification", false)
        }

        // 카메라 이용 권한 권한 확인
        if (allPermissionsGranted()) {
            faceShapeCamera.startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                REQUEST_CODE_PERMISSIONS
            )
        }

        Log.d("FaceShapeActivity", "정상동작!")

        // 검사하기 버튼 터치 시 회원 가입 화면으로 이동
        binding.btnSearch.setOnClickListener {
            var intent = Intent(this, FaceShapeOutputActivity::class.java)
            // 얼굴형 분류 결과 전달
            Log.d("FaceShapeActivity", faceShapeResult)
            intent.putExtra("FaceShape", faceShapeResult)
            intent.putExtra("reClassification", reClassification)
            startActivity(intent)
            this.finish()       // 카메라 종료를 위해 넣어줘야함
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                faceShapeCamera.startCamera()
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }
    }

    // 카메라 매니저 생성
    private fun createCameraManager() {
        faceShapeCamera = FaceShapeCamera(
            this,
            binding.previewViewFinder,
            this,
            binding.graphicOverlayFinder
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
    }
}
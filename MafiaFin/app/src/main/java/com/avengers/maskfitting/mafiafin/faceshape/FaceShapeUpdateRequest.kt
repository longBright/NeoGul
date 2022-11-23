package com.avengers.maskfitting.mafiafin.faceshape

import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest

class FaceShapeUpdateRequest (
    email: String,
    faceShape: String,
    listener: Response.Listener<String?>?,
) :
    StringRequest(Method.POST, URL, listener, null) {
    private val parameters: MutableMap<String, String>

    @Throws(AuthFailureError::class)
    override fun getParams(): Map<String, String> {
        return parameters
    }

    companion object {
        // 서버 url 설정 (php 파일 연동)
        private const val URL = "http://43.200.115.71/UpdateFaceShape.php" // "http:// 퍼블릭 DNS 주소/Register.php"
    }

    init {
        parameters = HashMap()
        parameters["email"] = email
        parameters["face_shape"] = faceShape
    }
}
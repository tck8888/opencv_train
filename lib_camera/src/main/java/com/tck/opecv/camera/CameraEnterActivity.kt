package com.tck.opecv.camera

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.tck.opecv.camera.databinding.ActivityCameraEnterBinding

/**
 *
 * description:

 * @date 2020/12/5 16:40

 * @author tck88
 *
 * @version v1.0.0
 *
 */
class CameraEnterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCameraEnterBinding

    companion object {
        val permissions = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraEnterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnJumpCamera.setOnClickListener {
            val all = permissions.all {
                ActivityCompat.checkSelfPermission(
                    this,
                    it
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (all) {
                openCamera()
            } else {
                ActivityCompat.requestPermissions(this, permissions, 100)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            val all = grantResults.all { it == PackageManager.PERMISSION_GRANTED }
            if (all) {
                openCamera()
            } else {
                Toast.makeText(this, "请打开相机权限", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun openCamera() {
        startActivity(Intent(this, CameraActivity::class.java))
    }
}
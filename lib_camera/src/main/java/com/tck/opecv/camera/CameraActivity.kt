package com.tck.opecv.camera

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.view.CameraView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.tck.opecv.camera.databinding.ActivityCameraBinding
import com.tck.opecv.camera.databinding.ActivityCameraEnterBinding
import java.io.File


/**
 *
 * description:

 * @date 2020/12/5 16:40

 * @author tck88
 *
 * @version v1.0.0
 *
 */
class CameraActivity : AppCompatActivity() {

    companion object {
        val TAG = "opencv"
    }

    private lateinit var binding: ActivityCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openCamera()

        binding.btnStartTakePhoto.setOnClickListener {
            takePicture()
        }
    }

    private fun takePicture() {
        val filePath = "${cacheDir}${File.separator}open_cv_test_${System.currentTimeMillis()}.png"
        val outputFileOptions = ImageCapture
            .OutputFileOptions
            .Builder(File(filePath))
            .build()
        binding.cameraView.captureMode = CameraView.CaptureMode.IMAGE
        binding.cameraView.takePicture(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Log.d(TAG, "takePicture success savedUri is :${filePath}")
                    val intent = Intent(this@CameraActivity, ImageEditActivity::class.java)
                    intent.putExtra("url", filePath)
                    startActivity(intent)

                }

                override fun onError(exception: ImageCaptureException) {
                    Log.d(TAG, "takePicture error :$exception")
                }
            }
        )
    }

    private fun openCamera() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        binding.cameraView.bindToLifecycle(this)
    }


}
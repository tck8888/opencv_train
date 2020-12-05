package com.tck.opecv.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.tck.opecv.base.MyLog
import com.tck.opecv.camera.databinding.ActivityCameraBinding
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
    private var preview: Preview? = null
    private var imageCapture: ImageCapture? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.btnStartTakePhoto.setOnClickListener {
            takePicture()
        }

        binding.previewView.post {
            initCamera()
        }
    }

    @SuppressLint("RestrictedApi")
    private fun initCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.Builder().build()
            val tempPreview = Preview.Builder()
                .build()
            val tempImageCapture = ImageCapture
                .Builder()
                .setDefaultResolution(Size(1280, 720))
                .build()
            preview = tempPreview
            imageCapture = tempImageCapture
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, cameraSelector, tempPreview, tempImageCapture)
                tempPreview.setSurfaceProvider(binding.previewView.surfaceProvider)
            } catch (e: Exception) {
                MyLog.d("initCamera error:${e.message}")
            }

        }, ContextCompat.getMainExecutor(this))


    }

    private fun takePicture() {
        val tempImageCapture= imageCapture ?: return
        val filePath = "${cacheDir}${File.separator}open_cv_test_${System.currentTimeMillis()}.png"
        val outputFileOptions = ImageCapture
            .OutputFileOptions
            .Builder(File(filePath))
            .build()
        tempImageCapture.takePicture(
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



}
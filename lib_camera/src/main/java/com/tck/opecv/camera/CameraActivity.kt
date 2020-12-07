package com.tck.opecv.camera

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.util.Size
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.tck.opecv.base.MyLog
import com.tck.opecv.camera.databinding.ActivityCameraBinding
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream


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
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build()
            val tempPreview = Preview.Builder()
                .build()
            val tempImageCapture = ImageCapture
                .Builder()
                .setDefaultResolution(Size(1280, 720))
                .build()
            tempImageCapture.flashMode
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
        val tempImageCapture = imageCapture ?: return
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
                    MyLog.d("takePicture success savedUri is :${filePath}")
                    saveRotateBitmap(filePath)
                    val intent = Intent(this@CameraActivity, ImageEditActivity::class.java)
                    intent.putExtra("url", filePath)
                    startActivity(intent)

                }

                override fun onError(exception: ImageCaptureException) {
                    MyLog.d("takePicture error :$exception")
                }
            }
        )
    }

    private fun saveRotateBitmap(filePath: String): Boolean {
        try {
            val readPictureDegree = readPictureDegree(filePath)
            val decodeFile = BitmapFactory.decodeFile(filePath)
            val rotateBitmap = rotateBitmap(decodeFile, readPictureDegree)
            return rotateBitmap?.let {
                FileOutputStream(File(filePath)).use { fileOutputStream ->
                    BufferedOutputStream(fileOutputStream).use { bufferedOutputStream ->
                        it.compress(Bitmap.CompressFormat.JPEG, 100, bufferedOutputStream)
                        bufferedOutputStream.flush()
                        true
                    }
                }
            } ?: false
        } catch (e: Exception) {
            MyLog.d("transformBitmap error:${e.message}")
        }
        return false
    }

    private fun rotateBitmap(origin: Bitmap?, degrees: Int): Bitmap? {
        try {
            val tempBitmap = origin ?: return null
            val matrix = Matrix()
            matrix.setRotate(degrees.toFloat())
            val newBitmap =
                Bitmap.createBitmap(
                    tempBitmap,
                    0,
                    0,
                    tempBitmap.width,
                    tempBitmap.height,
                    matrix,
                    false
                )
            if (newBitmap == tempBitmap) {
                return newBitmap
            }
            tempBitmap.recycle()
            return newBitmap
        } catch (e: Exception) {
            MyLog.d("rotateBitmap error:${e.message}")
        }

        return null
    }

    private fun readPictureDegree(path: String): Int {
        var degree = 0
        try {
            val exifInterface = ExifInterface(path)
            val orientation = exifInterface.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
            degree = when (orientation) {
                ExifInterface.ORIENTATION_ROTATE_90 -> 90
                ExifInterface.ORIENTATION_ROTATE_180 -> 180
                ExifInterface.ORIENTATION_ROTATE_270 -> 270
                else -> 0
            }
        } catch (e: Exception) {
            MyLog.d("readPictureDegree error:${e.message}")
        }

        return degree
    }


}
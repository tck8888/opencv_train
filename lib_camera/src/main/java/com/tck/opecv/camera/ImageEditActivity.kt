package com.tck.opecv.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.impl.utils.Exif
import com.bumptech.glide.Glide
import com.tck.opecv.base.MyLog
import com.tck.opecv.camera.databinding.ActivityImageEditBinding
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class ImageEditActivity : AppCompatActivity() {


    //source bitmap width:4032,height:3024
    private lateinit var binding: ActivityImageEditBinding

    private var url = ""

    @SuppressLint("RestrictedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        url = intent.getStringExtra("url") ?: ""
        MyLog.d("url:$url")
        val decodeFile = BitmapFactory.decodeFile(url)
        MyLog.d("bitmap current rotation:${Exif.createFromFileString(url).rotation}")
        decodeFile?.let {
            MyLog.d("source bitmap width:${it.width},height:${it.height}")
        }
        Glide.with(this).load(url).into(binding.ivCover)
        binding.btnRecognitionTextRegion.setOnClickListener {
            test()
        }
    }

    fun test() {
        Thread {
            try {
                val srcMat = Mat()
                val grayMat = Mat()
                val blurMat = Mat()
                val usm = Mat()
                val thresholdMat = Mat()
                val srcBitmap = BitmapFactory.decodeFile(url)
                Utils.bitmapToMat(srcBitmap, srcMat)

                Imgproc.GaussianBlur(srcMat, blurMat, Size(0.0, 0.0), 25.0)
                Core.addWeighted(srcMat, 1.5, blurMat, -0.5, 0.0, usm)

                Imgproc.cvtColor(usm, grayMat, Imgproc.COLOR_BGR2GRAY)

                Imgproc.threshold(grayMat, thresholdMat, 0.0, 255.0, Imgproc.THRESH_OTSU)
                Utils.matToBitmap(thresholdMat,srcBitmap)

                runOnUiThread {
                    binding.ivResult.setImageBitmap(srcBitmap)
                }
                srcMat.release()
                grayMat.release()
                blurMat.release()
                usm.release()
                thresholdMat.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }.start()

    }



}
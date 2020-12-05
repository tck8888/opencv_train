package com.tck.opecv.camera

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.camera.core.ImageProxy
import com.tck.opecv.camera.databinding.ActivityImageEditBinding
import org.opencv.android.Utils
import org.opencv.core.Core
import org.opencv.core.Mat
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.getStructuringElement

class ImageEditActivity : AppCompatActivity() {

    companion object {
        val TAG = "opencv"
    }

    //source bitmap width:4032,height:3024
    private lateinit var binding: ActivityImageEditBinding

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        url = intent.getStringExtra("url") ?: ""

        val decodeFile = BitmapFactory.decodeFile(url)
        decodeFile?.let {
            Log.d(TAG, "source bitmap width:${it.width},height:${it.height}")
        }
        binding.ivCover.setImageBitmap(decodeFile)
        binding.btnRecognitionTextRegion.setOnClickListener {
            test()
        }
    }

    fun test() {
        val srcMat = Mat()
        val dstMat = Mat()
        val srcBitmap = BitmapFactory.decodeFile(url)
        Utils.bitmapToMat(srcBitmap, srcMat)
        Imgproc.cvtColor(srcMat, dstMat, Imgproc.COLOR_BGR2GRAY)
        Imgproc.adaptiveThreshold(
            dstMat,
            dstMat,
            255.toDouble(),
            Imgproc.ADAPTIVE_THRESH_MEAN_C,
            Imgproc.THRESH_BINARY, 15, (-2).toDouble()
        )
        val d1 = (-1).toDouble()
        val d3 = 3.toDouble()

        val temp = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(d3, d3), Point(d1, d1))
        Imgproc.morphologyEx(dstMat, dstMat, Imgproc.MORPH_OPEN, temp, Point(d1, d1), 1, 0)
        Imgproc.cvtColor(dstMat, dstMat, Imgproc.COLOR_GRAY2BGR)

        Utils.matToBitmap(dstMat, srcBitmap)
        binding.ivCover.setImageBitmap(srcBitmap)
        srcMat.release()
        dstMat.release()
    }
}
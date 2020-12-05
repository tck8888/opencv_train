package com.tck.opecv.camera

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tck.opecv.camera.databinding.ActivityImageEditBinding

class ImageEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageEditBinding

    private var url = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        url = intent.getStringExtra("url") ?: ""

        val decodeFile = BitmapFactory.decodeFile(url)
        binding.ivCover.setImageBitmap(decodeFile)
    }
}
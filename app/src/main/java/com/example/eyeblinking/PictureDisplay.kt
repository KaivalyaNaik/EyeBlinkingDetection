package com.example.eyeblinking

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_picture.*


class PictureDisplay : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)


        val image = intent.getByteArrayExtra("image")
        val bitmap = image?.size?.let { size -> BitmapFactory.decodeByteArray(image, 0, size) }

        val matrix = Matrix()
        matrix.postRotate(-90f)
        matrix.postScale(-1f,1f)
        val scaledBitmap =
            bitmap?.width?.let { Bitmap.createScaledBitmap(bitmap, it, bitmap?.height, true) }

        val rotatedBitmap = scaledBitmap?.let {
            Bitmap.createBitmap(
                it,
                0,
                0,
                scaledBitmap.width,
                scaledBitmap.height,
                matrix,
                true
            )
        }


        imageView.setImageBitmap(rotatedBitmap)
    }
}
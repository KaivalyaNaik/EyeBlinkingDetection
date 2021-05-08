package com.example.eyeblinking


import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity


class PictureActivity : AppCompatActivity() {
    private lateinit var imageView: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picture)
        imageView = findViewById(R.id.img)
        imageView.setImageBitmap(MainActivity.bitmap)
    }
}
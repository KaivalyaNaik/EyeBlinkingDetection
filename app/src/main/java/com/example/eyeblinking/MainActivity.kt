package com.example.eyeblinking

import android.Manifest.permission
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.face.FaceDetector
import com.google.android.gms.vision.face.LargestFaceFocusingProcessor
import java.io.IOException
import java.util.*

class MainActivity : AppCompatActivity(), SurfaceHolder.Callback,CameraSource.PictureCallback{
    private lateinit var surfaceHolder: SurfaceHolder
    private lateinit var surfaceView: SurfaceView
    private val neededPermissions = arrayOf(permission.CAMERA)
    private lateinit var detector: FaceDetector
    private lateinit var cameraSource: CameraSource
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        surfaceView = findViewById(R.id.surfaceView)
        detector = FaceDetector.Builder(this)
                .setProminentFaceOnly(true) // optimize for single, relatively large face
                .setTrackingEnabled(true) // enable face tracking
                .setClassificationType( /* eyes open and smile */FaceDetector.ALL_CLASSIFICATIONS)
                .setMode(FaceDetector.FAST_MODE) // for one face this is OK
                .build()
        if (!detector.isOperational) {
            Log.w("MainActivity", "Detector Dependencies are not yet available")
        } else {
            Log.w("MainActivity", "Detector Dependencies are available")
            if (surfaceView != null) {
                val result = checkPermission()
                if (result) {
                    setViewVisibility(R.id.tv_capture)
                    setViewVisibility(R.id.surfaceView)
                    setupSurfaceHolder()
                }
            }
        }
    }

    private fun checkPermission(): Boolean {
        val permissionsNotGranted = ArrayList<String>()
        for (permission in neededPermissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsNotGranted.add(permission)
            }
        }
        if (!permissionsNotGranted.isEmpty()) {
            var shouldShowAlert = false
            for (permission in permissionsNotGranted) {
                shouldShowAlert = ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
            }
            if (shouldShowAlert) {
                showPermissionAlert(permissionsNotGranted.toTypedArray())
            } else {
                requestPermissions(permissionsNotGranted.toTypedArray())
            }
            return false
        }
        return true
    }

    private fun showPermissionAlert(permissions: Array<String>) {
        val alertBuilder = AlertDialog.Builder(this)
        alertBuilder.setCancelable(true)
        alertBuilder.setTitle(R.string.permission_required)
        alertBuilder.setMessage(R.string.permission_message)
        alertBuilder.setPositiveButton(R.string.yes) { dialog, which -> requestPermissions(permissions) }
        val alert = alertBuilder.create()
        alert.show()
    }

    private fun requestPermissions(permissions: Array<String>) {
        ActivityCompat.requestPermissions(this@MainActivity, permissions, CAMERA_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == CAMERA_REQUEST) {
            for (result in grantResults) {
                if (result == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(this@MainActivity, R.string.permission_warning, Toast.LENGTH_LONG).show()
                    checkPermission()
                    return
                }
            }
            setViewVisibility(R.id.tv_capture)
            setViewVisibility(R.id.surfaceView)
            setupSurfaceHolder()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun setViewVisibility(id: Int) {
        val view = findViewById<View>(id)
        if (view != null) {
            view.visibility = View.VISIBLE
        }
    }

    private fun setupSurfaceHolder() {
        cameraSource = CameraSource.Builder(this, detector)
                .setFacing(CameraSource.CAMERA_FACING_FRONT)
                .setRequestedFps(2.0f)
                .setAutoFocusEnabled(true)
                .build()
        surfaceHolder = surfaceView.holder
        surfaceHolder.addCallback(this)
    }

    fun takePicture(){
        cameraSource.takePicture(null,this)
    }



    override fun surfaceCreated(surfaceHolder: SurfaceHolder) {
        startCamera()
    }

    private fun startCamera() {
        try {
            if (ActivityCompat.checkSelfPermission(this, permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return
            }
            cameraSource!!.start(surfaceHolder)
            detector!!.setProcessor(LargestFaceFocusingProcessor(detector,
                    GraphicFaceTracker(this)))
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun surfaceChanged(surfaceHolder: SurfaceHolder, i: Int, i1: Int, i2: Int) {}

    fun stopCamera(){
        val intent =Intent(this,Success::class.java)
        startActivity(intent)
        Log.d("MainActivity","Stop Camera")

        finish()
    }
    override fun surfaceDestroyed(surfaceHolder: SurfaceHolder) {
        val handler =Handler(Looper.getMainLooper())
        handler.post {
            cameraSource.release()
            detector.release()
        }
        surfaceHolder.surface.release()
    }



    companion object {
        const val CAMERA_REQUEST = 101
        var bitmap: Bitmap? = null
    }

    override fun onPictureTaken(image: ByteArray?) {
        val intent =Intent(this,PictureDisplay::class.java)
        intent.putExtra("image",image)
        startActivity(intent)

        finish()
    }

}
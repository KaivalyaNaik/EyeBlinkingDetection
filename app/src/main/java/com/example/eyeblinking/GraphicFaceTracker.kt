package com.example.eyeblinking

import android.util.Log
import android.widget.Toast
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.Tracker
import com.google.android.gms.vision.face.Face
import kotlin.math.min

class GraphicFaceTracker(mainActivity: MainActivity):Tracker<Face>() {
    private val OPEN_THRESHOLD = 0.85f
    private val CLOSE_THRESHOLD = 0.2f
    private var mainActivity: MainActivity =mainActivity
    private var state = 0



    private fun blink(value :Float){
        when(state){
            0->{
                if(value > OPEN_THRESHOLD){
                    state =1
                    Log.d("GraphicFaceTracker","value"+value.toString()+"state :"+state.toString() )
                     //Both Eyes are Open
                 }
                return
            }
            1->{
                if(value < CLOSE_THRESHOLD){
                    state=2
                    Log.d("GraphicFaceTracker","value"+value.toString()+"state :"+state.toString() )
                }
                return
            }
            2->{
                if(value > OPEN_THRESHOLD)
                    state =0
                Log.d("GraphicFaceTracker","value"+value.toString()+"state :"+state.toString() )
                mainActivity.stopCamera()


                return
            }
        }
    }

    @Override
    override fun onUpdate(detectionResult: Detector.Detections<Face>?, face: Face?) {
        val left =face?.isLeftEyeOpenProbability
        val right=face?.isRightEyeOpenProbability

        if(left ==Face.UNCOMPUTED_PROBABILITY || right == Face.UNCOMPUTED_PROBABILITY){
            return
        }
        if(left != null && right!=null){
            blink(min(left,right))
        }

    }

}
package com.mrr.scaleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        var lineScaleView = findViewById<ScaleView>(R.id.horizontalScaleView)
        lineScaleView.mProgressChangeListener = progress()

        var verticalScaleView = findViewById<ScaleView>(R.id.verticalScaleView)
        verticalScaleView.mProgressChangeListener = progress()

        var circleScaleView = findViewById<ScaleView>(R.id.circleScaleView)
        circleScaleView.mProgressChangeListener = progress()
    }

    inner class progress : ScaleView.ProgressChangeListener {

        override fun progressChange(progress: Float) {

            Log.d(TAG, "progressChange: progress $progress")

        }

    }
}
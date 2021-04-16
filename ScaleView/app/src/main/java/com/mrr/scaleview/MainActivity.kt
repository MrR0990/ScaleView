package com.mrr.scaleview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.mrr.libscaleview.ScaleView

class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

    }

    inner class progress : ScaleView.ProgressChangeListener {

        override fun progressChange(progress: Float) {

            Log.d(TAG, "progressChange: progress $progress")

        }

    }
}
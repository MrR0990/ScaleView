package com.mrr.view

import android.content.Context
import kotlin.jvm.internal.Intrinsics

class Builder {

    var p: ScaleParam;

    constructor(context: Context) {
        p = ScaleParam()
        p.context = context
    }


    fun setCalibrationWidth(var1: Float): Builder {
        p.mScaleWidth = var1
        return this
    }


    fun setCalibrationNodeWidth(var1: Float): Builder {
        p.mScaleNodeWidth = var1
        return this
    }


    fun setCalibrationStyle(var1: CalibrationStyle): Builder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mScaleStyle = var1
        return this
    }


    fun setCalibrationDirect(var1: CalibrationStyle): Builder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mScaleDirect = var1
        return this
    }


    fun setCursorLoc(var1: CalibrationStyle): Builder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mCursorLoc = var1
        return this
    }


    fun setCursorWidth(var1: Float): Builder {
        p.mCursorWidth = var1
        return this
    }


    fun setCursorGap(var1: Float): Builder {
        p.mCursorGap = var1
        return this
    }


    fun setTotalCalibration(var1: Int): Builder {
        p.mTotalProgress = var1
        return this
    }


    fun setUnitCalibration(var1: Int): Builder {
        p.mUnitScale = var1
        return this
    }


    fun setDefaultColor(var1: Int): Builder {
        p.mDefaultColor = var1
        return this
    }


    fun setProgressColor(var1: Int): Builder {
        p.mProgressColor = var1
        return this
    }

    fun setProgressListener(var1: ScaleParam.ProgressListener): Builder {
        p.mProgressListener = var1
        return this
    }

    fun create(): ScaleView {
        val view = ScaleView(p.context);
        p.apply(view.mParam)
        return view;
    }


}
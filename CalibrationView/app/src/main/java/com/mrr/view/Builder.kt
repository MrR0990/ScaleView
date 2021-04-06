package com.mrr.view

import android.content.Context
import kotlin.jvm.internal.Intrinsics

class Builder {

    var p: CalibrationParam;

    constructor(context: Context) {
        p = CalibrationParam()
        p.context = context
    }


    fun setCalibrationWidth(var1: Float): Builder {
        p.mCalibrationWidth = var1
        return this
    }


    fun setCalibrationNodeWidth(var1: Float): Builder {
        p.mCalibrationNodeWidth = var1
        return this
    }


    fun setCalibrationStyle(var1: CalibrationStyle): Builder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mCalibrationStyle = var1
        return this
    }


    fun setCalibrationDirect(var1: CalibrationStyle): Builder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mCalibrationDirect = var1
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
        p.mUnitCalibration = var1
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

    fun setProgressListener(var1: CalibrationParam.ProgressListener): Builder {
        p.mProgressListener = var1
        return this
    }

    fun create(): CalibrationView {
        val view = CalibrationView(p.context);
        p.apply(view.mParam)
        return view;
    }


}
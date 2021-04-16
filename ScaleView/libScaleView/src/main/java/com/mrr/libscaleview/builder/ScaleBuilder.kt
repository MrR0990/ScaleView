package com.mrr.libscaleview.builder

import android.content.Context
import com.mrr.libscaleview.ScaleView
import com.mrr.libscaleview.attr.ScaleViewAttr
import com.mrr.libscaleview.enum.ScaleAttrEnum
import kotlin.jvm.internal.Intrinsics

class ScaleBuilder {

    var p: ScaleViewAttr;

    constructor(context: Context) {
        p = ScaleViewAttr()
        p.context = context
    }


    fun setCalibrationWidth(var1: Float): ScaleBuilder {
        p.mScaleWidth = var1
        return this
    }


    fun setCalibrationNodeWidth(var1: Float): ScaleBuilder {
        p.mScaleNodeWidth = var1
        return this
    }


    fun setCalibrationStyle(var1: ScaleAttrEnum): ScaleBuilder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mScaleStyle = var1
        return this
    }


    fun setCalibrationDirect(var1: ScaleAttrEnum): ScaleBuilder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mScaleDirect = var1
        return this
    }


    fun setCursorLoc(var1: ScaleAttrEnum): ScaleBuilder {
        Intrinsics.checkNotNullParameter(var1, "<set-?>")
        p.mCursorSeat = var1
        return this
    }


    fun setCursorWidth(var1: Float): ScaleBuilder {
        p.mCursorWidth = var1
        return this
    }


    fun setCursorGap(var1: Float): ScaleBuilder {
        p.mCursorGap = var1
        return this
    }


    fun setTotalCalibration(var1: Int): ScaleBuilder {
        p.mTotalProgress = var1
        return this
    }


    fun setUnitCalibration(var1: Int): ScaleBuilder {
        p.mUnitScale = var1
        return this
    }


    fun setDefaultColor(var1: Int): ScaleBuilder {
        p.mDefaultColor = var1
        return this
    }


    fun setProgressColor(var1: Int): ScaleBuilder {
        p.mProgressColor = var1
        return this
    }

    fun setProgressListener(var1: ScaleViewAttr.ProgressListener): ScaleBuilder {
        p.mProgressListener = var1
        return this
    }


    fun setScaleTextColor(var1: Int): ScaleBuilder {
        p.mScaleTextColor = var1
        return this
    }

    fun setScaleTextSize(var1: Float): ScaleBuilder {
        p.mScaleTextSize = var1
        return this
    }

    fun setScaleTextGap(var1: Float): ScaleBuilder {
        p.mScaleTextGap = var1
        return this
    }

    fun create(): ScaleView {
        val view = ScaleView(p.context);
        p.apply(view.mAttr)
        return view;
    }


}
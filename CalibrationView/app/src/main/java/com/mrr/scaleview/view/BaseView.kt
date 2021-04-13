package com.mrr.scaleview.view

import android.graphics.Canvas
import android.graphics.RectF

abstract class BaseView {

    /**
     * 刻度线一半的厚度,方便计算
     */
    var mHalfCalibration = 0f;

    /**
     * 圆形刻度之间的角度
     */
    var mPreDegrees = 0f

    /**
     * 圆形刻度的半径
     */
    var mCircleRadius = 0f

    /**
     * 每一个刻度最长可绘制的空间
     */
    var mInterval = 0f

    var mDrawSpace = 0f

    /**
     * 刻度之间的间距
     */
    var mPerInterval = 0f

    //第一个节点是一个刻度节点/节点刻度的宽度/高度
    var nodeLength = 0f

    //普通刻度占组件减去padding之后的宽度/高度
    var linelength = 0f


    /**
     * 线性刻度切割canvas使用
     */
    var mClipRect: RectF? = null

    var mCircleProgressAngel = 0.0

    var mClipProgress = 0f


    var nodeStartX = 0f
    var nodeStopX = 0f

    var nodeStartY = 0f
    var nodeStopY = 0f

    var startX = 0f
    var stopX = 0f

    var startY = 0f
    var stopY = 0f

    abstract fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    abstract fun onDraw(canvas: Canvas?)
}
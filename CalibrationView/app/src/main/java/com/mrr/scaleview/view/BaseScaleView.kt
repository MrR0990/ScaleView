package com.mrr.scaleview.view

import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.RectF
import com.mrr.scaleview.ScaleView
import com.mrr.scaleview.attr.ScaleViewAttr
import com.mrr.scaleview.rectf.CursorRectF
import com.mrr.scaleview.util.UnitConversion.Companion.px
import com.mrr.scaleview.util.UnitConversion.Companion.sp2px
import java.util.function.Consumer

abstract class BaseScaleView : CursorRectF.AngelChangeListener, ScaleView.ProgressChangeListener {

    var mAttr: ScaleViewAttr

    var mProgressChangeListener: ScaleView.ProgressChangeListener? = null


    /**
     * 刻度线一半的宽度
     */
    var halfCalibration = 0f;

    /**
     * 圆形刻度之间的角度
     */
    var preDegrees = 0f

    /**
     * 圆形刻度的半径
     */
    var circleRadius = 0f

    /**
     * 每一个刻度最长可绘制的空间
     */
    var interval = 0f

    var drawSpace = 0f

    /**
     * 刻度之间的间距
     */
    var perInterval = 0f

    //第一个节点是一个刻度节点/节点刻度的宽度/高度
    var nodeLength = 0f

    //普通刻度占组件减去padding之后的宽度/高度
    var linelength = 0f

    var circleProgressAngel = 0.0

    var clipProgress = 0f

    var nodeStartX = 0f
    var nodeStopX = 0f

    var nodeStartY = 0f
    var nodeStopY = 0f

    var startX = 0f
    var stopX = 0f

    var startY = 0f
    var stopY = 0f

    // 屏幕最中心的位置
    var centerX = 0f
    var centerY = 0f


    /**
     * 线性刻度切割canvas使用
     */
    var clipRect: RectF

    var cursorRectF: CursorRectF

    var cursorMatrix: Matrix

    /**
     *画笔
     */
    var originColorPaint: Paint
    var changeColorPaint: Paint
    var cursorPaint: Paint
    var textPaint: Paint

    var fontMetrics: Paint.FontMetrics? = null

    constructor(mAttr: ScaleViewAttr) {
        this.mAttr = mAttr

        clipRect = RectF()
        cursorRectF = CursorRectF(this)
        cursorMatrix = Matrix()

        halfCalibration = mAttr.mScaleLineWidth / 2

        if (null != mAttr.mCursorBitmap) {
            cursorRectF?.mScaleX = mAttr.mCursorWidth.px / mAttr.mCursorBitmap!!.width
            cursorRectF?.mScaleY = mAttr.mCursorWidth.px / mAttr.mCursorBitmap!!.height
        }

        originColorPaint = Paint()
        changeColorPaint = Paint()
        cursorPaint = Paint()
        textPaint = Paint()
        initPaint()
        fontMetrics = textPaint.fontMetrics
    }


    abstract fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int)
    abstract fun onDraw(canvas: Canvas?, touchX: Float, touchY: Float)
    abstract fun initTouchXY(
        touchXCon: Consumer<Float>,
        touchYCon: Consumer<Float>
    );//初始化触摸位置,也就是游标初始位置


    private fun initPaint() {
        originColorPaint.isAntiAlias = true
        originColorPaint.style = Paint.Style.STROKE
        originColorPaint.color = mAttr.mDefaultColor
        originColorPaint.strokeWidth = mAttr.mScaleLineWidth

        changeColorPaint.isAntiAlias = true
        changeColorPaint.style = Paint.Style.STROKE
        changeColorPaint.color = mAttr.mProgressColor
        changeColorPaint.strokeWidth = mAttr.mScaleLineWidth


        cursorPaint.isAntiAlias = true
        cursorPaint.style = Paint.Style.STROKE
        cursorPaint.strokeWidth = 1f

        textPaint.isAntiAlias = true
        textPaint.style = Paint.Style.FILL
        textPaint.color = mAttr.mScaleTextColor
        textPaint.textSize = mAttr.mScaleTextSize.sp2px
    }

    override fun angelChange(curAngel: Double) {
        TODO("Not yet implemented")
    }

    override fun progressChange(progress: Float) {
        mProgressChangeListener?.progressChange(progress)
    }
}
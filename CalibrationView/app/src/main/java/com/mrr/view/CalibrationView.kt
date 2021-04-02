package com.mrr.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

class CalibrationView : View {
    val TAG = "CalibrationProgressView"

    var mParam = CalibrationParam();

    var mProgressListener: CalibrationParam.ProgressListener? = null

    var mWidth = 0
    var mHeight = 0

    var mPaddingLeft = 0
    var mPaddingTop = 0
    var mPaddingRight = 0
    var mPaddingBottom = 0

    /**
     * 每个刻度之间的间隔
     */
    var mUnit = 0

    /**
     * 当前触摸到的位置
     */
    var mTouchY = 0f;

    /**
     * 刻度画笔
     */
    var mOriginColorPaint = Paint()

    /**
     * 走过的刻度画笔
     */
    var mChangeColorPaint = Paint()

    /**
     * 游标画笔
     */
    var mCursorPaint = Paint()

    /**
     * 是否已经初始化画笔
     */
    var isInit = false


    constructor(context: Context?) : super(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        initData()
    }

    fun initData() {

        if (!isInit) {
            mOriginColorPaint.isAntiAlias = true
            mOriginColorPaint.style = Paint.Style.STROKE
            mOriginColorPaint.color = mParam.mDefaultColor
            mOriginColorPaint.strokeWidth = 5f

            mChangeColorPaint.isAntiAlias = true
            mChangeColorPaint.style = Paint.Style.STROKE
            mChangeColorPaint.color = mParam.mProgressColor
            mChangeColorPaint.strokeWidth = 5f


            mCursorPaint.isAntiAlias = true
            mCursorPaint.style = Paint.Style.STROKE
            mCursorPaint.strokeWidth = 1f

            mProgressListener = mParam.mProgressListener

            isInit = true
        }

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mPaddingLeft = paddingLeft
        mPaddingTop = paddingTop
        mPaddingRight = paddingRight
        mPaddingBottom = paddingBottom

        mUnit = (mHeight - mPaddingTop - mPaddingBottom) / mParam.mTotalProgress
        mTouchY = (mPaddingTop + mUnit).toFloat()

        setMeasuredDimension(mWidth, mHeight)

        Log.d(TAG, "onMeasure : mWidth : $mWidth mHeight : $mHeight")
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawCalibration(canvas, mChangeColorPaint, 0, mTouchY.toInt())
        drawCalibration(canvas, mOriginColorPaint, mTouchY.toInt(), mHeight)

        drawCursor(canvas)
    }

    /**
     * 画刻度尺游标
     */
    private fun drawCursor(canvas: Canvas?) {

        var startX = (mWidth / 4 * 3).toFloat()
        var startY = mTouchY
        var cursorpath = Path()
        cursorpath.moveTo(startX, mTouchY)

        var targetXLength =
            Math.sqrt((mParam.mCursorWidth * mParam.mCursorWidth - (mParam.mCursorWidth / 2) * (mParam.mCursorWidth / 2)).toDouble())

        cursorpath.lineTo(
            (startX + targetXLength).toFloat(),
            mTouchY - mParam.mCursorWidth / 2
        )
        cursorpath.lineTo(
            (startX + targetXLength).toFloat(),
            mTouchY + mParam.mCursorWidth / 2
        )
        cursorpath.close()
        canvas?.drawPath(cursorpath, mCursorPaint)
    }

    /**
     * 画刻度
     */
    private fun drawCalibration(canvas: Canvas?, paint: Paint, from: Int, to: Int) {

        canvas!!.save()


        //这里就是最重要的分割canvas,可以分成上下左右的任何部分
        val rect = Rect(0, from, mWidth, to)
        canvas.clipRect(rect)


        var lineLength = mWidth / 2
        var stopY = mPaddingTop + mUnit

        for (index in 0..mParam.mTotalProgress) {
            canvas?.drawLine(
                (lineLength / 2).toFloat(),
                stopY.toFloat(),
                (lineLength / 2 + lineLength).toFloat(),
                stopY.toFloat(),
                paint
            )
            stopY += mUnit

            if (stopY > to) {
                break
            }
        }

        canvas.restore()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {

                mTouchY = event.y

                if (mTouchY < mPaddingTop + mUnit) {
                    mTouchY = (mPaddingTop + mUnit).toFloat()
                }

                if (mTouchY > mPaddingTop + (mUnit * (mParam.mTotalProgress + 1))) {
                    mTouchY =
                        (mPaddingTop + (mUnit * (mParam.mTotalProgress + 1))).toFloat()
                }
            }
        }

        Log.d(TAG, "mTouchY: $mTouchY")
        invalidate()

        mProgressListener?.progressChanged(mTouchY)
        return true
    }


}
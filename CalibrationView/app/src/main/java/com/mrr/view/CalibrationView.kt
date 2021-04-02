package com.mrr.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * 1. 画线性刻度
 * - 解决初始状态游标位置问题
 * - 设置一个背景颜色,看看是不是居中
 * - 绘制刻度节点长度不同效果
 *
 * 2. 画线性游标
 * - 游标按照节点最长的位置绘制
 */
class CalibrationView : View {
    val TAG = "CalibrationProgressView"

    var mContext: Context? = null
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
    var mUnitInterval = 0

    /**
     * 当前触摸到的位置
     */
    var mTouchY = 0f;

    /**
     * 默认刻度画笔
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


    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        setBackgroundColor(resources.getColor(R.color.yellow))
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (isInit) {
            return
        }

        initData(widthMeasureSpec, heightMeasureSpec)

        isInit = true

        Log.d(TAG, "onMeasure : mWidth : $mWidth mHeight : $mHeight")
    }

    private fun initData(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        mWidth = MeasureSpec.getSize(widthMeasureSpec)
        mHeight = MeasureSpec.getSize(heightMeasureSpec)
        mPaddingLeft = paddingLeft
        mPaddingTop = paddingTop
        mPaddingRight = paddingRight
        mPaddingBottom = paddingBottom

        mUnitInterval = (mHeight - mPaddingTop - mPaddingBottom) / mParam.mTotalProgress
        mTouchY = (mPaddingTop + mUnitInterval).toFloat()

        setMeasuredDimension(mWidth, mHeight)

        mOriginColorPaint.isAntiAlias = true
        mOriginColorPaint.style = Paint.Style.STROKE
        mOriginColorPaint.color = mParam.mDefaultColor
        mOriginColorPaint.strokeWidth = mParam.mCalibrationThick

        mChangeColorPaint.isAntiAlias = true
        mChangeColorPaint.style = Paint.Style.STROKE
        mChangeColorPaint.color = mParam.mProgressColor
        mChangeColorPaint.strokeWidth = mParam.mCalibrationThick


        mCursorPaint.isAntiAlias = true
        mCursorPaint.style = Paint.Style.STROKE
        mCursorPaint.strokeWidth = 1f

        mProgressListener = mParam.mProgressListener
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        drawCalibration(canvas, mChangeColorPaint, 0, mTouchY.toInt())
        drawCalibration(canvas, mOriginColorPaint, mTouchY.toInt(), mHeight)

//        drawCursor(canvas)
    }

    /**
     * 画游标
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


        //第一个节点是一个刻度节点
        var lineLength = (mWidth - mPaddingLeft - mPaddingRight) * mParam.mCalibrationNodeWidth


        var startX = (mWidth - lineLength) / 2 + mPaddingLeft
        var startY = mPaddingTop

        var stopX = startX + lineLength
        var stopY = startY

        for (index in 0..mParam.mTotalProgress) {
            if (stopY > to) {
                break
            }

            canvas?.drawLine(
                startX,
                startY.toFloat(),
                stopX,
                stopY.toFloat(),
                paint
            )

            startY += mUnitInterval
            stopY += mUnitInterval

            Log.d(TAG, "index : $index ")
        }

        canvas.restore()

    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {

                mTouchY = event.y

                if (mTouchY < mPaddingTop + mUnitInterval) {
                    mTouchY = (mPaddingTop + mUnitInterval).toFloat()
                }

                if (mTouchY > mPaddingTop + (mUnitInterval * (mParam.mTotalProgress + 1))) {
                    mTouchY =
                        (mPaddingTop + (mUnitInterval * (mParam.mTotalProgress + 1))).toFloat()
                }
            }
        }

        Log.d(TAG, "mTouchY: $mTouchY")
        invalidate()

        mProgressListener?.progressChanged(mTouchY)
        return true
    }


}
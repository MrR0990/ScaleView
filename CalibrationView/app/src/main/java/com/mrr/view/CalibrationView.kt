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

    var mWidth = 0f
    var mHeight = 0f


    /**
     * 刻度线一半的厚度,方便计算
     */
    var mHalfCalibration = 0f;

    /**
     * 刻度之间的缝隙大小
     */
    var mPerInterval = 0f

    /**
     * 圆形刻度之间的角度
     */
    var mPreAngle = 0f

    /**
     * 圆形刻度的半径
     */
    var mCircleRadius = 0f

    /**
     * 每个一个刻度最长可绘制的空间
     */
    var mInterval = 0f

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

    var mCurrentRotationAngle = -90f

    // 屏幕最中心的位置
    private var mCenterX = 0f
    private var mCenterY = 0f

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        var typeArray = context?.obtainStyledAttributes(attrs, R.styleable.CalibrationView)

        mParam.mCalibrationWidth =
            typeArray!!.getFloat(R.styleable.CalibrationView_calibrationWidth, 0.5f)


        mParam.mCalibrationNodeWidth =
            typeArray!!.getFloat(R.styleable.CalibrationView_calibrationNodeWidth, 0.7f)

        mParam.mCalibrationThick =
            typeArray!!.getDimension(R.styleable.CalibrationView_calibrationThick, 5f)


        var style = typeArray!!.getInt(R.styleable.CalibrationView_calibrationStyle, -1)
        if (style > 0) {
            setCalibrationStyle(style)
        }

        var direct =
            typeArray!!.getInt(R.styleable.CalibrationView_calibrationDirect, -1)
        if (direct > 0) {
            setCalibrationDirect(direct)
        }

        var cursorLoc =
            typeArray!!.getInt(R.styleable.CalibrationView_cursorLoc, -1)
        if (cursorLoc > 0) {
            setCursorLoc(cursorLoc)
        }

        mParam.mCursorWidth =
            typeArray!!.getDimension(R.styleable.CalibrationView_cursorWidth, 20f)

        mParam.mCursorGap =
            typeArray!!.getDimension(R.styleable.CalibrationView_cursorGap, 5f)

        mParam.mTotalProgress =
            typeArray!!.getInt(R.styleable.CalibrationView_totalProgress, 60)

        mParam.mUnitCalibration =
            typeArray!!.getInt(R.styleable.CalibrationView_unitCalibration, 10)

        mParam.mDefaultColor =
            typeArray!!.getColor(R.styleable.CalibrationView_defaultColor, Color.DKGRAY)

        mParam.mProgressColor =
            typeArray!!.getColor(R.styleable.CalibrationView_progressColor, Color.DKGRAY)

        typeArray.recycle()
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

        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()
        setMeasuredDimension(mWidth.toInt(), mHeight.toInt())

        initPaint()


        if (mParam.mCalibrationStyle == CalibrationStyle.LINE) {

            //刻度线本身占用的空间
            var calibrationSpace = mParam.mCalibrationThick * (mParam.mTotalProgress + 1)

            if (mParam.mCalibrationDirect == CalibrationStyle.VERTICAL) {

                //总共的绘制空间
                var drawSpace = mHeight - mPaddingTop - mPaddingBottom
                //刻度之间的缝隙大小
                mPerInterval = (drawSpace - calibrationSpace) / mParam.mTotalProgress
                //每个一个刻度最长可绘制的空间
                mInterval = mWidth - paddingLeft - paddingRight

            } else if (mParam.mCalibrationDirect == CalibrationStyle.HORIZONTAL) {

                var drawSpace = mWidth - mPaddingLeft - paddingRight
                mPerInterval = (drawSpace - calibrationSpace) / mParam.mTotalProgress
                mInterval = mHeight - paddingTop - paddingBottom
            }

        } else if (mParam.mCalibrationStyle == CalibrationStyle.CIRCLE) {
            mPreAngle = (Math.PI * 2 / mParam.mTotalProgress).toFloat()
            mCircleRadius =
                Math.min(
                    mWidth - paddingLeft - paddingRight,
                    mHeight - paddingTop - paddingBottom
                ) / 2

            mCenterX = mWidth / 2
            mCenterY = mHeight / 2
        }


        mHalfCalibration = mParam.mCalibrationThick / 2

        mTouchY = (mPaddingTop + mPerInterval).toFloat()

        mProgressListener = mParam.mProgressListener
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        var rect = RectF()
        rect.left = mPaddingLeft.toFloat()
        rect.top = mPaddingTop.toFloat()
        rect.right = (mWidth - mPaddingRight).toFloat()
        rect.bottom = (mHeight - mPaddingBottom).toFloat()

        canvas?.drawRoundRect(rect, 0f, 0f, mCursorPaint)
//        drawCalibration(canvas, mChangeColorPaint, 0, mTouchY.toInt())

        if (mParam.mCalibrationStyle == CalibrationStyle.LINE) {
            drawLineCalibration(canvas, mOriginColorPaint, 0f, mHeight)
        } else if (mParam.mCalibrationStyle == CalibrationStyle.CIRCLE) {
            drawCircleCalibration(canvas, mOriginColorPaint, 0f, mHeight)
        }


//        drawCursor(canvas)
    }

    private fun drawCircleCalibration(canvas: Canvas?, paint: Paint, from: Float, to: Float) {

        canvas?.drawCircle(mWidth / 2, mHeight / 2, mCircleRadius, mCursorPaint)

        //节点刻度和普通刻度的长度差
        var lengthDiff =
            ((mCircleRadius * mParam.mCalibrationNodeWidth) - (mCircleRadius * mParam.mCalibrationWidth)) / 2

        var startX = 0f
        var stopX = 0f
        var startY = 0f
        var stopY = 0f

        for (index in 0..mParam.mTotalProgress - 1) {
            // 初始角度 + 当前旋转的角度
            val angle = (index * mPreAngle).toDouble()

            //从外往内绘制
            if (index % mParam.mUnitCalibration == 0) {
                startX = (mCenterX + mCircleRadius * Math.cos(angle)).toFloat()
                startY = (mCenterY + mCircleRadius * Math.sin(angle)).toFloat()
                stopX = (mCenterX + (mCircleRadius - mCircleRadius * mParam.mCalibrationNodeWidth)
                        * Math.cos(angle)).toFloat()
                stopY = (mCenterY + (mCircleRadius - mCircleRadius * mParam.mCalibrationNodeWidth)
                        * Math.sin(angle)).toFloat()
            } else {
                startX = (mCenterX + (mCircleRadius - lengthDiff)
                        * Math.cos(angle)).toFloat()
                startY = (mCenterY + (mCircleRadius - lengthDiff) * Math.sin(angle)).toFloat()
                stopX =
                    (mCenterX + (mCircleRadius - mCircleRadius * mParam.mCalibrationWidth - lengthDiff)
                            * Math.cos(angle)).toFloat()
                stopY =
                    (mCenterY + (mCircleRadius - mCircleRadius * mParam.mCalibrationWidth - lengthDiff)
                            * Math.sin(angle)).toFloat()
            }


            canvas!!.drawLine(
                startX, startY, stopX, stopY,
                mOriginColorPaint
            )

        }
    }

    /**
     * 画线性的刻度
     */
    private fun drawLineCalibration(canvas: Canvas?, paint: Paint, from: Float, to: Float) {


        canvas!!.save()

        //这里就是最重要的分割canvas,可以分成上下左右的任何部分
        val rect = Rect(0, from.toInt(), mWidth.toInt(), to.toInt())
        canvas.clipRect(rect)


        //第一个节点是一个刻度节点
        var nodeLength = mInterval * mParam.mCalibrationNodeWidth

        //普通刻度占组件减去padding之后的宽度/高度
        var linelength = mInterval * mParam.mCalibrationWidth

        var nodeStartX = 0f
        var nodeStopX = 0f

        var nodeStartY = 0f
        var nodeStopY = 0f

        var startX = 0f
        var stopX = 0f

        var startY = 0f
        var stopY = 0f


        if (mParam.mCalibrationDirect == CalibrationStyle.VERTICAL) {

            nodeStartX = (mWidth - nodeLength) / 2
            nodeStopX = nodeStartX + nodeLength

            startX = (mWidth - linelength) / 2
            stopX = startX + linelength

            startY = mPaddingTop + mHalfCalibration
            stopY = startY

            for (index in 0..mParam.mTotalProgress) {
                if (stopY > to) {
                    break
                }

                canvas?.drawLine(
                    if (index % mParam.mUnitCalibration == 0) nodeStartX.toFloat() else startX,
                    startY.toFloat(),
                    if (index % mParam.mUnitCalibration == 0) nodeStopX.toFloat() else stopX,
                    stopY.toFloat(),
                    paint
                )

                startY += (mPerInterval + mParam.mCalibrationThick)
                stopY += (mPerInterval + mParam.mCalibrationThick)

                Log.d(TAG, "index : $index ")
            }

        } else if (mParam.mCalibrationDirect == CalibrationStyle.HORIZONTAL) {

            nodeStartX = paddingLeft + mHalfCalibration
            nodeStopX = nodeStartX

            nodeStartY = (mHeight - nodeLength) / 2
            nodeStopY = nodeStartY + nodeLength

            startY = (mHeight - linelength) / 2
            stopY = startY + linelength

            for (index in 0..mParam.mTotalProgress) {

                canvas?.drawLine(
                    nodeStartX,
                    if (index % mParam.mUnitCalibration == 0) nodeStartY.toFloat() else startY,
                    nodeStopX,
                    if (index % mParam.mUnitCalibration == 0) nodeStopY.toFloat() else stopY,
                    paint
                )

                nodeStartX += (mPerInterval + mParam.mCalibrationThick)
                nodeStopX += (mPerInterval + mParam.mCalibrationThick)

            }
        }

        canvas.restore()

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


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {

                mTouchY = event.y

                if (mTouchY < mPaddingTop + mPerInterval) {
                    mTouchY = (mPaddingTop + mPerInterval).toFloat()
                }

                if (mTouchY > mPaddingTop + (mPerInterval * (mParam.mTotalProgress + 1))) {
                    mTouchY =
                        (mPaddingTop + (mPerInterval * (mParam.mTotalProgress + 1))).toFloat()
                }
            }
        }

        Log.d(TAG, "mTouchY: $mTouchY")
        invalidate()

        mProgressListener?.progressChanged(mTouchY)
        return true
    }


    private fun initPaint() {
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
    }

    fun setCalibrationStyle(style: Int) {
        when (style) {
            CalibrationStyle.LINE.value -> {
                mParam.mCalibrationStyle = CalibrationStyle.LINE
            }
            CalibrationStyle.CIRCLE.value -> {
                mParam.mCalibrationStyle = CalibrationStyle.CIRCLE
            }
        }

    }

    fun setCalibrationDirect(direct: Int) {
        when (direct) {
            CalibrationStyle.HORIZONTAL.value -> {
                mParam.mCalibrationDirect = CalibrationStyle.HORIZONTAL
            }
            CalibrationStyle.VERTICAL.value -> {
                mParam.mCalibrationDirect = CalibrationStyle.VERTICAL
            }
        }

    }

    fun setCursorLoc(loc: Int) {
        when (loc) {
            CalibrationStyle.LEFT.value -> {
                mParam.mCursorLoc = CalibrationStyle.LEFT
            }
            CalibrationStyle.RIGHT.value -> {
                mParam.mCursorLoc = CalibrationStyle.RIGHT
            }
            CalibrationStyle.INSIDE.value -> {
                mParam.mCursorLoc = CalibrationStyle.INSIDE
            }
            CalibrationStyle.OUTSIDE.value -> {
                mParam.mCursorLoc = CalibrationStyle.OUTSIDE
            }
        }

    }
}
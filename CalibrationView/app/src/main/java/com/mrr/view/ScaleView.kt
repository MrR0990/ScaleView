package com.mrr.view

import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
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
class ScaleView : View {
    val TAG = "ScaleView"

    var mContext: Context? = null
    var mParam = ScaleParam();

    var mProgressListener: ScaleParam.ProgressListener? = null

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

    // 屏幕最中心的位置
    private var mCenterX = 0f
    private var mCenterY = 0f

    //第一个节点是一个刻度节点
    var nodeLength = mInterval * mParam.mScaleNodeWidth

    //普通刻度占组件减去padding之后的宽度/高度
    var linelength = mInterval * mParam.mScaleWidth

    var mCursorBitmap: Bitmap? = null

    val mCursorMatrix = Matrix()
    val mCursorRectF = CursorRectF()

    //单位转换
    val Float.dp: Float
        get() = (this / Resources.getSystem().displayMetrics.density)
    val Float.px: Float
        get() = (this * Resources.getSystem().displayMetrics.density)

    val Float.sp2px: Float
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            this.toFloat(),
            resources.displayMetrics
        )

    constructor(context: Context?) : this(context, null)

    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        this.mContext = context
        var typeArray = context?.obtainStyledAttributes(attrs, R.styleable.ScaleView)

        mParam.mScaleWidth =
            typeArray!!.getFloat(R.styleable.ScaleView_scaleWidth, 0.5f)


        mParam.mScaleNodeWidth =
            typeArray!!.getFloat(R.styleable.ScaleView_scaleNodeWidth, 0.7f)

        mParam.mScaleThick =
            typeArray!!.getDimension(R.styleable.ScaleView_scaleThick, 5f)

        var cursorDrawableID = typeArray!!.getResourceId(R.styleable.ScaleView_cursorDrawable, 0)

        if (cursorDrawableID > 0) {
            mCursorBitmap = BitmapFactory.decodeResource(resources, cursorDrawableID)
        }

        var style = typeArray!!.getInt(R.styleable.ScaleView_scaleStyle, -1)

        if (style > 0) {
            setCalibrationStyle(style)
        }

        var direct =
            typeArray!!.getInt(R.styleable.ScaleView_scaleDirect, -1)
        if (direct > 0) {
            setCalibrationDirect(direct)
        }

        var cursorLoc =
            typeArray!!.getInt(R.styleable.ScaleView_cursorLoc, -1)
        if (cursorLoc > 0) {
            setCursorLoc(cursorLoc)
        }

        mParam.mCursorWidth =
            typeArray!!.getDimension(R.styleable.ScaleView_cursorWidth, 20f)

        mParam.mCursorGap =
            typeArray!!.getDimension(R.styleable.ScaleView_cursorGap, 5f)

        mParam.mTotalProgress =
            typeArray!!.getInt(R.styleable.ScaleView_totalProgress, 60)

        mParam.mUnitScale =
            typeArray!!.getInt(R.styleable.ScaleView_unitScale, 10)

        mParam.mDefaultColor =
            typeArray!!.getColor(R.styleable.ScaleView_defaultColor, Color.DKGRAY)

        mParam.mProgressColor =
            typeArray!!.getColor(R.styleable.ScaleView_progressColor, Color.DKGRAY)

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

        if (null != mCursorBitmap) {
            mCursorRectF.mScaleX = mParam.mCursorWidth.px / mCursorBitmap!!.width
            mCursorRectF.mScaleY = mParam.mCursorWidth.px / mCursorBitmap!!.height
        }

        initPaint()

        if (mParam.mScaleStyle == ScaleStyle.LINE) {

            //刻度线本身占用的空间
            var calibrationSpace = mParam.mScaleThick * (mParam.mTotalProgress + 1)

            if (mParam.mScaleDirect == ScaleStyle.VERTICAL) {

                //总共的绘制空间
                var drawSpace = mHeight - mPaddingTop - mPaddingBottom
                //刻度之间的缝隙大小
                mPerInterval = (drawSpace - calibrationSpace) / mParam.mTotalProgress
                //每个一个刻度最长可绘制的空间
                mInterval = mWidth - paddingLeft - paddingRight
                nodeLength = mInterval * mParam.mScaleNodeWidth
                linelength = mInterval * mParam.mScaleWidth

            } else if (mParam.mScaleDirect == ScaleStyle.HORIZONTAL) {

                var drawSpace = mWidth - mPaddingLeft - paddingRight
                mPerInterval = (drawSpace - calibrationSpace) / mParam.mTotalProgress
                mInterval = mHeight - paddingTop - paddingBottom
                nodeLength = mInterval * mParam.mScaleNodeWidth
                linelength = mInterval * mParam.mScaleWidth
            }

        } else if (mParam.mScaleStyle == ScaleStyle.CIRCLE) {
            mPreAngle = (Math.PI * 2 / mParam.mTotalProgress).toFloat()
            mCircleRadius =
                Math.min(
                    mWidth - paddingLeft - paddingRight,
                    mHeight - paddingTop - paddingBottom
                ) / 2

            mCenterX = mWidth / 2
            mCenterY = mHeight / 2
        }


        mHalfCalibration = mParam.mScaleThick / 2

        mTouchY = (mPaddingTop + mPerInterval).toFloat()

        mProgressListener = mParam.mProgressListener
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

//        var rect = RectF()
//        rect.left = mPaddingLeft.toFloat()
//        rect.top = mPaddingTop.toFloat()
//        rect.right = (mWidth - mPaddingRight).toFloat()
//        rect.bottom = (mHeight - mPaddingBottom).toFloat()
//
//        canvas?.drawRoundRect(rect, 0f, 0f, mCursorPaint)
//        drawCalibration(canvas, mChangeColorPaint, 0, mTouchY.toInt())

        if (mParam.mScaleStyle == ScaleStyle.LINE) {
            drawLineCalibration(canvas, mOriginColorPaint, 0f, mHeight)
        } else if (mParam.mScaleStyle == ScaleStyle.CIRCLE) {
            drawCircleCalibration(canvas, mOriginColorPaint, 0f, mHeight)
        }

        drawCursor(canvas)

    }

    private fun drawCircleCalibration(canvas: Canvas?, paint: Paint, from: Float, to: Float) {

        canvas?.drawCircle(mWidth / 2, mHeight / 2, mCircleRadius, mCursorPaint)

        //节点刻度和普通刻度的长度差
        var lengthDiff =
            ((mCircleRadius * mParam.mScaleNodeWidth) - (mCircleRadius * mParam.mScaleWidth)) / 2

        var startX = 0f
        var stopX = 0f
        var startY = 0f
        var stopY = 0f

        for (index in 0..mParam.mTotalProgress - 1) {
            // 初始角度 + 当前旋转的角度
            val angle = (index * mPreAngle).toDouble()

            //从外往内绘制
            if (index % mParam.mUnitScale == 0) {
                startX = (mCenterX + mCircleRadius * Math.cos(angle)).toFloat()
                startY = (mCenterY + mCircleRadius * Math.sin(angle)).toFloat()
                stopX = (mCenterX + (mCircleRadius - mCircleRadius * mParam.mScaleNodeWidth)
                        * Math.cos(angle)).toFloat()
                stopY = (mCenterY + (mCircleRadius - mCircleRadius * mParam.mScaleNodeWidth)
                        * Math.sin(angle)).toFloat()
            } else {
                startX = (mCenterX + (mCircleRadius - lengthDiff)
                        * Math.cos(angle)).toFloat()
                startY = (mCenterY + (mCircleRadius - lengthDiff) * Math.sin(angle)).toFloat()
                stopX =
                    (mCenterX + (mCircleRadius - mCircleRadius * mParam.mScaleWidth - lengthDiff)
                            * Math.cos(angle)).toFloat()
                stopY =
                    (mCenterY + (mCircleRadius - mCircleRadius * mParam.mScaleWidth - lengthDiff)
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


        var nodeStartX = 0f
        var nodeStopX = 0f

        var nodeStartY = 0f
        var nodeStopY = 0f

        var startX = 0f
        var stopX = 0f

        var startY = 0f
        var stopY = 0f


        if (mParam.mScaleDirect == ScaleStyle.VERTICAL) {

            nodeStartX = paddingLeft + (mInterval - nodeLength) / 2
            nodeStopX = nodeStartX + nodeLength

            startX = paddingLeft + (mInterval - linelength) / 2
            stopX = startX + linelength

            startY = mPaddingTop + mHalfCalibration
            stopY = startY

            for (index in 0..mParam.mTotalProgress) {
                if (stopY > to) {
                    break
                }

                canvas?.drawLine(
                    if (index % mParam.mUnitScale == 0) nodeStartX.toFloat() else startX,
                    startY.toFloat(),
                    if (index % mParam.mUnitScale == 0) nodeStopX.toFloat() else stopX,
                    stopY.toFloat(),
                    paint
                )

                startY += (mPerInterval + mParam.mScaleThick)
                stopY += (mPerInterval + mParam.mScaleThick)

                Log.d(TAG, "index : $index ")
            }

        } else if (mParam.mScaleDirect == ScaleStyle.HORIZONTAL) {

            nodeStartX = paddingLeft + mHalfCalibration
            nodeStopX = nodeStartX

            nodeStartY = paddingTop + (mInterval - nodeLength) / 2
            nodeStopY = nodeStartY + nodeLength

            startY = paddingTop + (mInterval - linelength) / 2
            stopY = startY + linelength

            for (index in 0..mParam.mTotalProgress) {

                canvas?.drawLine(
                    nodeStartX,
                    if (index % mParam.mUnitScale == 0) nodeStartY.toFloat() else startY,
                    nodeStopX,
                    if (index % mParam.mUnitScale == 0) nodeStopY.toFloat() else stopY,
                    paint
                )

                nodeStartX += (mPerInterval + mParam.mScaleThick)
                nodeStopX += (mPerInterval + mParam.mScaleThick)

            }
        }

        canvas.restore()

    }


    /**
     * 绘制用户自定义的图片游标
     *
     */
    private fun drawCursor(canvas: Canvas?) {

        if (null == mCursorBitmap) {
            return
        }

        when (mParam.mCursorLoc) {
            ScaleStyle.LEFT -> {
                if (mParam.mScaleStyle != ScaleStyle.LINE) {
                    return
                }
                mCursorRectF.mTransX =
                    paddingLeft + (mInterval - linelength) / 2 - mParam.mCursorGap.px - mParam.mCursorWidth.px
                mCursorRectF.mTransY = 30f;

            }
            ScaleStyle.RIGHT -> {
                if (mParam.mScaleStyle != ScaleStyle.LINE) {
                    return
                }
                mCursorRectF.mTransX =
                    paddingLeft + (mInterval - linelength) / 2 + linelength + mParam.mCursorGap.px
                mCursorRectF.mTransY = 80f;


            }
            ScaleStyle.TOP -> {
                if (mParam.mScaleStyle != ScaleStyle.LINE) {
                    return
                }

                mCursorRectF.mTransX = 30f

                mCursorRectF.mTransY =
                    paddingTop + (mInterval - linelength) / 2 - mParam.mCursorGap.px - mParam.mCursorWidth.px
            }

            ScaleStyle.BOTTOM -> {
                if (mParam.mScaleStyle != ScaleStyle.LINE) {
                    return
                }

                mCursorRectF.mTransX = 80f

                mCursorRectF.mTransY =
                    paddingTop + (mInterval - linelength) / 2 + linelength + mParam.mCursorGap.px

            }
            ScaleStyle.INSIDE -> {
                if (mParam.mScaleStyle != ScaleStyle.CIRCLE) {
                    return
                }
            }
            ScaleStyle.OUTSIDE -> {
                if (mParam.mScaleStyle != ScaleStyle.CIRCLE) {
                    return
                }
            }
        }


        mCursorMatrix.reset()
        // 创建操作图片用的 Matrix 对象
        mCursorMatrix.postScale(
            mCursorRectF.mScaleX, mCursorRectF.mScaleY

        );
        mCursorMatrix.postTranslate(
            mCursorRectF.mTransX,
            mCursorRectF.mTransY
        );
        canvas?.drawBitmap(mCursorBitmap, mCursorMatrix, null)
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
        mOriginColorPaint.strokeWidth = mParam.mScaleThick

        mChangeColorPaint.isAntiAlias = true
        mChangeColorPaint.style = Paint.Style.STROKE
        mChangeColorPaint.color = mParam.mProgressColor
        mChangeColorPaint.strokeWidth = mParam.mScaleThick


        mCursorPaint.isAntiAlias = true
        mCursorPaint.style = Paint.Style.STROKE
        mCursorPaint.strokeWidth = 1f
    }

    fun setCalibrationStyle(style: Int) {
        when (style) {
            ScaleStyle.LINE.value -> {
                mParam.mScaleStyle = ScaleStyle.LINE
            }
            ScaleStyle.CIRCLE.value -> {
                mParam.mScaleStyle = ScaleStyle.CIRCLE
            }
        }

    }

    fun setCalibrationDirect(direct: Int) {
        when (direct) {
            ScaleStyle.HORIZONTAL.value -> {
                mParam.mScaleDirect = ScaleStyle.HORIZONTAL
            }
            ScaleStyle.VERTICAL.value -> {
                mParam.mScaleDirect = ScaleStyle.VERTICAL
            }
        }

    }

    fun setCursorLoc(loc: Int) {
        when (loc) {
            ScaleStyle.LEFT.value -> {
                mParam.mCursorLoc = ScaleStyle.LEFT
            }
            ScaleStyle.RIGHT.value -> {
                mParam.mCursorLoc = ScaleStyle.RIGHT
            }
            ScaleStyle.TOP.value -> {
                mParam.mCursorLoc = ScaleStyle.TOP
            }
            ScaleStyle.BOTTOM.value -> {
                mParam.mCursorLoc = ScaleStyle.BOTTOM
            }
            ScaleStyle.INSIDE.value -> {
                mParam.mCursorLoc = ScaleStyle.INSIDE
            }
            ScaleStyle.OUTSIDE.value -> {
                mParam.mCursorLoc = ScaleStyle.OUTSIDE
            }
        }

    }

    private fun sp2px(sp: Int): Float {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,
            sp.toFloat(),
            resources.displayMetrics
        )
    }


}
package com.mrr.scaleview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.mrr.scaleview.rectf.CursorRectF
import com.mrr.scaleview.enum.ScaleAttrEnum
import com.mrr.scaleview.util.UnitConversion.Companion.px
import com.mrr.scaleview.attr.ScaleViewAttr

/**
 * 1. 画线性刻度
 * - 解决初始状态游标位置问题
 * - 设置一个背景颜色,看看是不是居中
 * - 绘制刻度节点长度不同效果
 *
 * 2. 画线性游标
 * - 游标按照节点最长的位置绘制
 */
class ScaleView : View, CursorRectF.ProgressChangeListener {
    val TAG = "ScaleView"

    var mContext: Context? = null

    /**
     * 当前进度
     */
    var mCurProgress = 0

    /**
     * 各种属性
     */
    var mParam = ScaleViewAttr();

    var mProgressListener: ScaleViewAttr.ProgressListener? = null

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
    var mPreDegrees = 0f

    /**
     * 圆形刻度的半径
     */
    var mCircleRadius = 0f

    /**
     * 每一个刻度最长可绘制的空间
     */
    private var mInterval = 0f

    private var mDrawSpace = 0f

    /**
     * 当前触摸到的位置
     */
    private var mTouchX = 0f;
    private var mTouchY = 0f;

    /**
     *画笔
     */
    private var mOriginColorPaint = Paint()
    private var mChangeColorPaint = Paint()
    private var mCursorPaint = Paint()

    /**
     * 是否已经初始化画笔
     */
    private var isInit = false

    // 屏幕最中心的位置
    private var mCenterX = 0f
    private var mCenterY = 0f

    //第一个节点是一个刻度节点/节点刻度的宽度/高度
    private var nodeLength = 0f

    //普通刻度占组件减去padding之后的宽度/高度
    private var linelength = 0f

    private var mCursorBitmap: Bitmap? = null

    private val mCursorMatrix = Matrix()
    private val mCursorRectF = CursorRectF(this)

    /**
     * 线性刻度时候切割canvas使用
     */
    private val mClipRect = RectF()

    private var mCircleProgressAngel = 0.0

    private var mClipProgress = 0f

    private var nodeStartX = 0f
    private var nodeStopX = 0f

    private var nodeStartY = 0f
    private var nodeStopY = 0f

    private var startX = 0f
    private var stopX = 0f

    private var startY = 0f
    private var stopY = 0f

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

        mParam.mScaleLineWidth =
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


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)


        if (mParam.mScaleStyle == ScaleAttrEnum.LINE) {

            if (mParam.mScaleDirect == ScaleAttrEnum.VERTICAL) {

                mClipProgress = mDrawSpace * (mTouchY - paddingTop) / mDrawSpace

                mClipRect.set(0f, 0f, mWidth, mTouchY)
                drawLineScale(canvas, mChangeColorPaint, mClipRect)

                mClipRect.set(0f, mTouchY, mWidth, mHeight)

            } else if (mParam.mScaleDirect == ScaleAttrEnum.HORIZONTAL) {

                mClipProgress = mDrawSpace * (mTouchX - paddingLeft) / mDrawSpace

                mClipRect.set(0f, 0f, mTouchX, mHeight)
                drawLineScale(canvas, mChangeColorPaint, mClipRect)

                mClipRect.set(mTouchX, 0f, mWidth, mHeight)
            }

            drawLineScale(canvas, mOriginColorPaint, mClipRect)
            drawCursor(canvas)

        } else if (mParam.mScaleStyle == ScaleAttrEnum.CIRCLE) {
            drawCircleScale(canvas, mOriginColorPaint, Math.PI * 2)
            drawCursor(canvas)
            drawCircleScale(
                canvas,
                mChangeColorPaint,
                mCircleProgressAngel
            )
        }
    }


    private fun drawCircleScale(canvas: Canvas?, paint: Paint, maxAngel: Double) {

        if (maxAngel == 0.0) {
            return
        }

        canvas?.drawCircle(mWidth / 2, mHeight / 2, mCircleRadius, mCursorPaint)

        //节点刻度和普通刻度的长度差
        var lengthDiff =
            ((mCircleRadius * mParam.mScaleNodeWidth) - (mCircleRadius * mParam.mScaleWidth)) / 2


        for (index in 0..mParam.mTotalProgress - 1) {
            // 初始角度 + 当前旋转的角度
            val angle = (index * mPreDegrees).toDouble()

            if (angle > maxAngel) {
                return
            }

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
                paint
            )

        }
    }


    /**
     * 画线性的刻度
     */
    private fun drawLineScale(canvas: Canvas?, paint: Paint, clipRectF: RectF) {


        canvas!!.save()
        canvas.clipRect(clipRectF)


        if (mParam.mScaleDirect == ScaleAttrEnum.VERTICAL) {

            nodeStartX = paddingLeft + (mInterval - nodeLength) / 2
            nodeStopX = nodeStartX + nodeLength

            startX = paddingLeft + (mInterval - linelength) / 2
            stopX = startX + linelength

            startY = mPaddingTop + mHalfCalibration
            stopY = startY

            for (index in 0..mParam.mTotalProgress) {

                canvas?.drawLine(
                    if (index % mParam.mUnitScale == 0) nodeStartX.toFloat() else startX,
                    startY.toFloat(),
                    if (index % mParam.mUnitScale == 0) nodeStopX.toFloat() else stopX,
                    stopY.toFloat(),
                    paint
                )

                startY += (mPerInterval + mParam.mScaleLineWidth)
                stopY += (mPerInterval + mParam.mScaleLineWidth)

            }

        } else if (mParam.mScaleDirect == ScaleAttrEnum.HORIZONTAL) {

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

                nodeStartX += (mPerInterval + mParam.mScaleLineWidth)
                nodeStopX += (mPerInterval + mParam.mScaleLineWidth)

            }
        }

        canvas.restore()

    }


    /**
     * 绘制游标
     *
     */
    private fun drawCursor(canvas: Canvas?) {

        if (null == mCursorBitmap) {
            return
        }

        when (mParam.mCursorLoc) {
            ScaleAttrEnum.LEFT -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }
                mCursorRectF.mTransX =
                    paddingLeft + (mInterval - linelength) / 2 - mParam.mCursorGap.px - mParam.mCursorWidth.px


                mCursorRectF.mTransY = mTouchY - mParam.mCursorWidth.px / 2

            }
            ScaleAttrEnum.RIGHT -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }
                mCursorRectF.mTransX =
                    paddingLeft + (mInterval - linelength) / 2 + linelength + mParam.mCursorGap.px

                mCursorRectF.mTransY = mTouchY - mParam.mCursorWidth.px / 2

            }
            ScaleAttrEnum.TOP -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }

                mCursorRectF.mTransX = mTouchX - mParam.mCursorWidth.px / 2

                mCursorRectF.mTransY =
                    paddingTop + (mInterval - linelength) / 2 - mParam.mCursorGap.px - mParam.mCursorWidth.px
            }
            ScaleAttrEnum.BOTTOM -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }

                mCursorRectF.mTransX = mTouchX - mParam.mCursorWidth.px / 2

                mCursorRectF.mTransY =
                    paddingTop + (mInterval - linelength) / 2 + linelength + mParam.mCursorGap.px

            }
            ScaleAttrEnum.INSIDE, ScaleAttrEnum.OUTSIDE -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.CIRCLE) {
                    return
                }

                mCursorRectF.calculateAttributes(
                    mTouchX,
                    mTouchY,
                    mCenterX,
                    mCenterY,
                    mCircleRadius,
                    mParam
                )


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

        if (mParam.mScaleStyle == ScaleAttrEnum.CIRCLE) {
            mCursorMatrix.postRotate(
                mCursorRectF.mRoutateDegress,
                mCursorRectF.mRoutateCenterX,
                mCursorRectF.mRoutateCenterY
            )
        }

        canvas?.drawBitmap(mCursorBitmap, mCursorMatrix, null)

    }


    override fun onTouchEvent(event: MotionEvent?): Boolean {

        when (event?.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE, MotionEvent.ACTION_UP -> {

                mTouchX = event.x
                mTouchY = event.y

                if (mTouchX < paddingLeft) {
                    mTouchX = paddingLeft.toFloat()
                }

                if (mTouchX > mWidth - paddingRight) {
                    mTouchX = mWidth - paddingRight
                }

                if (mTouchY < paddingTop) {
                    mTouchY = paddingTop.toFloat()
                }

                if (mTouchY > mHeight - paddingBottom) {
                    mTouchY = mHeight - paddingBottom
                }

            }
        }

        invalidate()

        mProgressListener?.progressChanged(mTouchY)
        return true
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

        if (mParam.mScaleStyle == ScaleAttrEnum.LINE) {

            //刻度线本身占用的空间
            var calibrationSpace = mParam.mScaleLineWidth * (mParam.mTotalProgress + 1)

            if (mParam.mScaleDirect == ScaleAttrEnum.VERTICAL) {

                //总共的绘制空间
                mDrawSpace = mHeight - mPaddingTop - mPaddingBottom
                //刻度之间的缝隙大小
                mPerInterval = (mDrawSpace - calibrationSpace) / mParam.mTotalProgress
                //每个一个刻度最长可绘制的空间
                mInterval = mWidth - paddingLeft - paddingRight
                nodeLength = mInterval * mParam.mScaleNodeWidth
                linelength = mInterval * mParam.mScaleWidth
                mTouchY = paddingTop.toFloat()

            } else if (mParam.mScaleDirect == ScaleAttrEnum.HORIZONTAL) {

                mDrawSpace = mWidth - mPaddingLeft - paddingRight
                mPerInterval = (mDrawSpace - calibrationSpace) / mParam.mTotalProgress
                mInterval = mHeight - paddingTop - paddingBottom
                nodeLength = mInterval * mParam.mScaleNodeWidth
                linelength = mInterval * mParam.mScaleWidth
                mTouchX = paddingLeft.toFloat()
            }

        } else if (mParam.mScaleStyle == ScaleAttrEnum.CIRCLE) {
            mPreDegrees = (Math.PI * 2 / mParam.mTotalProgress).toFloat()
            mCircleRadius =
                Math.min(
                    mWidth - paddingLeft - paddingRight,
                    mHeight - paddingTop - paddingBottom
                ) / 2

            mCenterX = mWidth / 2
            mCenterY = mHeight / 2
            mTouchX = mCenterX + mCircleRadius
            mTouchY = mCenterY
        }

        mHalfCalibration = mParam.mScaleLineWidth / 2
        mProgressListener = mParam.mProgressListener
    }


    private fun initPaint() {
        mOriginColorPaint.isAntiAlias = true
        mOriginColorPaint.style = Paint.Style.STROKE
        mOriginColorPaint.color = mParam.mDefaultColor
        mOriginColorPaint.strokeWidth = mParam.mScaleLineWidth

        mChangeColorPaint.isAntiAlias = true
        mChangeColorPaint.style = Paint.Style.STROKE
        mChangeColorPaint.color = mParam.mProgressColor
        mChangeColorPaint.strokeWidth = mParam.mScaleLineWidth


        mCursorPaint.isAntiAlias = true
        mCursorPaint.style = Paint.Style.STROKE
        mCursorPaint.strokeWidth = 1f
    }

    fun setCalibrationStyle(style: Int) {
        when (style) {
            ScaleAttrEnum.LINE.value -> {
                mParam.mScaleStyle = ScaleAttrEnum.LINE
            }
            ScaleAttrEnum.CIRCLE.value -> {
                mParam.mScaleStyle = ScaleAttrEnum.CIRCLE
            }
        }

    }

    fun setCalibrationDirect(direct: Int) {
        when (direct) {
            ScaleAttrEnum.HORIZONTAL.value -> {
                mParam.mScaleDirect = ScaleAttrEnum.HORIZONTAL
            }
            ScaleAttrEnum.VERTICAL.value -> {
                mParam.mScaleDirect = ScaleAttrEnum.VERTICAL
            }
        }

    }

    fun setCursorLoc(loc: Int) {
        when (loc) {
            ScaleAttrEnum.LEFT.value -> {
                mParam.mCursorLoc = ScaleAttrEnum.LEFT
            }
            ScaleAttrEnum.RIGHT.value -> {
                mParam.mCursorLoc = ScaleAttrEnum.RIGHT
            }
            ScaleAttrEnum.TOP.value -> {
                mParam.mCursorLoc = ScaleAttrEnum.TOP
            }
            ScaleAttrEnum.BOTTOM.value -> {
                mParam.mCursorLoc = ScaleAttrEnum.BOTTOM
            }
            ScaleAttrEnum.INSIDE.value -> {
                mParam.mCursorLoc = ScaleAttrEnum.INSIDE
            }
            ScaleAttrEnum.OUTSIDE.value -> {
                mParam.mCursorLoc = ScaleAttrEnum.OUTSIDE
            }
        }

    }


    override fun progressChange(curAngel: Double) {
        mCircleProgressAngel = curAngel
    }


}
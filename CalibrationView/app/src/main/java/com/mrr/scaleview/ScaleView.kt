package com.mrr.scaleview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mrr.scaleview.enum.ScaleAttrEnum
import com.mrr.scaleview.attr.ScaleViewAttr
import com.mrr.scaleview.view.BaseView
import com.mrr.scaleview.view.CircleScaleView
import com.mrr.scaleview.view.HorizontalScaleView
import com.mrr.scaleview.view.VerticalScaleView
import java.util.function.Consumer

class ScaleView : View {
    val TAG = "ScaleView"

    var mContext: Context? = null

    var mParam = ScaleViewAttr();

    lateinit var mView: BaseView

    /**
     * 是否已经初始化画笔
     */
    private var isInit = false

    var mWidth = 0f
    var mHeight = 0f

    var mTouchX = 0f;
    var mTouchY = 0f;


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
            typeArray!!.getDimension(R.styleable.ScaleView_scaleLineWidth, 5f)

        var cursorDrawableID = typeArray!!.getResourceId(R.styleable.ScaleView_cursorDrawable, 0)

        if (cursorDrawableID > 0) {
            mParam.mCursorBitmap = BitmapFactory.decodeResource(resources, cursorDrawableID)
        }

        var style = typeArray!!.getInt(R.styleable.ScaleView_scaleStyle, -1)
        mParam.mScaleStyle =
            ScaleAttrEnum.get(style)

        var direct =
            typeArray!!.getInt(R.styleable.ScaleView_scaleDirect, -1)
        mParam.mScaleDirect = ScaleAttrEnum.get(direct)


        var cursorSeat =
            typeArray!!.getInt(R.styleable.ScaleView_cursorSeat, -1)
        mParam.mCursorSeat = ScaleAttrEnum.get(cursorSeat)

        var scaleTextSeat =
            typeArray!!.getInt(R.styleable.ScaleView_scaleTextSeat, -1)
        mParam.mScaleTextSeat = ScaleAttrEnum.get(scaleTextSeat)

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

        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        mParam.mWidth = mWidth
        mParam.mHeight = mHeight

        setMeasuredDimension(mWidth.toInt(), mHeight.toInt())

        mParam.mPaddingLeft = mPaddingLeft
        mParam.mPaddingRight = mPaddingRight
        mParam.mPaddingTop = mPaddingTop
        mParam.mPaddingBottom = mPaddingBottom


        when {
            (mParam.mScaleStyle == ScaleAttrEnum.LINE && mParam.mScaleDirect == ScaleAttrEnum.VERTICAL) -> {
                mView = VerticalScaleView(mParam)
            }
            (mParam.mScaleStyle == ScaleAttrEnum.LINE && mParam.mScaleDirect == ScaleAttrEnum.HORIZONTAL) -> {
                mView = HorizontalScaleView(mParam)
            }
            (mParam.mScaleStyle == ScaleAttrEnum.CIRCLE) -> {
                mView = CircleScaleView(mParam)
            }
        }

        mView.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mView.initTouchXY(
            Consumer { t -> mTouchX = t },
            Consumer { t -> mTouchY = t }
        )
        isInit = true

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mView.onDraw(canvas, mTouchX, mTouchY)
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

        return true
    }


}
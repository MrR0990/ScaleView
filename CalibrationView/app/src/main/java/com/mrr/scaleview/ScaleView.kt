package com.mrr.scaleview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.mrr.scaleview.enum.ScaleAttrEnum
import com.mrr.scaleview.attr.ScaleViewAttr
import com.mrr.scaleview.view.BaseScaleView
import com.mrr.scaleview.view.CircleScaleView
import com.mrr.scaleview.view.HorizontalScaleView
import com.mrr.scaleview.view.VerticalScaleView
import java.util.function.Consumer

class ScaleView : View {
    val TAG = "ScaleView"

    var mContext: Context? = null

    var mAttr = ScaleViewAttr();

    lateinit var mScaleView: BaseScaleView

    var mProgressChangeListener: ProgressChangeListener? = null
        set(value) {

            if (this::mScaleView.isInitialized) {
                mScaleView?.mProgressChangeListener = value
            }

        }

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
        mAttr.initAttr(typeArray, mContext)

        when {
            (mAttr.mScaleStyle == ScaleAttrEnum.LINE && mAttr.mScaleDirect == ScaleAttrEnum.VERTICAL) -> {
                mScaleView = VerticalScaleView(mAttr)
            }
            (mAttr.mScaleStyle == ScaleAttrEnum.LINE && mAttr.mScaleDirect == ScaleAttrEnum.HORIZONTAL) -> {
                mScaleView = HorizontalScaleView(mAttr)
            }
            (mAttr.mScaleStyle == ScaleAttrEnum.CIRCLE) -> {
                mScaleView = CircleScaleView(mAttr)
            }
        }
        
        typeArray?.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        if (isInit) {
            return
        }

        mWidth = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        mHeight = MeasureSpec.getSize(heightMeasureSpec).toFloat()

        mAttr.mWidth = mWidth
        mAttr.mHeight = mHeight

        setMeasuredDimension(mWidth.toInt(), mHeight.toInt())

        mAttr.mPaddingLeft = mPaddingLeft
        mAttr.mPaddingRight = mPaddingRight
        mAttr.mPaddingTop = mPaddingTop
        mAttr.mPaddingBottom = mPaddingBottom



        mScaleView.onMeasure(widthMeasureSpec, heightMeasureSpec)
        mScaleView.initTouchXY(
            Consumer { t -> mTouchX = t },
            Consumer { t -> mTouchY = t }
        )
        isInit = true

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        mScaleView.onDraw(canvas, mTouchX, mTouchY)
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

    interface ProgressChangeListener {

        fun progressChange(progress: Float)
    }
}
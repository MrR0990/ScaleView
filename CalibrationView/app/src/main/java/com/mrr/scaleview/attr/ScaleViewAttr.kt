package com.mrr.scaleview.attr

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import com.mrr.scaleview.enum.ScaleAttrEnum

class ScaleViewAttr {

    var context: Context? = null

    //基本属性
    var mWidth = 0f
    var mHeight = 0f
    var mPaddingLeft = 0
    var mPaddingRight = 0
    var mPaddingTop = 0
    var mPaddingBottom = 0

    /**
     * 普通刻度占组件减去padding之后的宽度/高度的比例
     */
    var mScaleWidth = 0.5f

    /**
     * 刻度节点占组件减去padding之后的宽度/高度的比例
     */
    var mScaleNodeWidth = 0.7f

    /**
     * 刻度线线条宽度
     */
    var mScaleLineWidth = 5f


    /**
     * 刻度样式,默认为线性样式
     */
    var mScaleStyle = ScaleAttrEnum.LINE

    /**
     * 线性刻度的方向
     * HORIZONTAL
     * VERTICAL
     */
    var mScaleDirect = ScaleAttrEnum.HORIZONTAL

    /**
     * 游标位置
     *
     * LEFT : 线性刻度的左侧
     * RIGHT:线性刻度的右侧
     * INSIDE:圆形刻度的内侧
     * OUTSIDE:圆形刻度的外侧
     *
     */
    var mCursorSeat = ScaleAttrEnum.NONE

    var mScaleTextSeat = ScaleAttrEnum.NONE

    /**
     * 游标的宽度
     *
     * 配置的时候需要注意游标和组件宽度之间的关系,留足够的空间
     */
    var mCursorWidth = 20f

    /**
     * 游标和刻度之间的间隙
     */
    var mCursorGap = 0f

    /**
     * 总刻度
     */
    var mTotalProgress = 50

    /**
     * 单位刻度,主要是用来控制节点刻度
     */
    var mUnitScale = 10

    /**
     * 刻度默认颜色
     */
    var mDefaultColor = Color.parseColor("#999999");

    /**
     * 已走过刻度颜色
     */
    var mProgressColor = Color.parseColor("#999999");

    var mProgressListener: ProgressListener? = null

    var mScaleMarkSize = 0f

    var mCursorBitmap: Bitmap? = null


    fun apply(viewAttr: ScaleViewAttr) {
        viewAttr.context = this.context
        viewAttr.mScaleWidth = this.mScaleWidth
        viewAttr.mScaleLineWidth = this.mScaleLineWidth
        viewAttr.mScaleNodeWidth = this.mScaleNodeWidth
        viewAttr.mScaleStyle = this.mScaleStyle
        viewAttr.mScaleDirect = this.mScaleDirect
        viewAttr.mCursorSeat = this.mCursorSeat
        viewAttr.mCursorWidth = this.mCursorWidth
        viewAttr.mCursorGap = this.mCursorGap
        viewAttr.mTotalProgress = this.mTotalProgress
        viewAttr.mUnitScale = this.mUnitScale
        viewAttr.mDefaultColor = this.mDefaultColor
        viewAttr.mProgressColor = this.mProgressColor
        viewAttr.mScaleMarkSize = this.mScaleMarkSize
        viewAttr.mCursorBitmap = this.mCursorBitmap
        viewAttr.mProgressListener = this.mProgressListener
    }


    interface ProgressListener {
        fun progressChanged(progress: Float)
    }
}
package com.mrr.view

import android.content.Context
import android.graphics.Color

class ScaleParam {

    var context: Context? = null

    /**
     * 普通刻度占组件减去padding之后的宽度/高度的比例
     */
    var mScaleWidth = 0.5f

    /**
     * 刻度节点占组件减去padding之后的宽度/高度的比例
     */
    var mScaleNodeWidth = 0.7f

    /**
     * 刻度线的粗细
     */
    var mScaleThick = 5f


    /**
     * 刻度样式,默认为线性样式
     */
    var mScaleStyle = ScaleStyle.LINE

    /**
     * 线性刻度的方向
     * HORIZONTAL
     * VERTICAL
     */
    var mScaleDirect = ScaleStyle.HORIZONTAL

    /**
     * 游标位置
     *
     * LEFT : 线性刻度的左侧
     * RIGHT:线性刻度的右侧
     * INSIDE:圆形刻度的内侧
     * OUTSIDE:圆形刻度的外侧
     *
     */
    var mCursorLoc = ScaleStyle.LEFT

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


    fun apply(param: ScaleParam) {
        param.context = this.context
        param.mScaleWidth = this.mScaleWidth
        param.mScaleThick = this.mScaleThick
        param.mScaleNodeWidth = this.mScaleNodeWidth
        param.mScaleStyle = this.mScaleStyle
        param.mScaleDirect = this.mScaleDirect
        param.mCursorLoc = this.mCursorLoc
        param.mCursorWidth = this.mCursorWidth
        param.mCursorGap = this.mCursorGap
        param.mTotalProgress = this.mTotalProgress
        param.mUnitScale = this.mUnitScale
        param.mDefaultColor = this.mDefaultColor
        param.mProgressColor = this.mProgressColor
        param.mScaleMarkSize = this.mScaleMarkSize
        param.mProgressListener = this.mProgressListener
    }


    interface ProgressListener {
        fun progressChanged(progress: Float)
    }
}
package com.mrr.view

import android.content.Context
import android.graphics.Color

class CalibrationParam {

    var context: Context? = null

    /**
     * 普通刻度占组件减去padding之后的宽度/高度的比例
     */
    var mCalibrationWidth = 0.5f

    /**
     * 刻度节点占组件减去padding之后的宽度/高度的比例
     */
    var mCalibrationNodeWidth = 0.7f

    /**
     * 刻度线的粗细
     */
    var mCalibrationThick = 5f


    /**
     * 刻度样式,默认为线性样式
     */
    var mCalibrationStyle = CalibrationStyle.LINE

    /**
     * 线性刻度的方向
     * HORIZONTAL
     * VERTICAL
     */
    var mCalibrationDirect = CalibrationStyle.HORIZONTAL

    /**
     * 游标位置
     *
     * LEFT : 线性刻度的左侧
     * RIGHT:线性刻度的右侧
     * INSIDE:圆形刻度的内侧
     * OUTSIDE:圆形刻度的外侧
     *
     */
    var mCursorLoc = CalibrationStyle.LEFT

    /**
     * 游标的宽度
     *
     * 配置的时候需要注意游标和组件宽度之间的关系,留足够的空间
     */
    var mCursorWidth = 20f

    /**
     * 游标和刻度之间的间隙
     */
    var mCursorGap = 5f

    /**
     * 总刻度
     */
    var mTotalProgress = 50

    /**
     * 单位刻度,主要是用来控制节点刻度
     */
    var mUnitCalibration = 10

    /**
     * 刻度默认颜色
     */
    var mDefaultColor = Color.parseColor("#999999");

    /**
     * 已走过刻度颜色
     */
    var mProgressColor = Color.parseColor("#999999");

    var mProgressListener: ProgressListener? = null


    fun apply(param: CalibrationParam) {
        param.context = this.context
        param.mCalibrationWidth = this.mCalibrationWidth
        param.mCalibrationThick = this.mCalibrationThick
        param.mCalibrationNodeWidth = this.mCalibrationNodeWidth
        param.mCalibrationStyle = this.mCalibrationStyle
        param.mCalibrationDirect = this.mCalibrationDirect
        param.mCursorLoc = this.mCursorLoc
        param.mCursorWidth = this.mCursorWidth
        param.mCursorGap = this.mCursorGap
        param.mTotalProgress = this.mTotalProgress
        param.mUnitCalibration = this.mUnitCalibration
        param.mDefaultColor = this.mDefaultColor
        param.mProgressColor = this.mProgressColor
        param.mProgressListener = this.mProgressListener
    }


    interface ProgressListener {
        fun progressChanged(progress: Float)
    }
}
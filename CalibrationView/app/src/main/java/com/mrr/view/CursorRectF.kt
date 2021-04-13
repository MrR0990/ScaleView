package com.mrr.view

import android.util.Log
import com.mrr.view.UnitConversion.Companion.px

class CursorRectF {

    private val TAG = "CursorRectF"

    /**
     * 游标变换位置的各个维度
     */
    var mScaleX = 0f;
    var mScaleY = 0f;
    var mTransX = 0f;
    var mTransY = 0f;
    var mRoutateCenterX = 0f;
    var mRoutateCenterY = 0f;
    var mRoutateDegress = 0f;


    /**
     * 计算圈内游标的各个属性
     */
    fun calculateAttributesInside(
        touchX: Float,
        touchY: Float,
        centerX: Float,
        centerY: Float,
        circleRadius: Float,
        p: ScaleParam,
    ) {

        var tx = Math.abs(touchX - centerX)
        var ty = Math.abs(touchY - centerY)
        var t_length = Math.sqrt((tx * tx + ty * ty).toDouble())

        var angle = 0.0//弧度
        var degress = 0.0//角度

        val diff =
            ((circleRadius * p.mScaleNodeWidth) - (circleRadius * p.mScaleWidth)) / 2

        /**
         * 以游标中心点为弧
         */
        var cursorCircleRadius =
            circleRadius - circleRadius * p.mScaleWidth - diff - p.mCursorWidth.px / 2//游标绘制时候的虚拟半径


        //处理各个象限以及数轴
        when {
            (touchX > centerX && touchY < centerY) -> {//第一象限

                angle = Math.PI * 2 - Math.acos(tx / t_length)

            }
            (touchX < centerX && touchY < centerY) -> {//第二象限

                angle = Math.PI + Math.acos(tx / t_length)

            }
            (touchX < centerX && touchY > centerY) -> {//第三象限

                angle = Math.PI - Math.acos(tx / t_length)

            }
            (touchX > centerX && touchY > centerY) -> {//第四象限
                angle = Math.acos(tx / t_length)

            }
            (touchX > centerX && touchY == centerY) -> {//X轴正方向
                angle = Math.PI * 2
            }
            (touchX == centerX && touchY > centerY) -> {//Y轴正方向
                angle = Math.PI / 2
            }
            (touchX < centerX && touchY == centerY) -> {//X轴反方向
                angle = Math.PI
            }
            (touchX == centerX && touchY < centerY) -> {//Y轴反方向
                angle = Math.PI * 2 - Math.PI / 2
            }
        }

        mRoutateCenterX =
            (centerX + cursorCircleRadius
                    * Math.cos(angle)).toFloat()

        mRoutateCenterY =
            (centerY + cursorCircleRadius
                    * Math.sin(angle)).toFloat()

        mTransX = mRoutateCenterX - p.mCursorWidth.px / 2
        mTransY = mRoutateCenterY - p.mCursorWidth.px / 2

        mRoutateDegress = Math.toDegrees(angle - Math.PI / 2).toFloat()
    }

    /**
     * 计算圈外游标的各个属性
     */
    fun calculateAttributesOutside(
        mTouchX: Float,
        mTouchY: Float,
        mCenterX: Float,
        mCenterY: Float,
        diff: Float
    ) {
        val tx: Float = mTouchX - mCenterX
        val ty: Float = mTouchY - mCenterY

        var t_length = 0.0
        var a = 0.0

        //处理各个象限以及数轴
        when {
            (tx > 0 && ty < 0) -> {//第一象限
                t_length = Math.sqrt((tx * tx + ty * ty).toDouble())
                a = -Math.acos(tx / t_length)
            }
            (tx < 0 && ty < 0) -> {//第二象限

                t_length = Math.sqrt((tx * tx + ty * ty).toDouble())
                a = -Math.acos(tx / t_length)
            }
            (tx < 0 && ty > 0) -> {//第三象限
                t_length = Math.sqrt((tx * tx + ty * ty).toDouble())
                a = Math.acos(tx / t_length)

            }
            (tx > 0 && ty > 0) -> {//第四象限
                t_length = Math.sqrt((tx * tx + ty * ty).toDouble())
                a = Math.acos(tx / t_length)
            }
            (tx > 0 && ty == 0f) -> {//X轴正方向

                a = 0.0
            }
            (tx == 0f && ty > 0) -> {//Y轴正方向
                a = 0.0
            }
            (tx < 0 && ty == 0f) -> {//X轴反方向
                a = 0.0
            }
            (tx == 0f && ty < 0) -> {//Y轴反方向
                a = 0.0
            }
        }
    }

    /**
     * 根绝余弦求出角度
     */
    fun cosDegress(value: Double): Double {
        if (value == 0.0) {
            return 0.0
        }
        return Math.toDegrees(Math.acos(value))

    }

}
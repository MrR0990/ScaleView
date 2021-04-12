package com.mrr.view

import com.mrr.view.UnitConversion.Companion.px

class CursorRectF {
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
        mTouchX: Float,
        mTouchY: Float,
        mCenterX: Float,
        mCenterY: Float,
        mCircleRadius: Float,
        mScaleParam: ScaleParam,
    ) {
        val diff =
            ((mCircleRadius * mScaleParam.mScaleNodeWidth) - (mCircleRadius * mScaleParam.mScaleWidth)) / 2

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


        mRoutateCenterX =
            (mCenterX + (mCircleRadius - mCircleRadius * mScaleParam.mScaleWidth - diff)
                    * Math.cos(a)).toFloat()

        mRoutateCenterY =
            (mCenterY + (mCircleRadius - mCircleRadius * mScaleParam.mScaleWidth - diff)
                    * Math.sin(a)).toFloat()

        mTransX = mRoutateCenterX - mScaleParam.mCursorWidth.px / 2
        mTransY = mRoutateCenterY - mScaleParam.mCursorWidth.px / 2


        mRoutateDegress = (270).toFloat()
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
}
package com.mrr.scaleview.enum

/**
 * 刻度样式的配置
 */
enum class ScaleAttrEnum(val value: Int) {

    NONE(-1),

    /**
     * 线性刻度使用,是横向绘制还是竖向绘制
     */
    HORIZONTAL(0),
    VERTICAL(1),

    /**
     * 线性刻度
     */
    LINE(2),

    /**
     * 圆形刻度,类似手表
     */
    CIRCLE(3),

    /**
     * 游标位置
     *
     * LEFT : 线性刻度的左侧
     * RIGHT:线性刻度的右侧
     * INSIDE:圆形刻度的内侧
     * OUTSIDE:圆形刻度的外侧
     *
     */
    LEFT(4),
    RIGHT(5),
    TOP(6),
    BOTTOM(7),
    INSIDE(8),
    OUTSIDE(9),

    /**
     * 线性刻度方向
     */
    LEFT_TO_RIGHT(11),
    RIGHT_TO_LEFT(12),
    TOP_TO_BOTTOM(13),
    BOTTOM_TO_TOP(14),

    /**
     * 圆形刻度方向
     */
    CLOCK_WISE(15),
    COUNTER_CLOCKWISE(16);


    companion object {// 包裹范围内 属于静态方法

        fun get(value: Int): ScaleAttrEnum {
            when (value) {
                NONE.value -> return NONE
                HORIZONTAL.value -> return HORIZONTAL
                VERTICAL.value -> return VERTICAL
                LINE.value -> return LINE
                CIRCLE.value -> return CIRCLE
                LEFT.value -> return LEFT
                RIGHT.value -> return RIGHT
                TOP.value -> return TOP
                BOTTOM.value -> return BOTTOM
                INSIDE.value -> return INSIDE
                OUTSIDE.value -> return OUTSIDE
                LEFT_TO_RIGHT.value -> return LEFT_TO_RIGHT
                RIGHT_TO_LEFT.value -> return RIGHT_TO_LEFT
                TOP_TO_BOTTOM.value -> return TOP_TO_BOTTOM
                BOTTOM_TO_TOP.value -> return BOTTOM_TO_TOP
                CLOCK_WISE.value -> return CLOCK_WISE
                COUNTER_CLOCKWISE.value -> return COUNTER_CLOCKWISE
            }
            return NONE
        }
    }


}
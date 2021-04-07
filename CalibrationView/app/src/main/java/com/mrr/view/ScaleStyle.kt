package com.mrr.view

/**
 * 刻度样式的配置
 */
enum class ScaleStyle(val value: Int) {
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
    OUTSIDE(9)
}
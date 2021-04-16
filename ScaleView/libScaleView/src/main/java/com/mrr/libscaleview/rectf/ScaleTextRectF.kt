package com.mrr.libscaleview.rectf

import com.mrr.libscaleview.enum.ScaleAttrEnum
import com.mrr.libscaleview.util.UnitConversion.Companion.px
import com.mrr.libscaleview.view.BaseScaleView

/**
 * 绘制刻度数字
 */
class ScaleTextRectF {

    private val TAG = "ScaleTextRectF"

    var x = 0f
    var baseLine = 0f

    private var letterCenterY = 0f
    private var dy = 0f
    private var textWidth = 0f
    private var textHeight = 0f

    private var lengthDiff = 0f
    private var textCenterX = 0f
    private var textCenterY = 0f
    private var textCircleRadius = 0f

    var init = false

    fun initLineTextSeat(letter: String, attr: BaseScaleView) {
        init = false


        dy =
            (attr.fontMetrics!!.bottom - attr.fontMetrics!!.top) / 2 - attr.fontMetrics!!.bottom
        textWidth = attr.textPaint.measureText(letter)
        textHeight = -attr.fontMetrics!!.ascent + attr.fontMetrics!!.descent

        if (attr.mAttr.mScaleTextSeat == ScaleAttrEnum.LEFT) {
            letterCenterY = attr.startY
            x = attr.nodeStartX - textWidth - attr.mAttr.mScaleTextGap.px
            init = true
        } else if (attr.mAttr.mScaleTextSeat == ScaleAttrEnum.RIGHT) {
            letterCenterY = attr.startY
            x = attr.nodeStopX + attr.mAttr.mScaleTextGap.px
            init = true
        } else if (attr.mAttr.mScaleTextSeat == ScaleAttrEnum.TOP) {
            letterCenterY =
                attr.nodeStartY - textHeight / 2 - attr.mAttr.mScaleTextGap.px
            x = attr.nodeStartX - textWidth / 2
            init = true
        } else if (attr.mAttr.mScaleTextSeat == ScaleAttrEnum.BOTTOM) {
            letterCenterY =
                attr.nodeStopY + textHeight / 2 + attr.mAttr.mScaleTextGap.px

            x = attr.nodeStopX - textWidth / 2
            init = true
        } else {
            init = false
        }

        baseLine = letterCenterY + dy

    }

    fun initCircleTextSeat(letter: String, angle: Double, attr: BaseScaleView) {
        init = false

        dy =
            (attr.fontMetrics!!.bottom - attr.fontMetrics!!.top) / 2 - attr.fontMetrics!!.bottom
        textWidth = attr.textPaint.measureText(letter)
        textHeight = -attr.fontMetrics!!.ascent + attr.fontMetrics!!.descent


        if (attr.mAttr.mScaleTextSeat == ScaleAttrEnum.INSIDE) {

            textCircleRadius =
                attr.circleRadius - attr.circleRadius * attr.mAttr.mScaleNodeWidth - Math.min(
                    textWidth,
                    textHeight
                ) / 2 - attr.mAttr.mScaleTextGap.px
            init = true

        } else if (attr.mAttr.mScaleTextSeat == ScaleAttrEnum.OUTSIDE) {

            textCircleRadius = attr.circleRadius + Math.min(
                textWidth,
                textHeight
            ) / 2 + attr.mAttr.mScaleTextGap.px
            init = true
        }


        textCenterX =
            (attr.centerX + textCircleRadius
                    * Math.cos(angle)).toFloat()

        textCenterY =
            (attr.centerY + textCircleRadius
                    * Math.sin(angle)).toFloat()

        x = textCenterX - textWidth / 2

        baseLine = textCenterY + dy
    }
}
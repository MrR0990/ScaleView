package com.mrr.scaleview.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.mrr.scaleview.attr.ScaleViewAttr
import com.mrr.scaleview.enum.ScaleAttrEnum
import com.mrr.scaleview.rectf.ScaleTextRectF
import com.mrr.scaleview.util.UnitConversion.Companion.px
import java.util.function.Consumer

class HorizontalScaleView : BaseScaleView {


    var letter: String = ""
    var scaleTextRectF: ScaleTextRectF? = null

    constructor(attr: ScaleViewAttr) : super(attr) {
        scaleTextRectF = ScaleTextRectF()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //刻度线本身占用的空间
        var calibrationSpace = mAttr.mScaleLineWidth * (mAttr.mTotalProgress + 1)

        drawSpace = mAttr.mWidth - mAttr.mPaddingLeft - mAttr.mPaddingRight
        //刻度之间的缝隙大小
        perInterval = (drawSpace - calibrationSpace) / mAttr.mTotalProgress
        //每个一个刻度最长可绘制的空间
        interval = mAttr.mHeight - mAttr.mPaddingTop - mAttr.mPaddingBottom
        nodeLength = interval * mAttr.mScaleNodeWidth
        linelength = interval * mAttr.mScaleWidth

        clipRect = RectF()
    }

    override fun onDraw(canvas: Canvas?, touchX: Float, touchY: Float) {

        clipProgress = drawSpace * (touchX - mAttr.mPaddingLeft) / drawSpace

        clipRect?.set(0f, 0f, touchX, mAttr.mHeight)
        drawLineScale(canvas, changeColorPaint, clipRect)

        clipRect?.set(touchX, 0f, mAttr.mWidth, mAttr.mHeight)

        drawLineScale(canvas, originColorPaint, clipRect)

        drawCursor(canvas, touchX, touchY)
    }


    override fun initTouchXY(touchXCon: Consumer<Float>, touchYCon: Consumer<Float>) {
        touchXCon.accept(mAttr.mPaddingLeft.toFloat())
    }


    /**
     * 画线性的刻度
     */
    private fun drawLineScale(canvas: Canvas?, paint: Paint, clipRectF: RectF) {


        canvas!!.save()
        canvas.clipRect(clipRectF)


        nodeStartX = mAttr.mPaddingLeft + halfCalibration
        nodeStopX = nodeStartX

        nodeStartY = mAttr.mPaddingTop + (interval - nodeLength) / 2
        nodeStopY = nodeStartY + nodeLength

        startY = mAttr.mPaddingTop + (interval - linelength) / 2
        stopY = startY + linelength

        for (index in 0..mAttr.mTotalProgress) {

            canvas?.drawLine(
                nodeStartX,
                if (index % mAttr.mUnitScale == 0) nodeStartY.toFloat() else startY,
                nodeStopX,
                if (index % mAttr.mUnitScale == 0) nodeStopY.toFloat() else stopY,
                paint
            )

            if ((index % mAttr.mUnitScale == 0)) {

                letter = (index / mAttr.mUnitScale).toString()
                scaleTextRectF?.initLineTextSeat(letter, this)

                if (scaleTextRectF?.init == true) {

                    canvas.drawText(
                        letter,
                        scaleTextRectF!!.x,
                        scaleTextRectF!!.baseLine,
                        textPaint
                    )
                }
            }

            nodeStartX += (perInterval + mAttr.mScaleLineWidth)
            nodeStopX += (perInterval + mAttr.mScaleLineWidth)
        }



        canvas.restore()

    }


    /**
     * 绘制游标
     *
     */
    private fun drawCursor(canvas: Canvas?, touchX: Float, touchY: Float) {

        if (null == mAttr.mCursorBitmap) {
            return
        }

        when (mAttr.mCursorSeat) {
            ScaleAttrEnum.TOP -> {
                if (mAttr.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }

                cursorRectF.mTransX = touchX - mAttr.mCursorWidth.px / 2

                cursorRectF.mTransY =
                    mAttr.mPaddingTop + (interval - linelength) / 2 - mAttr.mCursorGap.px - mAttr.mCursorWidth.px
            }
            ScaleAttrEnum.BOTTOM -> {
                if (mAttr.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }

                cursorRectF.mTransX = touchX - mAttr.mCursorWidth.px / 2

                cursorRectF.mTransY =
                    mAttr.mPaddingTop + (interval - linelength) / 2 + linelength + mAttr.mCursorGap.px

            }


        }


        cursorMatrix.reset()
        // 创建操作图片用的 Matrix 对象
        cursorMatrix.postScale(
            cursorRectF.mScaleX, cursorRectF.mScaleY
        );
        cursorMatrix.postTranslate(
            cursorRectF.mTransX,
            cursorRectF.mTransY
        );

        canvas?.drawBitmap(mAttr.mCursorBitmap, cursorMatrix, null)

    }


}
package com.mrr.scaleview.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.Log
import com.android.internal.R.id.input
import com.mrr.scaleview.enum.ScaleAttrEnum
import com.mrr.scaleview.attr.ScaleViewAttr
import com.mrr.scaleview.util.UnitConversion.Companion.px
import java.util.function.Consumer

class VerticalScaleView : BaseView {

    private val TAG = "VerticalScaleView"

    constructor(attr: ScaleViewAttr) : super(attr) {

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //刻度线本身占用的空间
        var calibrationSpace = mAttr.mScaleLineWidth * (mAttr.mTotalProgress + 1)

        drawSpace = mAttr.mHeight - mAttr.mPaddingTop - mAttr.mPaddingBottom
        //刻度之间的缝隙大小
        perInterval = (drawSpace - calibrationSpace) / mAttr.mTotalProgress
        //每个一个刻度最长可绘制的空间
        interval = mAttr.mWidth - mAttr.mPaddingLeft - mAttr.mPaddingRight
        nodeLength = interval * mAttr.mScaleNodeWidth
        linelength = interval * mAttr.mScaleWidth

        clipRect = RectF()

    }

    override fun onDraw(canvas: Canvas?, touchX: Float, touchY: Float) {

        clipProgress = drawSpace * (touchY - mAttr.mPaddingTop) / drawSpace

        clipRect?.set(0f, 0f, mAttr.mWidth, touchY)
        drawLineScale(canvas, changeColorPaint, clipRect)

        clipRect?.set(0f, touchY, mAttr.mWidth, mAttr.mHeight)

        drawLineScale(canvas, originColorPaint, clipRect)

        drawCursor(canvas, touchX, touchY)
    }

    override fun initTouchXY(touchXCon: Consumer<Float>, touchYCon: Consumer<Float>) {
        touchYCon.accept(mAttr.mPaddingTop.toFloat())
    }


    /**
     * 画线性的刻度
     */
    private fun drawLineScale(canvas: Canvas?, paint: Paint, clipRectF: RectF) {


        canvas!!.save()
        canvas.clipRect(clipRectF)

        nodeStartX = mAttr.mPaddingLeft + (interval - nodeLength) / 2
        nodeStopX = nodeStartX + nodeLength

        startX = mAttr.mPaddingLeft + (interval - linelength) / 2
        stopX = startX + linelength

        startY = mAttr.mPaddingTop + halfCalibration
        stopY = startY

        Log.d(
            TAG,
            "VERTICAL drawLineScale: startY $startY mPaddingTop ${mAttr.mPaddingTop} mHalfCalibration $halfCalibration"
        )

        for (index in 0..mAttr.mTotalProgress) {

            canvas?.drawLine(
                if (index % mAttr.mUnitScale == 0) nodeStartX.toFloat() else startX,
                startY.toFloat(),
                if (index % mAttr.mUnitScale == 0) nodeStopX.toFloat() else stopX,
                stopY.toFloat(),
                paint
            )

            startY += (perInterval + mAttr.mScaleLineWidth)
            stopY += (perInterval + mAttr.mScaleLineWidth)


        }

        canvas.restore()

    }


    /**
     * 绘制游标
     *
     */
    private fun drawCursor(canvas: Canvas?, touchX: Float, touchY: Float) {

        if (null == mAttr.mCursorBitmap || mAttr.mScaleStyle != ScaleAttrEnum.LINE) {
            return
        }

        when (mAttr.mCursorLoc) {
            ScaleAttrEnum.LEFT -> {
                cursorRectF.mTransX =
                    mAttr.mPaddingLeft + (interval - linelength) / 2 - mAttr.mCursorGap.px - mAttr.mCursorWidth.px

                cursorRectF.mTransY = touchY - mAttr.mCursorWidth.px / 2

            }
            ScaleAttrEnum.RIGHT -> {
                cursorRectF.mTransX =
                    mAttr.mPaddingLeft + (interval - linelength) / 2 + linelength + mAttr.mCursorGap.px

                cursorRectF.mTransY = touchY - mAttr.mCursorWidth.px / 2

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
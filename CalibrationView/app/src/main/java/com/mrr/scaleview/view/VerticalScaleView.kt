package com.mrr.scaleview.view

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import com.mrr.scaleview.enum.ScaleAttrEnum
import com.mrr.scaleview.attr.ScaleViewAttr
import com.mrr.scaleview.rectf.CursorRectF

class VerticalScaleView(
    var a: ScaleViewAttr,
    var c: CursorRectF
) : BaseView(),
    CursorRectF.ProgressChangeListener {


    private var mTouchX = 0f;
    private var mTouchY = 0f;

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {

        //刻度线本身占用的空间
        var calibrationSpace = a.mScaleLineWidth * (a.mTotalProgress + 1)
        mDrawSpace = a.mHeight - a.mPaddingTop - a.mPaddingBottom

        //刻度之间的缝隙大小
        mPerInterval = (mDrawSpace - calibrationSpace) / a.mTotalProgress

        //每个一个刻度最长可绘制的空间
        mInterval = a.mWidth - a.mPaddingLeft - a.mPaddingRight
        nodeLength = mInterval * a.mScaleNodeWidth
        linelength = mInterval * a.mScaleWidth

        mTouchY = a.mPaddingTop.toFloat()

        mClipRect = RectF()

    }

    override fun onDraw(canvas: Canvas?) {

        mClipProgress = mDrawSpace * (mTouchY - a.mPaddingTop) / mDrawSpace

        mClipRect.set(0f, 0f, a.mWidth, mTouchY)
        drawLineScale(canvas, mChangeColorPaint, mClipRect)

        mClipRect.set(0f, mTouchY, mWidth, mHeight)
    }


    /**
     * 画线性的刻度
     */
    private fun drawLineScale(canvas: Canvas?, paint: Paint, clipRectF: RectF) {


        canvas!!.save()
        canvas.clipRect(clipRectF)


        nodeStartX = paddingLeft + (mInterval - nodeLength) / 2
        nodeStopX = nodeStartX + nodeLength

        startX = paddingLeft + (mInterval - linelength) / 2
        stopX = startX + linelength

        startY = mPaddingTop + mHalfCalibration
        stopY = startY

        for (index in 0..mParam.mTotalProgress) {

            canvas?.drawLine(
                if (index % mParam.mUnitScale == 0) nodeStartX.toFloat() else startX,
                startY.toFloat(),
                if (index % mParam.mUnitScale == 0) nodeStopX.toFloat() else stopX,
                stopY.toFloat(),
                paint
            )

            startY += (mPerInterval + mParam.mScaleThick)
            stopY += (mPerInterval + mParam.mScaleThick)

        }



        canvas.restore()

    }


    /**
     * 绘制游标
     *
     */
    private fun drawCursor(canvas: Canvas?) {

        if (null == mCursorBitmap) {
            return
        }

        when (mParam.mCursorLoc) {
            ScaleAttrEnum.LEFT -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }
                mCursorRectF.mTransX =
                    paddingLeft + (mInterval - linelength) / 2 - mParam.mCursorGap.px - mParam.mCursorWidth.px


                mCursorRectF.mTransY = mTouchY - mParam.mCursorWidth.px / 2

            }
            ScaleAttrEnum.RIGHT -> {
                if (mParam.mScaleStyle != ScaleAttrEnum.LINE) {
                    return
                }
                mCursorRectF.mTransX =
                    paddingLeft + (mInterval - linelength) / 2 + linelength + mParam.mCursorGap.px

                mCursorRectF.mTransY = mTouchY - mParam.mCursorWidth.px / 2

            }


        }


        mCursorMatrix.reset()
        // 创建操作图片用的 Matrix 对象
        mCursorMatrix.postScale(
            mCursorRectF.mScaleX, mCursorRectF.mScaleY
        );
        mCursorMatrix.postTranslate(
            mCursorRectF.mTransX,
            mCursorRectF.mTransY
        );

        if (mParam.mScaleStyle == ScaleAttrEnum.CIRCLE) {
            mCursorMatrix.postRotate(
                mCursorRectF.mRoutateDegress,
                mCursorRectF.mRoutateCenterX,
                mCursorRectF.mRoutateCenterY
            )
        }

        canvas?.drawBitmap(mCursorBitmap, mCursorMatrix, null)

    }

    override fun progressChange(curAngel: Double) {
    }
}
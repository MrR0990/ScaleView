package com.mrr.scaleview.util

import android.content.res.Resources

/**
 * 单位转换
 */
class UnitConversion {

    companion object {

        val Float.dp: Float
            get() = (this / Resources.getSystem().displayMetrics.density)
        val Float.px: Float
            get() = (this * Resources.getSystem().displayMetrics.density)

        val Float.sp2px: Float
            get() = (this * Resources.getSystem().getDisplayMetrics().scaledDensity)
    }

}
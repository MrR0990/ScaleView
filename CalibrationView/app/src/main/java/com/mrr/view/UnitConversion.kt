package com.mrr.view

import android.content.res.Resources
import android.util.TypedValue

class UnitConversion {

    companion object {
        //单位转换
        val Float.dp: Float
            get() = (this / Resources.getSystem().displayMetrics.density)
        val Float.px: Float
            get() = (this * Resources.getSystem().displayMetrics.density)

        val Float.sp2px: Float
            get() = (this * Resources.getSystem().getDisplayMetrics().scaledDensity)
    }

}
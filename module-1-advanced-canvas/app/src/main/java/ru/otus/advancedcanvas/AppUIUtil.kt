package ru.otus.advancedcanvas

import android.content.Context

class AppUIUtil {
    companion object{
        fun convertDpiToPixel(dp: Float, context: Context): Float {
            val resources = context.resources
            val metrics = resources.displayMetrics
            return dp * (metrics.densityDpi / 160f)
        }
    }
}
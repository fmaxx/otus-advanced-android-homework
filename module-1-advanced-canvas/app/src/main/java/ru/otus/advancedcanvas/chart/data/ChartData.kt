package ru.otus.advancedcanvas.chart.data

class ChartData(val before: Float, val after: Float) {

    internal fun getValue(isBefore: Boolean): Float {
        return if (isBefore) before else after
    }

    internal fun getStringValue(isBefore: Boolean): String {
        return Integer.toString((if (isBefore) before else after).toInt())
    }

    companion object{
        fun empty(): ChartData {
            return ChartData(0f, 0f)
        }
    }


}
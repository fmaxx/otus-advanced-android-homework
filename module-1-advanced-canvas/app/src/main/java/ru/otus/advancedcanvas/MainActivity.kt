package ru.otus.advancedcanvas

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ru.otus.advancedcanvas.chart.data.ChartData

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        chartControl.setData(generateRandomDataSet(100))

//        try to test a huge data-set ;)
//        chartControl.setData(generateRandomDataSet(1_000_000))
    }

    private fun generateRandomDataSet(size: Int): List<ChartData> {
        if (size <= 0) return emptyList()
        val max = 10.0f
        val min = 0.0f
        val result: ArrayList<ChartData> = ArrayList()
        var counter = size
        while (counter > -1) {
            counter--
            result.add(
                ChartData(getRand(min, max), getRand(min, max))
            )
        }
        return result
    }

    private fun getRand(min: Float, max: Float): Float {
        return (min + Math.random() * (max - min)).toFloat()
    }
}

package ru.otus.advancedcanvas.chart

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.HorizontalScrollView
import ru.otus.advancedcanvas.chart.data.ChartData

/**
 * Контрол для отображения 2х графиков одновременно
 * по горизонтали, контрол можно скроллировать,
 * поддерживает отрисовку данных любого размера
 * я тестировал дата сет на 1млн точек.
 *
 * */

class ChartControl : HorizontalScrollView {

    private var chartView: ChartView? = null

    private var isClickGestureAdded: Boolean = false

    // region Constructor
    constructor(context: Context) : this(context, null) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init()
    }
    // endregion

    private fun init() {
        isFillViewport = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            // bugfix: OpenGLRenderer: Path too large to be rendered into a texture
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
        }
    }

    fun setData(list: List<ChartData>) {
        /*
         * we should recreate ChartView each time, because HorizontalScrollView has a measure bug
         * */
        initChartView(context)

        chartView?.let {
            it.setData(list)
            // update scroll
            post {
                val scrollPosition = 0
                it.updateChartBy(scrollPosition, width)
                scrollTo(scrollPosition, 0)
            }
        }
    }

    private fun initChartView(context: Context) {
        chartView?.let {
            removeView(it)
            it.dispose()
        }

        val view = ChartView(context)
        view.layoutParams =
            LayoutParams(
                LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT
            )
        addView(view)
        chartView = view
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        chartView?.updateChartBy(l, width)
    }

    override fun setOnClickListener(l: View.OnClickListener?) {
        super.setOnClickListener(l)
        setupGestures()
    }

    /*
     * The scroll is preventing regular touch events for onClickListener
     * @see: https://stackoverflow.com/a/21832530/2154011
     * */
    private fun setupGestures() {
        if (isClickGestureAdded) return
        isClickGestureAdded = true
        val detector = GestureDetector(context, object : GestureDetector.OnGestureListener {
            override fun onDown(e: MotionEvent): Boolean {
                return false
            }

            override fun onShowPress(e: MotionEvent) {

            }

            override fun onSingleTapUp(e: MotionEvent): Boolean {
                performClick()
                return false
            }

            override fun onScroll(
                e1: MotionEvent,
                e2: MotionEvent,
                distanceX: Float,
                distanceY: Float
            ): Boolean {
                return false
            }

            override fun onLongPress(e: MotionEvent) {

            }

            override fun onFling(
                e1: MotionEvent,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                return false
            }
        })

        setOnTouchListener { v, event ->
            detector.onTouchEvent(event)
            false
        }
    }
}
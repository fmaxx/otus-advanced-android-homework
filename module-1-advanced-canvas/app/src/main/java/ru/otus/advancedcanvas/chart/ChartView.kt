package ru.otus.advancedcanvas.chart

import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.Drawable
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import ru.otus.advancedcanvas.AppUIUtil
import ru.otus.advancedcanvas.R
import ru.otus.advancedcanvas.chart.data.ChartData
import java.util.ArrayList
import kotlin.math.max


/*
* Класс занимается отрисовкой графика
* в зависимости от положения скролла.
* Отрисовывается буфер: 1 экран слева, экран, 1 экран справа
* */
class ChartView : View {

    private val tickWidthDpi = 33
    private val numVisibleTicks = 9
    private val horizontalContentPaddingDpi = 5
    private val verticalContentPaddingDpi = tickWidthDpi / 2
    private val drawBufferPixels = Rect()

    private lateinit var chartDrawer: ChartDrawer
    private var contentWidth: Int = 0
    private var viewPortWidth: Int = 0

    // region Constructor
    constructor(context: Context) : this(context, null) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        val typedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.ChartView, 0, 0)
        init(typedArray)
    }
    // endregion

    fun setData(list: List<ChartData>?) {
        if (list == null) {
            updateContentWidth(0)
            chartDrawer.setData(emptyList())
            return
        }
        updateContentWidth(list.size - 1)
        chartDrawer.setData(list)
    }

    fun dispose() {
        chartDrawer.dispose()
    }

    fun updateChartBy(scrollPosition: Int, viewPortWidth: Int) {
        this.viewPortWidth = viewPortWidth
        val min = 0
        val max = contentWidth
        var bufferLeft = scrollPosition - viewPortWidth
        bufferLeft = if (bufferLeft < min) min else bufferLeft
        var bufferRight = scrollPosition + viewPortWidth
        bufferRight = if (bufferRight > max) max else bufferRight
        drawBufferPixels.set(bufferLeft, 0, bufferRight, 0)
        invalidate()
    }

    private fun updateContentWidth(size: Int) {
        val numTicks = Math.max(numVisibleTicks, size)
        val dpiWidth = (tickWidthDpi * numTicks + 2 * horizontalContentPaddingDpi).toFloat()
        contentWidth = AppUIUtil.convertDpiToPixel(dpiWidth, context).toInt()
    }

    // region lifecycle
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val fullContentWidth = Math.max(widthMeasureSpec, contentWidth)
        val width = fullContentWidth + paddingLeft + paddingRight + suggestedMinimumWidth
        resolveSizeAndState(width, widthMeasureSpec, 1)
        setMeasuredDimension(width, heightMeasureSpec)

        // size ChartDrawer
        val height = measuredHeight
        val hPadding = AppUIUtil.convertDpiToPixel(horizontalContentPaddingDpi.toFloat(), context)
        val vPadding = AppUIUtil.convertDpiToPixel(verticalContentPaddingDpi.toFloat(), context)
        val tickWidthPixels = AppUIUtil.convertDpiToPixel(tickWidthDpi.toFloat(), context)
        chartDrawer.setSize(
            widthMeasureSpec,
            height,
            tickWidthPixels,
            hPadding.toInt(),
            vPadding.toInt()
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        chartDrawer.draw(canvas, drawBufferPixels)
    }
    // endregion

    private fun init(typedArray: TypedArray?) {
        chartDrawer = ChartDrawer(
            resources.displayMetrics.density,
            R.drawable.vector_tens_chart_dotted_line,
            getColor(R.color.gray_border)

        )
        initStyles(typedArray)
    }

    private fun getColor(@ColorRes res: Int): Int {
        return ContextCompat.getColor(context, res)
    }

    private fun getDimen(@DimenRes res: Int): Float {
        return context.resources.getDimension(res)
    }

    // region styles
    private fun initStyles(typedArray: TypedArray?) {
        if (typedArray == null) {
            setDefaultStyles()
            return
        }
        try {
            chartDrawer.setStyleBefore(
                typedArray.getDimension(
                    R.styleable.ChartView_before_dot_size,
                    getDimen(R.dimen.tens_chart_dot_before)
                ),
                typedArray.getColor(
                    R.styleable.ChartView_before_dot_color,
                    getColor(R.color.tens_chart_dot_before)
                ),
                typedArray.getDimension(
                    R.styleable.ChartView_before_stroke_size,
                    getDimen(R.dimen.tens_chart_stroke_before)
                ),
                typedArray.getColor(
                    R.styleable.ChartView_before_stroke_color,
                    getColor(R.color.tens_chart_stroke_before)
                )
            )

            chartDrawer.setStyleAfter(
                typedArray.getDimension(
                    R.styleable.ChartView_after_dot_size,
                    getDimen(R.dimen.tens_chart_dot_after)
                ),
                typedArray.getColor(
                    R.styleable.ChartView_after_dot_color,
                    getColor(R.color.tens_chart_dot_after)
                ),
                typedArray.getDimension(
                    R.styleable.ChartView_after_stroke_size,
                    getDimen(R.dimen.tens_chart_stroke_after)
                ),
                typedArray.getColor(
                    R.styleable.ChartView_after_stroke_color,
                    getColor(R.color.tens_chart_stroke_after)
                )
            )

        } finally {
            typedArray.recycle()
            setDefaultStyles()
        }
    }

    private fun setDefaultStyles() {
        chartDrawer.setStyleBefore(
            getDimen(R.dimen.tens_chart_dot_before),
            getColor(R.color.tens_chart_dot_before),
            getDimen(R.dimen.tens_chart_stroke_before),
            getColor(R.color.tens_chart_stroke_before)
        )

        chartDrawer.setStyleAfter(
            getDimen(R.dimen.tens_chart_dot_after),
            getColor(R.color.tens_chart_dot_after),
            getDimen(R.dimen.tens_chart_stroke_after),
            getColor(R.color.tens_chart_stroke_after)
        )
    }
    // endregion


    // region Helpers
    /*
    * Внутренний класс-хелпер, берет на себя процедуру отрисовки
    * контента в заданом буфере на Canvas
    * */
    private inner class ChartDrawer internal constructor(
        density: Float,
        @param:DrawableRes @field:DrawableRes
        private val dottedLineResource: Int,
        private val noPointTextColor: Int
    ) {

        private val maxYAxisValue = 10
        private val curveIntensity = 0.5f

        // before chart
        private val beforeStrokePath: Path
        private val beforeStrokePaint: Paint
        private val beforeDotPaint: Paint

        private val afterStrokePath: Path
        private val afterStrokePaint: Paint
        private val afterDotPaint: Paint
        private val textPaint: TextPaint = TextPaint()

        private var dataSet: List<ChartData>? = null
        private var viewPortHeightPixels: Int = 0
        private var viewPortWidthPixels: Int = 0

        private var tickWidthPixels: Float = 0.0f
        private var tickHeightPixels: Float = 0.0f
        private var clearOnDraw: Boolean = false

        private val textBounds = Rect()
        private var dottedLineDrawable: Drawable? = null
        private var verticalPaddingPixels: Int = 0
        private var horizontalPaddingPixels: Int = 0

        private val drawableDottedLine: Drawable
            get() {
                if (dottedLineDrawable == null) {
                    dottedLineDrawable = resources.getDrawable(dottedLineResource, null)
                }
                return dottedLineDrawable!!
            }

        init {

            textPaint.isAntiAlias = true
            textPaint.letterSpacing = -0.1f
            textPaint.textSize = 9 * density
            textPaint.isFakeBoldText = true

            beforeStrokePath = Path()
            beforeStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            beforeStrokePaint.style = Paint.Style.STROKE
            beforeDotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            beforeDotPaint.style = Paint.Style.FILL

            afterStrokePath = Path()
            afterStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG)
            afterStrokePaint.style = Paint.Style.STROKE
            afterDotPaint = Paint(Paint.ANTI_ALIAS_FLAG)
            afterDotPaint.style = Paint.Style.FILL
        }

        fun setData(list: List<ChartData>): ChartDrawer {
            this.dataSet = ArrayList<ChartData>(list)
            clearOnDraw = true
            return this
        }

        internal fun setStyleBefore(
            dotSizePixels: Float,
            dotColor: Int,
            strokeSizePixels: Float,
            strokeColor: Int
        ): ChartDrawer {

            beforeDotPaint.strokeWidth = dotSizePixels
            beforeDotPaint.color = dotColor
            beforeStrokePaint.strokeWidth = strokeSizePixels
            beforeStrokePaint.color = strokeColor
            return this
        }

        internal fun setStyleAfter(
            dotSizePixels: Float,
            dotColor: Int,
            strokeSizePixels: Float,
            strokeColor: Int
        ): ChartDrawer {

            afterDotPaint.strokeWidth = dotSizePixels
            afterDotPaint.color = dotColor
            afterStrokePaint.strokeWidth = strokeSizePixels
            afterStrokePaint.color = strokeColor
            return this
        }

        internal fun setSize(
            viewPortWidthPixels: Int,
            viewPortHeightPixels: Int,
            tickWidthPixels: Float,
            horizontalPaddingPixels: Int,
            verticalPaddingPixels: Int
        ): ChartDrawer {
            this.viewPortWidthPixels = viewPortWidthPixels
            this.viewPortHeightPixels = viewPortHeightPixels
            this.horizontalPaddingPixels = horizontalPaddingPixels
            this.tickWidthPixels = tickWidthPixels
            this.verticalPaddingPixels = verticalPaddingPixels
            tickHeightPixels =
                ((this.viewPortHeightPixels - 2 * verticalPaddingPixels) / maxYAxisValue).toFloat()
            return this
        }

        internal fun draw(canvas: Canvas, drawBufferInPixels: Rect): ChartDrawer {
            if (dataSet == null || dataSet!!.size == 0) {
                canvas.drawColor(Color.WHITE)
                return this
            }

            if (clearOnDraw) {
                canvas.drawColor(Color.WHITE)
                clearOnDraw = false
            }

            // curves
            val curvePositions = convertPixelsToPositions(drawBufferInPixels, true)
            val curveLeft = curvePositions.left
            val curveRight = curvePositions.right

            // lines buffer is [ - viewPort][viewPort][ + viewPort]
            val linesDrawBuffer = Rect(
                drawBufferInPixels.left - viewPortWidth,
                0,
                drawBufferInPixels.left + 2 * viewPortWidth,
                0
            )
            val linesPositions = convertPixelsToPositions(linesDrawBuffer, false)
            val lineStart = linesPositions.left
            val lineEnd = Math.max(curveRight, linesPositions.right)

            beforeStrokePath.reset()
            afterStrokePath.reset()

            for (i in lineStart..lineEnd) {
                if (i < lineEnd) {
                    // dotted lines and texts
                    drawDottedLine(i, canvas)
                }
                drawText(i, canvas)
                // curves
                if (i >= curveLeft && i <= curveRight) {
                    drawCurveBefore(i, canvas)
                    drawCurveAfter(i, canvas)
                }
            }

            canvas.drawPath(beforeStrokePath, beforeStrokePaint)
            canvas.drawPath(afterStrokePath, afterStrokePaint)

            beforeStrokePath.close()
            afterStrokePath.close()

            return this
        }

        private fun convertPixelsToPositions(pixels: Rect, limitRight: Boolean): Rect {
            var left = (pixels.left.toFloat() / tickWidthPixels).toInt()
            left = if (left < 0) 0 else left
            var right = (pixels.right.toFloat() / tickWidthPixels).toInt()
            if (limitRight) {
                right = if (right > dataSet!!.size - 1) dataSet!!.size - 1 else right
            }
            return Rect(left, 0, right, 0)
        }

        private fun drawCurveAt(
            position: Int,
            stroke: Path,
            dot: Paint,
            canvas: Canvas,
            isBefore: Boolean
        ) {
            if (dataSet == null) return
            val prevIndex = max(position - 1, 0)
            val prev = dataSet!![prevIndex]
            val curr = dataSet!![position]

            val curPoint = toPoint(position, curr.getValue(isBefore))
            val useLargeRadius = isBefore && curr.after == curr.before
            drawDot(canvas, curPoint, dot, useLargeRadius)

            if (position == 0) {
                stroke.moveTo(curPoint.x, curPoint.y)
                return
            }

            val prevPoint = toPoint(prevIndex, prev.getValue(isBefore))
            val controlX = prevPoint.x + (curPoint.x - prevPoint.x) * curveIntensity
            stroke.cubicTo(
                // control 1
                controlX,
                prevPoint.y,

                // control 2
                controlX,
                curPoint.y,

                // end point
                curPoint.x,
                curPoint.y
            )
        }

        private fun drawCurveAfter(position: Int, canvas: Canvas) {
            drawCurveAt(position, afterStrokePath, afterDotPaint, canvas, false)
        }

        private fun drawCurveBefore(position: Int, canvas: Canvas) {
            drawCurveAt(position, beforeStrokePath, beforeDotPaint, canvas, true)
        }

        private fun drawDottedLine(position: Int, canvas: Canvas) {
            val d = drawableDottedLine
            val p = toPoint(position, 0f)
            val left = (p.x + tickWidthPixels / 2).toInt()
            d.setBounds(
                left,
                verticalPaddingPixels,
                left + d.intrinsicWidth,
                viewPortHeightPixels - verticalPaddingPixels
            )
            d.draw(canvas)
        }

        private fun drawText(position: Int, canvas: Canvas) {
            var beforeColor = noPointTextColor
            var afterColor = noPointTextColor
            var data = ChartData.empty()
            if (position > -1 && position < dataSet!!.size) {
                data = dataSet!![position]
                beforeColor = beforeDotPaint.color
                afterColor = afterDotPaint.color
            }
            val p = toPoint(position, 0f)

            // BEFORE
            var text = data.getStringValue(true)
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            var x = p.x - (textBounds.right shr 1)
            var y = textBounds.height().toFloat()
            textPaint.color = beforeColor
            canvas.drawText(text, x, y, textPaint)

            // AFTER
            text = data.getStringValue(false)
            textPaint.getTextBounds(text, 0, text.length, textBounds)
            x = p.x - (textBounds.right shr 1)
            y = (viewPortHeightPixels - textBounds.bottom).toFloat()
            textPaint.color = afterColor
            canvas.drawText(text, x, y, textPaint)
        }

        private fun drawDot(
            canvas: Canvas,
            point: PlotPoint,
            paint: Paint,
            useLargeRadius: Boolean
        ) {
            var radius = paint.strokeWidth / 2
            if (useLargeRadius) {
                radius *= 1.6f
            }
            drawDot(canvas, point, paint, radius)
        }

        private fun drawDot(canvas: Canvas, point: PlotPoint, paint: Paint, radius: Float) {
            canvas.drawCircle(point.x, point.y, radius, paint)
        }

        private fun toPoint(position: Int, yValue: Float): PlotPoint {
            return PlotPoint(
                // absolute X value
                position * tickWidthPixels + horizontalPaddingPixels,
                // absolute Y value
                viewPortHeightPixels.toFloat() - yValue * tickHeightPixels - verticalPaddingPixels.toFloat()
            )
        }

        fun dispose() {
            dataSet = null
        }
    }

    private inner class PlotPoint internal constructor(internal val x: Float, internal val y: Float)
    // endregion
}
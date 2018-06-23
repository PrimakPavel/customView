package com.example.pavelprimak.customviewapp.customView

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.os.Build
import android.support.annotation.RequiresApi
import android.util.AttributeSet
import android.view.View
import com.example.pavelprimak.customviewapp.R
import com.example.pavelprimak.customviewapp.model.Event
import com.example.pavelprimak.customviewapp.utils.ConvertValueUtil


class LineGraphView : View {
    companion object {
        const val TOP_MARGIN = 20f//in dp
        const val LEFT_MARGIN = 0f
        const val WIDTH_MINUTES_GRAPH = 1440f//in dp
        const val WIDTH_SECONDS_GRAPH = 86400f//in dp
        const val DEFAULT_HEIGHT_GRAPH = 30f//in dp
        const val DIVIDER_MINUTES = 0
        const val DIVIDER_SECONDS = 1
        const val SEC_IN_MIN = 60
        const val MIN_IN_HOUR = 60
        const val DEFAULT_BACKGROUND_COLOR = Color.BLACK
        const val DEFAULT_TEXT_COLOR = Color.BLACK
        const val DEFAULT_TEXT_SIZE = 10f
        const val DEFAULT_TEXT_TOP_MARGIN = 15f//in dp
        const val DEFAULT_MAIN_EVENT_COLOR = Color.GRAY
        const val DEFAULT_MARK_EVENT_COLOR = Color.RED
        const val DEFAULT_LINE_WIDTH = 1f//in dp
        const val DEFAULT_LINE_HEIGHT = 7f//in dp
    }

    //COLORS
    private var backgroundColors = DEFAULT_BACKGROUND_COLOR
    private var textColor = DEFAULT_TEXT_COLOR
    private var mainEventColor = DEFAULT_MAIN_EVENT_COLOR
    private var markEventColor = DEFAULT_MARK_EVENT_COLOR

    //SIZES
    private var dividerType = DIVIDER_MINUTES
    private var mainWidthInPx = ConvertValueUtil.convertDpToPixel(WIDTH_MINUTES_GRAPH, context)
    private var topMarginInPx = ConvertValueUtil.convertDpToPixel(TOP_MARGIN, context)
    private var mainHeightInPx = ConvertValueUtil.convertDpToPixel(DEFAULT_HEIGHT_GRAPH, context)
    private var lineWidthInPx = ConvertValueUtil.convertDpToPixel(DEFAULT_LINE_WIDTH, context)
    private var lineHeightInPx = ConvertValueUtil.convertDpToPixel(DEFAULT_LINE_HEIGHT, context)
    private var textSizeInPx = ConvertValueUtil.convertDpToPixel(DEFAULT_TEXT_SIZE, context)
    private var textTopMarginInPx = ConvertValueUtil.convertDpToPixel(DEFAULT_TEXT_TOP_MARGIN, context)


    //data Lists
    private var mainEventsList = ArrayList<Event>()
    private var markEventsList = ArrayList<Event>()

    private var paint = Paint()


    constructor(context: Context) : super(context) {
        init(null)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init(attrs)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(attrs)
    }

    constructor(context: Context, backgroundColors: Int = DEFAULT_BACKGROUND_COLOR,
                mainEventColor: Int = DEFAULT_MAIN_EVENT_COLOR,
                markEventColor: Int = DEFAULT_MARK_EVENT_COLOR,
                textColor: Int = DEFAULT_TEXT_COLOR,
                textSize: Float = DEFAULT_TEXT_SIZE,
                dividerType: Int = DIVIDER_MINUTES,
                mainHeight: Float = DEFAULT_HEIGHT_GRAPH) : super(context) {
        init(backgroundColors, mainEventColor, markEventColor, textColor, textSize, dividerType, mainHeight)
    }

    public override fun onDraw(canvas: Canvas) {
        //background prepare
        drawBackground(canvas)
        //MAIN EVENTS DRAW FROM LIST
        mainEventsList.forEach { event ->
            drawMainEvent(canvas, fromSecToPositionInPx(event.startPosition), fromSecToPositionInPx(event.duration))
        }
        //MARK EVENTS DRAW FROM LIST
        markEventsList.forEach { event ->
            drawMarkEvent(canvas, fromSecToPositionInPx(event.startPosition), fromSecToPositionInPx(event.duration))
        }
        drawLines(canvas)
        drawTexts(canvas)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val width = mainWidthInPx
        val height = mainHeightInPx + topMarginInPx
        setMeasuredDimension(width.toInt(), height.toInt())
    }

    fun setValuesAndInvalidate(backgroundColors: Int = DEFAULT_BACKGROUND_COLOR,
                               mainEventColor: Int = DEFAULT_MAIN_EVENT_COLOR,
                               markEventColor: Int = DEFAULT_MARK_EVENT_COLOR,
                               textColor: Int = DEFAULT_TEXT_COLOR,
                               textSize: Int = ConvertValueUtil.convertDpToPixel(DEFAULT_TEXT_SIZE, context).toInt(),
                               dividerType: Int = DIVIDER_MINUTES,
                               mainHeight: Int = ConvertValueUtil.convertDpToPixel(DEFAULT_HEIGHT_GRAPH, context).toInt()) {
        init(backgroundColors, mainEventColor, markEventColor, textColor, textSize, dividerType, mainHeight)
        invalidate()
    }

    private fun init(set: AttributeSet?) {
        // Obtain a typed array of attributes
        val typedArray = context.theme.obtainStyledAttributes(set, R.styleable.LineGraphView,
                0, 0)

        // Extract custom attributes into member variables
        backgroundColors = typedArray.getColor(R.styleable.LineGraphView_LgvBackgroundColor, DEFAULT_BACKGROUND_COLOR)
        mainEventColor = typedArray.getColor(R.styleable.LineGraphView_LgvMainEventColor, DEFAULT_MAIN_EVENT_COLOR)
        markEventColor = typedArray.getColor(R.styleable.LineGraphView_LgvMarkEventColor, DEFAULT_MARK_EVENT_COLOR)
        textColor = typedArray.getColor(R.styleable.LineGraphView_LgvTextColor, DEFAULT_TEXT_COLOR)
        textSizeInPx = typedArray.getDimension(R.styleable.LineGraphView_LgvTextSize, textSizeInPx)
        dividerType = typedArray.getInt(R.styleable.LineGraphView_LgvMinDivider, DIVIDER_MINUTES)
        mainHeightInPx = typedArray.getDimension(R.styleable.LineGraphView_LgvBorderHeight, mainHeightInPx) + topMarginInPx
        initGraphWidth()
        // TypedArray objects are shared and must be recycled.
        typedArray.recycle()
    }

    private fun init(backgroundColors: Int, mainEventColor: Int, markEventColor: Int, textColor: Int, textSize: Float, dividerType: Int, mainHeight: Float) {
        this.backgroundColors = backgroundColors
        this.mainEventColor = mainEventColor
        this.markEventColor = markEventColor
        this.textColor = textColor
        textSizeInPx = ConvertValueUtil.convertDpToPixel(textSize, context)
        this.dividerType = dividerType
        mainHeightInPx = ConvertValueUtil.convertDpToPixel(mainHeight, context) + topMarginInPx
        initGraphWidth()
    }

    private fun init(backgroundColors: Int, mainEventColor: Int, markEventColor: Int, textColor: Int, textSizeInPx: Int, dividerType: Int, mainHeightInPx: Int) {
        this.backgroundColors = backgroundColors
        this.mainEventColor = mainEventColor
        this.markEventColor = markEventColor
        this.textColor = textColor
        this.textSizeInPx = textSizeInPx.toFloat()
        this.dividerType = dividerType
        this.mainHeightInPx = mainHeightInPx.toFloat()
        initGraphWidth()
    }

    private fun initGraphWidth() {
        mainWidthInPx = if (dividerType == DIVIDER_MINUTES) {
            ConvertValueUtil.convertDpToPixel(WIDTH_MINUTES_GRAPH, context)
        } else {
            ConvertValueUtil.convertDpToPixel(WIDTH_SECONDS_GRAPH, context)
        }
    }

    private fun drawBackground(canvas: Canvas) {
        paint.color = backgroundColors
        paint.strokeWidth = 0f
        canvas.drawRect(LEFT_MARGIN, topMarginInPx, mainWidthInPx, mainHeightInPx, paint)
    }

    private fun drawText(canvas: Canvas, position: Float, text: String) {
        paint.color = textColor
        paint.textSize = textSizeInPx
        var textLeftMargin = 0f
        textLeftMargin = when (text.length) {
            3 -> textSizeInPx / 1.5f
            4 -> textSizeInPx
            else -> textSizeInPx / 2
        }
        canvas.drawText(text, position - textLeftMargin, textTopMarginInPx, paint)
    }

    private fun drawTexts(canvas: Canvas) {
        if (dividerType == DIVIDER_MINUTES) {
            for (i in MIN_IN_HOUR..WIDTH_MINUTES_GRAPH.toInt() - MIN_IN_HOUR step MIN_IN_HOUR) {
                drawText(canvas, ConvertValueUtil.convertDpToPixel(i.toFloat(), context), String.format("%02d", i / MIN_IN_HOUR))
            }
        } else {
            for (i in SEC_IN_MIN..WIDTH_SECONDS_GRAPH.toInt() - SEC_IN_MIN step SEC_IN_MIN) {
                drawText(canvas, ConvertValueUtil.convertDpToPixel(i.toFloat(), context), String.format("%02d", i / SEC_IN_MIN))
            }
        }
    }

    private fun drawLines(canvas: Canvas) {
        if (dividerType == DIVIDER_MINUTES) {
            for (i in 0..WIDTH_MINUTES_GRAPH.toInt() step MIN_IN_HOUR) {
                drawLine(canvas, ConvertValueUtil.convertDpToPixel(i.toFloat(), context))
            }
        } else {
            for (i in 0..WIDTH_SECONDS_GRAPH.toInt() step SEC_IN_MIN) {
                drawLine(canvas, ConvertValueUtil.convertDpToPixel(i.toFloat(), context))
            }
        }

    }

    private fun drawLine(canvas: Canvas, position: Float) {
        paint.color = textColor
        paint.strokeWidth = lineWidthInPx
        canvas.drawLine(position, topMarginInPx - lineHeightInPx / 2, position, topMarginInPx + lineHeightInPx / 2, paint)
    }

    fun setMainEventList(mainEvents: List<Event>) {
        mainEventsList.clear()
        mainEventsList.addAll(mainEvents)
        invalidate()
    }

    fun setMarkEventList(markEvents: List<Event>) {
        markEventsList.clear()
        markEventsList.addAll(markEvents)
        invalidate()
    }

    private fun fromSecToPositionInPx(secPosition: Int): Float {
        return if (dividerType == DIVIDER_MINUTES) {
            ConvertValueUtil.convertDpToPixel((secPosition / SEC_IN_MIN).toFloat(), context)
        } else {
            ConvertValueUtil.convertDpToPixel(secPosition.toFloat(), context)
        }
    }

    private fun drawMainEvent(canvas: Canvas, startPosition: Float, duration: Float) {
        paint.strokeWidth = 0f
        paint.color = mainEventColor
        val finishPosition = startPosition + duration
        canvas.drawRect(startPosition, topMarginInPx, finishPosition, mainHeightInPx, paint)
    }

    private fun drawMarkEvent(canvas: Canvas, startPosition: Float, duration: Float) {
        paint.strokeWidth = 0f
        paint.color = markEventColor
        val finishPosition = startPosition + duration
        canvas.drawRect(startPosition, topMarginInPx, finishPosition, mainHeightInPx, paint)
    }

}

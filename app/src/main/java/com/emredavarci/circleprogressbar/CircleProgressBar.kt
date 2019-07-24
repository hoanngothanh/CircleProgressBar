package com.emredavarci.circleprogressbar

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.preference.Preference
import android.text.Html
import android.text.TextPaint
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView

/**
 * Created by M. Emre Davarci on 02.08.2017.
 */

class CircleProgressBar : View {

    private var progressBarPaint: Paint? = null
    private var bacgroundPaint: Paint? = null
    private var textPaint: Paint? = null

    private var mRadius: Float = 0.toFloat()
    private val mArcBounds = RectF()

    internal var drawUpto = 0f

    private var progressColor: Int = 0
    private var backgroundColor: Int = 0
    private var strokeWidth: Float = 0.toFloat()
    private var backgroundWidth: Float = 0.toFloat()
    private var roundedCorners: Boolean = false
    private var maxValue: Float = 0.toFloat()

    private var progressTextColor = Color.BLACK
    private var textSize = 18f
    private var text: String? = ""
    private var suffix: String? = ""
    private var prefix: String? = ""

    internal var defStyleAttr: Int = 0

    var progress: Float
        get() = drawUpto
        set(f) {
            drawUpto = f
            invalidate()
        }

    val progressPercentage: Float
        get() = drawUpto / getMaxValue() * 100

    var textColor: Int
        get() = progressTextColor
        set(color) {
            progressTextColor = color
            textPaint!!.color = color
            invalidate()
        }

    constructor(context: Context) : super(context) {

        // create the Paint and set its color

    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        this.defStyleAttr = defStyleAttr
        initPaints(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0) {
        initPaints(context, attrs)
    }

    private fun initPaints(context: Context, attrs: AttributeSet) {

        val ta = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressBar, defStyleAttr, 0)

        progressColor = ta.getColor(R.styleable.CircleProgressBar_progressColor, Color.BLUE)
        backgroundColor = ta.getColor(R.styleable.CircleProgressBar_backgroundColor, Color.GRAY)
        strokeWidth = ta.getFloat(R.styleable.CircleProgressBar_strokeWidth, 10f)
        backgroundWidth = ta.getFloat(R.styleable.CircleProgressBar_backgroundWidth, 10f)
        roundedCorners = ta.getBoolean(R.styleable.CircleProgressBar_roundedCorners, false)
        maxValue = ta.getFloat(R.styleable.CircleProgressBar_maxValue, 100f)
        progressTextColor = ta.getColor(R.styleable.CircleProgressBar_progressTextColor, Color.BLACK)
        textSize = ta.getDimension(R.styleable.CircleProgressBar_textSize, 18f)
        suffix = ta.getString(R.styleable.CircleProgressBar_suffix)
        prefix = ta.getString(R.styleable.CircleProgressBar_prefix)
        text = ta.getString(R.styleable.CircleProgressBar_progressText)

        progressBarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        progressBarPaint!!.style = Paint.Style.FILL
        progressBarPaint!!.color = progressColor
        progressBarPaint!!.style = Paint.Style.STROKE
        progressBarPaint!!.strokeWidth = strokeWidth * resources.displayMetrics.density
        if (roundedCorners) {
            progressBarPaint!!.strokeCap = Paint.Cap.ROUND
        } else {
            progressBarPaint!!.strokeCap = Paint.Cap.BUTT
        }
        val pc = String.format("#%06X", 0xFFFFFF and progressColor)
        progressBarPaint!!.color = Color.parseColor(pc)

        bacgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        bacgroundPaint!!.style = Paint.Style.FILL
        bacgroundPaint!!.color = backgroundColor
        bacgroundPaint!!.style = Paint.Style.STROKE
        bacgroundPaint!!.strokeWidth = backgroundWidth * resources.displayMetrics.density
        bacgroundPaint!!.strokeCap = Paint.Cap.SQUARE
        val bc = String.format("#%06X", 0xFFFFFF and backgroundColor)
        bacgroundPaint!!.color = Color.parseColor(bc)

        ta.recycle()

        textPaint = TextPaint()
        textPaint!!.color = progressTextColor
        val c = String.format("#%06X", 0xFFFFFF and progressTextColor)
        textPaint!!.color = Color.parseColor(c)
        textPaint!!.textSize = textSize
        textPaint!!.isAntiAlias = true

        //paint.setAntiAlias(true);
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mRadius = Math.min(w, h) / 2f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = View.MeasureSpec.getSize(widthMeasureSpec)
        val h = View.MeasureSpec.getSize(heightMeasureSpec)

        val size = Math.min(w, h)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val mouthInset = mRadius / 3
        mArcBounds.set(mouthInset, mouthInset, mRadius * 2 - mouthInset, mRadius * 2 - mouthInset)
        canvas.drawArc(mArcBounds, 0f, 360f, false, bacgroundPaint!!)
        canvas.drawArc(mArcBounds, 270f, drawUpto / getMaxValue() * 360, false, progressBarPaint!!)

        if (TextUtils.isEmpty(suffix)) {
            suffix = ""
        }

        if (TextUtils.isEmpty(prefix)) {
            prefix = ""
        }

        val drawnText = prefix + text + suffix

        if (!TextUtils.isEmpty(text)) {
            val textHeight = textPaint!!.descent() + textPaint!!.ascent()
            canvas.drawText(drawnText, (width - textPaint!!.measureText(drawnText)) / 2.0f, (width - textHeight) / 2.0f, textPaint!!)
        }

    }

    fun setProgressColor(color: Int) {
        progressColor = color
        progressBarPaint!!.color = color
        invalidate()
    }

    fun setProgressColor(color: String) {
        progressBarPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    override fun setBackgroundColor(color: Int) {
        backgroundColor = color
        bacgroundPaint!!.color = color
        invalidate()
    }

    fun setBackgroundColor(color: String) {
        bacgroundPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    fun getMaxValue(): Float {
        return maxValue
    }

    fun setMaxValue(max: Float) {
        maxValue = max
        invalidate()
    }

    fun setStrokeWidth(width: Float) {
        strokeWidth = width
        invalidate()
    }

    fun getStrokeWidth(): Float {
        return strokeWidth
    }

    fun setBackgroundWidth(width: Float) {
        backgroundWidth = width
        invalidate()
    }

    fun getBackgroundWidth(): Float {
        return backgroundWidth
    }

    fun setText(progressText: String) {
        text = progressText
        invalidate()
    }

    fun getText(): String? {
        return text
    }

    fun setTextColor(color: String) {
        textPaint!!.color = Color.parseColor(color)
        invalidate()
    }

    fun setSuffix(suffix: String) {
        this.suffix = suffix
        invalidate()
    }

    fun getSuffix(): String? {
        return suffix
    }

    fun getPrefix(): String? {
        return prefix
    }

    fun setPrefix(prefix: String) {
        this.prefix = prefix
        invalidate()
    }
}



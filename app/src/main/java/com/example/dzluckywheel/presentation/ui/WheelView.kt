package com.example.dzluckywheel.presentation.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.utils.RandomUtils
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class WheelView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var entries: List<Entry> = emptyList()

    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        color = Color.WHITE
        strokeWidth = 6f
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.LEFT
        textSize = 45f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        setShadowLayer(4f, 0f, 2f, Color.parseColor("#80000000"))
    }
    private val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#20000000")
        setShadowLayer(20f, 0f, 10f, Color.parseColor("#40000000"))
    }
    private val indicatorPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.wheel_indicator)
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 4f, Color.parseColor("#40000000"))
    }
    private val centerPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.FILL
        setShadowLayer(8f, 0f, 4f, Color.parseColor("#40000000"))
    }
    private val centerTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.md_theme_primary)
        textAlign = Paint.Align.CENTER
        textSize = 40f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private var rotationAngle = 0f
    private var isSpinning = false
    var onResult: ((Entry) -> Unit)? = null

    private val colors = listOf(
        ContextCompat.getColor(context, R.color.wheel_color_1),
        ContextCompat.getColor(context, R.color.wheel_color_2),
        ContextCompat.getColor(context, R.color.wheel_color_3),
        ContextCompat.getColor(context, R.color.wheel_color_4),
        ContextCompat.getColor(context, R.color.wheel_color_5),
        ContextCompat.getColor(context, R.color.wheel_color_6),
        ContextCompat.getColor(context, R.color.wheel_color_7),
        ContextCompat.getColor(context, R.color.wheel_color_8)
    )

    init {
        setLayerType(LAYER_TYPE_SOFTWARE, null)
    }

    fun setEntries(entries: List<Entry>) {
        this.entries = entries
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        val cx = width / 2f
        val cy = height / 2f
        val radius = size / 2f - 24f
        val rect = RectF(cx - radius, cy - radius, cx + radius, cy + radius)

        canvas.drawCircle(cx, cy, radius, shadowPaint)

        if (entries.isEmpty()) {
            slicePaint.color = Color.LTGRAY
            canvas.drawArc(rect, 0f, 360f, true, slicePaint)
            drawCenterButton(canvas, cx, cy, radius)
            drawIndicator(canvas, cx, cy, radius)
            return
        }

        val sweepAngle = 360f / entries.size
        var startAngle = rotationAngle - 90f

        entries.forEachIndexed { index, entry ->
            slicePaint.color = colors[index % colors.size]
            canvas.drawArc(rect, startAngle, sweepAngle, true, slicePaint)
            canvas.drawArc(rect, startAngle, sweepAngle, true, strokePaint)

            canvas.save()
            canvas.translate(cx, cy)
            canvas.rotate(startAngle + sweepAngle / 2)
            
            if (entry.type == com.example.dzluckywheel.data.model.EntryType.TEXT) {
                val text = if (entry.value.length > 12) entry.value.take(10) + ".." else entry.value
                canvas.drawText(text, radius * 0.35f, 16f, textPaint)
            } else {
                try {
                    val uri = Uri.parse(entry.value)
                    val input = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(input)
                    input?.close()
                    bitmap?.let {
                        val imgSize = (radius / 3.5f).toInt()
                        val scaled = Bitmap.createScaledBitmap(it, imgSize, imgSize, true)
                        canvas.drawBitmap(scaled, radius * 0.5f, -imgSize / 2f, null)
                    }
                } catch (_: Exception) {}
            }
            canvas.restore()
            startAngle += sweepAngle
        }

        canvas.drawCircle(cx, cy, radius, strokePaint)
        drawCenterButton(canvas, cx, cy, radius)
        drawIndicator(canvas, cx, cy, radius)
    }

    private fun drawCenterButton(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val centerRadius = radius * 0.2f
        canvas.drawCircle(cx, cy, centerRadius, centerPaint)
        canvas.drawText("QUAY", cx, cy + 14f, centerTextPaint)
    }

    private fun drawIndicator(canvas: Canvas, cx: Float, cy: Float, radius: Float) {
        val path = Path()
        path.moveTo(cx, cy - radius + 30f)
        path.lineTo(cx - 25f, cy - radius - 20f)
        path.lineTo(cx + 25f, cy - radius - 20f)
        path.close()
        canvas.drawPath(path, indicatorPaint)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val cx = width / 2f
            val cy = height / 2f
            val dx = event.x - cx
            val dy = event.y - cy
            val distance = sqrt(dx.pow(2) + dy.pow(2))

            val size = min(width, height).toFloat()
            val radius = size / 2f - 24f
            val centerRadius = radius * 0.2f

            if (distance < centerRadius && !isSpinning) {
                spinWheel()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun spinWheel() {
        if (entries.isEmpty() || isSpinning) return

        isSpinning = true

        val validIndices = entries.indices.filter { !entries[it].excluded }
        val targetIndex = if (validIndices.isNotEmpty()) validIndices.random() else entries.indices.random()

        val sweepAngle = 360f / entries.size
        val currentMod = rotationAngle % 360f
        val targetRotation = rotationAngle + 3600f - currentMod - (targetIndex * sweepAngle + sweepAngle / 2f)

        val animator = ValueAnimator.ofFloat(rotationAngle, targetRotation)
        animator.duration = 4500
        animator.interpolator = DecelerateInterpolator(1.8f)
        animator.addUpdateListener {
            rotationAngle = it.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                onResult?.invoke(entries[targetIndex])
                isSpinning = false
            }
            override fun onAnimationStart(animation: android.animation.Animator) {}
            override fun onAnimationCancel(animation: android.animation.Animator) {
                isSpinning = false
            }
            override fun onAnimationRepeat(animation: android.animation.Animator) {}
        })
        animator.start()
    }
}

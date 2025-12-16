package com.example.dzluckywheel.presentation.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
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
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40f
    }

    private var rotationAngle = 0f
    private var isSpinning = false
    var onResult: ((Entry) -> Unit)? = null

    // Danh sách màu đa dạng
    private val colors = listOf(
        Color.parseColor("#FF6F61"), // đỏ cam
        Color.parseColor("#6B5B95"), // tím
        Color.parseColor("#88B04B"), // xanh lá
        Color.parseColor("#F7CAC9"), // hồng nhạt
        Color.parseColor("#92A8D1"), // xanh dương nhạt
        Color.parseColor("#955251"), // nâu đỏ
        Color.parseColor("#B565A7"), // tím pastel
        Color.parseColor("#009B77"), // xanh ngọc
        Color.parseColor("#DD4124"), // đỏ tươi
        Color.parseColor("#45B8AC")  // xanh teal
    )

    fun setEntries(entries: List<Entry>) {
        this.entries = entries
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (entries.isEmpty()) return

        val size = min(width, height).toFloat()
        val rect = RectF(0f, 0f, size, size)

        val sweepAngle = 360f / entries.size
        var startAngle = rotationAngle

        entries.forEachIndexed { index, entry ->
            // chọn màu theo index, nếu entries > colors thì lặp lại
            paint.color = colors[index % colors.size]
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

            // vẽ text
            paint.color = Color.BLACK
            val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val textX = size / 2 + (size / 3) * Math.cos(angleRad).toFloat()
            val textY = size / 2 + (size / 3) * Math.sin(angleRad).toFloat()
            canvas.drawText(entry.value, textX, textY, paint)

            startAngle += sweepAngle
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val cx = width / 2f
            val cy = height / 2f
            val dx = event.x - cx
            val dy = event.y - cy
            val distance = sqrt(dx.pow(2) + dy.pow(2))

            // Nếu chạm vào tâm và chưa quay
            if (distance < width / 6f && !isSpinning) {
                spinWheel()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    private fun spinWheel() {
        if (entries.isEmpty()) return
        if (isSpinning) return

        isSpinning = true

        val targetAngle = RandomUtils.getTargetAngle(entries)
        val animator = ValueAnimator.ofFloat(rotationAngle, rotationAngle + 1440f + targetAngle)
        animator.duration = 3000
        animator.addUpdateListener {
            rotationAngle = it.animatedValue as Float
            invalidate()
        }
        animator.addListener(object : android.animation.Animator.AnimatorListener {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                val sweepAngle = 360f / entries.size
                val normalizedAngle = (rotationAngle % 360 + 360) % 360
                val index = ((normalizedAngle) / sweepAngle).toInt()
                onResult?.invoke(entries[index])
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

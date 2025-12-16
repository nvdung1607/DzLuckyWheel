package com.example.dzluckywheel.presentation.ui

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.net.Uri
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
        Color.parseColor("#FF6F61"), Color.parseColor("#6B5B95"),
        Color.parseColor("#88B04B"), Color.parseColor("#F7CAC9"),
        Color.parseColor("#92A8D1"), Color.parseColor("#955251"),
        Color.parseColor("#B565A7"), Color.parseColor("#009B77"),
        Color.parseColor("#DD4124"), Color.parseColor("#45B8AC")
    )

    fun setEntries(entries: List<Entry>) {
        this.entries = entries
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        val size = min(width, height).toFloat()
        val rect = RectF(0f, 0f, size, size)

        if (entries.isEmpty()) {
            // Vẽ vòng tròn trống khi không có entry
            paint.color = Color.LTGRAY
            canvas.drawArc(rect, 0f, 360f, true, paint)
            return
        }

        val sweepAngle = 360f / entries.size
        var startAngle = rotationAngle

        entries.forEachIndexed { index, entry ->
            // Vẽ lát với màu riêng
            paint.color = colors[index % colors.size]
            canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

            // Tính vị trí trung tâm lát để vẽ nội dung
            val angleRad = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
            val contentX = size / 2 + (size / 3) * Math.cos(angleRad).toFloat()
            val contentY = size / 2 + (size / 3) * Math.sin(angleRad).toFloat()

            if (entry.type == com.example.dzluckywheel.data.model.EntryType.TEXT) {
                paint.color = Color.BLACK
                canvas.drawText(entry.value, contentX, contentY, paint)
            } else {
                try {
                    val uri = Uri.parse(entry.value)
                    val input = context.contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(input)
                    bitmap?.let {
                        val scaled = Bitmap.createScaledBitmap(
                            it,
                            (size / 5).toInt(),
                            (size / 5).toInt(),
                            true
                        )
                        canvas.drawBitmap(
                            scaled,
                            contentX - scaled.width / 2,
                            contentY - scaled.height / 2,
                            null
                        )
                    }
                } catch (_: Exception) {
                    // Nếu lỗi khi load ảnh thì bỏ qua
                }
            }
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

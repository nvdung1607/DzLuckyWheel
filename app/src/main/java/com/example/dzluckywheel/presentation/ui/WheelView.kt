package com.example.dzluckywheel.presentation.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import com.example.dzluckywheel.data.model.Entry

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

    fun setEntries(entries: List<Entry>) {
        this.entries = entries
        invalidate() // yêu cầu vẽ lại
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (entries.isEmpty()) return

        val size = Math.min(width, height).toFloat()
        val rect = RectF(0f, 0f, size, size)

        val sweepAngle = 360f / entries.size
        var startAngle = 0f

        entries.forEachIndexed { index, entry ->
            // xen kẽ màu
            paint.color = if (index % 2 == 0) Color.parseColor("#FFCC66") else Color.parseColor("#66CCFF")

            // vẽ lát
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
}

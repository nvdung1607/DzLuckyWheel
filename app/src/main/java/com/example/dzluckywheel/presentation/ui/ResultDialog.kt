package com.example.dzluckywheel.presentation.ui

import android.app.Dialog
import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.model.EntryType

class ResultDialog(
    context: Context,
    private val entry: Entry,
    private val onRemove: (Entry) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_result)

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val ivResult = findViewById<ImageView>(R.id.ivResult)
        val btnRemove = findViewById<Button>(R.id.btnRemove)
        val btnClose = findViewById<Button>(R.id.btnClose)

        if (entry.type == EntryType.TEXT) {
            tvResult.visibility = View.VISIBLE
            ivResult.visibility = View.GONE
            tvResult.text = entry.value
        } else {
            tvResult.visibility = View.GONE
            ivResult.visibility = View.VISIBLE
            try {
                val uri = Uri.parse(entry.value)
                val input = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(input)
                input?.close()
                ivResult.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                tvResult.visibility = View.VISIBLE
                ivResult.visibility = View.GONE
                tvResult.text = "Không thể hiển thị ảnh"
            }
        }

        btnRemove.setOnClickListener {
            onRemove(entry)
            dismiss()
        }

        btnClose.setOnClickListener {
            dismiss()
        }
    }
}

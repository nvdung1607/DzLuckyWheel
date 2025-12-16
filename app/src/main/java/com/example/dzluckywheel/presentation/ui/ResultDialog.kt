package com.example.dzluckywheel.presentation.ui

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry

class ResultDialog(
    context: Context,
    private val entry: Entry,
    private val onRemove: (Entry) -> Unit
) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_result)

        val tvResult = findViewById<TextView>(R.id.tvResult)
        val btnClose = findViewById<Button>(R.id.btnClose)
        val btnRemove = findViewById<Button>(R.id.btnRemove)

        tvResult.text = "Kết quả: ${entry.value}"

        btnClose.setOnClickListener { dismiss() }
        btnRemove.setOnClickListener {
            onRemove(entry)
            dismiss()
        }
    }
}

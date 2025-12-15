package com.example.dzluckywheel.presentation.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dzluckywheel.R
import com.example.dzluckywheel.presentation.viewmodel.WheelViewModel

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tạo instance ViewModel và gọi testLogic
        val viewModel = WheelViewModel()
        viewModel.testLogic()
    }
}

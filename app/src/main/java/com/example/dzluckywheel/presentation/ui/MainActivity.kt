package com.example.dzluckywheel.presentation.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.model.EntryType
import com.example.dzluckywheel.presentation.viewmodel.WheelViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: WheelViewModel by viewModels()
    private lateinit var adapter: EntryAdapter
    private lateinit var wheelView: WheelView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo WheelView
        wheelView = findViewById(R.id.wheelView)

        // Khởi tạo RecyclerView + Adapter
        adapter = EntryAdapter(emptyList())
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Observe LiveData từ ViewModel
        viewModel.entries.observe(this) { entries ->
            adapter.updateData(entries)
            wheelView.setEntries(entries) // cập nhật vòng quay
        }

        // Lắng nghe kết quả quay từ WheelView
        wheelView.onResult = { entry ->
            val dialog = ResultDialog(this, entry) { removed ->
                viewModel.removeEntry(removed)
            }
            dialog.show()
        }

        // Tạo vài entry mặc định để test
        viewModel.addEntry(Entry(1, EntryType.TEXT, "Alice"))
        viewModel.addEntry(Entry(2, EntryType.TEXT, "Bob"))
        viewModel.addEntry(Entry(3, EntryType.TEXT, "Charlie"))
        viewModel.addEntry(Entry(4, EntryType.TEXT, "David"))

        // Gắn sự kiện cho 3 nút điều khiển
        findViewById<android.widget.Button>(R.id.btnShuffle).setOnClickListener {
            viewModel.shuffleEntries()
        }

        findViewById<android.widget.Button>(R.id.btnSort).setOnClickListener {
            viewModel.sortEntries()
        }

        findViewById<android.widget.Button>(R.id.btnAddImage).setOnClickListener {
            // TODO: mở gallery để chọn ảnh và thêm vào danh sách
            // Tạm thời thêm entry giả để test
            viewModel.addEntry(Entry(System.currentTimeMillis().toInt(), EntryType.TEXT, "New Entry"))
        }
    }
}

package com.example.dzluckywheel.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.text.Editable
import android.text.TextWatcher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.dzluckywheel.R
import com.example.dzluckywheel.data.model.Entry
import com.example.dzluckywheel.data.model.EntryType
import com.example.dzluckywheel.presentation.viewmodel.WheelViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel: WheelViewModel by viewModels()
    private lateinit var wheelView: WheelView

    // Quản lý ảnh tách riêng: id ảnh -> uri
    private val imageMap = linkedMapOf<Int, String>()
    private var nextImageId = 1

    // Cờ tránh vòng lặp TextWatcher khi setText chương trình
    private var isSyncingText = false

    private val imagePicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            // Giữ quyền truy cập lâu dài để có thể hiển thị lại ảnh
            try {
                contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: Exception) { /* optional */ }

            // Thêm vào imageMap với id tăng dần
            val imageId = nextImageId++
            imageMap[imageId] = it.toString()

            // Cập nhật ViewModel bằng cách gộp TEXT hiện tại + toàn bộ IMAGE trong imageMap
            val textEntries = parseTextEntries(findViewById(R.id.etEntries))
            val allEntries = combineEntries(textEntries, imageMap)
            viewModel.setEntries(allEntries)

            // Cập nhật EditText: thêm dòng "ảnh {id}: tên"
            val et = findViewById<android.widget.EditText>(R.id.etEntries)
            val name = ellipsize(getFileName(it), 10)
            val current = et.text.toString()
            val newLine = if (current.isEmpty()) "" else "\n"
            isSyncingText = true
            et.setText(current + newLine + "ảnh $imageId: $name")
            isSyncingText = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        wheelView = findViewById(R.id.wheelView)
        val etEntries = findViewById<android.widget.EditText>(R.id.etEntries)

        // TextWatcher: chỉ quản lý TEXT, và xóa/giữ IMAGE dựa trên các dòng "ảnh n:"
        etEntries.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (isSyncingText || s == null) return

                // 1) Parse TEXT entries từ EditText
                val textEntries = mutableListOf<Entry>()
                s.toString().split("\n").forEach { line ->
                    val t = line.trim()
                    if (t.isNotEmpty() && !t.startsWith("ảnh")) {
                        textEntries.add(Entry(0, EntryType.TEXT, t)) // id sẽ gán lại khi combine
                    }
                }

                // 2) Tìm các id ảnh đang còn trong EditText
                val imageIdsKept = mutableSetOf<Int>()
                s.toString().split("\n").forEach { line ->
                    val t = line.trim()
                    if (t.startsWith("ảnh")) {
                        val numPart = t.split(":")[0].removePrefix("ảnh").trim()
                        val id = numPart.toIntOrNull()
                        if (id != null) imageIdsKept.add(id)
                    }
                }

                // 3) Xóa ảnh nào không còn dòng "ảnh n:" và giữ lại ảnh còn tồn tại
                val iterator = imageMap.iterator()
                while (iterator.hasNext()) {
                    val (id, _) = iterator.next()
                    if (!imageIdsKept.contains(id)) {
                        iterator.remove() // ảnh bị xóa khỏi map
                    }
                }

                // 4) Gộp lại TEXT + IMAGE hiện có
                val allEntries = combineEntries(textEntries, imageMap)
                viewModel.setEntries(allEntries)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        // Quan sát entries để cập nhật WheelView
        viewModel.entries.observe(this) { entries ->
            wheelView.setEntries(entries)
        }

        // Kết quả quay
        wheelView.onResult = { entry ->
            val dialog = ResultDialog(this, entry) { removed ->
                // Nếu xóa entry ảnh từ dialog, đồng bộ xóa khỏi imageMap và EditText
                if (removed.type == EntryType.IMAGE) {
                    imageMap.remove(removed.id)
                }
                viewModel.removeEntry(removed)
                syncEditText(findViewById(R.id.etEntries))
            }
            dialog.show()
        }

        // Shuffle
        findViewById<android.widget.Button>(R.id.btnShuffle).setOnClickListener {
            viewModel.shuffleEntries()
            syncEditText(etEntries)
        }

        // Sort
        findViewById<android.widget.Button>(R.id.btnSort).setOnClickListener {
            viewModel.sortEntries()
            syncEditText(etEntries)
        }

        // Add Image
        findViewById<android.widget.Button>(R.id.btnAddImage).setOnClickListener {
            imagePicker.launch(arrayOf("image/*"))
        }

        // Khởi tạo ví dụ
        if (viewModel.entries.value.isNullOrEmpty()) {
            isSyncingText = true
            etEntries.setText(listOf("Dũng", "Hùng", "Nghĩa", "Đạt", "Vũ", "Thiệu", "Chính").joinToString("\n"))
            isSyncingText = false
            viewModel.setEntries(parseTextEntries(etEntries)) // chưa có ảnh
        }
    }

    // Gộp TEXT và IMAGE: gán id tuần tự hiển thị, giữ id gốc cho IMAGE để ResultDialog nhận diện
    private fun combineEntries(textEntries: List<Entry>, imageMap: Map<Int, String>): List<Entry> {
        val result = mutableListOf<Entry>()
        var displayId = 1

        // Text trước
        textEntries.forEach { t ->
            result.add(Entry(displayId++, EntryType.TEXT, t.value))
        }

        // Ảnh sau (theo thứ tự map)
        imageMap.forEach { (imageId, uri) ->
            // id = imageId (giữ id gốc để đối chiếu khi xóa), giá trị là uri
            result.add(Entry(imageId, EntryType.IMAGE, uri))
        }

        return result
    }

    // Đồng bộ lại EditText theo danh sách hiện tại (text + ảnh)
    private fun syncEditText(et: android.widget.EditText) {
        val entries = viewModel.entries.value.orEmpty()
        val lines = mutableListOf<String>()

        // Chỉ render: TEXT là nguyên gốc; IMAGE là "ảnh id: tên"
        entries.forEach { e ->
            if (e.type == EntryType.TEXT) {
                lines.add(e.value)
            } else {
                val name = ellipsize(getFileName(Uri.parse(e.value)), 20)
                lines.add("ảnh ${e.id}: $name")
            }
        }

        isSyncingText = true
        et.setText(lines.joinToString("\n"))
        isSyncingText = false
    }

    // Parse TEXT từ EditText thành entries TEXT (không đụng ảnh)
    private fun parseTextEntries(et: android.widget.EditText): List<Entry> {
        val list = mutableListOf<Entry>()
        et.text.toString().split("\n").forEach { line ->
            val t = line.trim()
            if (t.isNotEmpty() && !t.startsWith("ảnh")) {
                list.add(Entry(0, EntryType.TEXT, t))
            }
        }
        return list
    }

    // Lấy tên file đúng
    private fun getFileName(uri: Uri): String {
        var name = "unknown"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex != -1 && it.moveToFirst()) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    // Cắt gọn tên dài
    private fun ellipsize(s: String, max: Int): String {
        return if (s.length > max) s.take(max) + "..." else s
    }
}

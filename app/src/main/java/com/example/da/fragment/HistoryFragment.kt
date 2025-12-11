package com.example.da.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.da.R
import com.example.da.activity.MainActivity // Sử dụng MainActivity để điều khiển BottomNav
import com.example.da.adapter.HistoryAdapter
import com.example.da.database.HistoryDAO
import com.example.da.model.HistoryEntity
import kotlin.collections.isNotEmpty
import kotlin.collections.toMutableList
import kotlin.text.trim

class HistoryFragment : Fragment() {

    private lateinit var historyDAO: HistoryDAO
    private lateinit var adapter: HistoryAdapter
    private lateinit var rcv: RecyclerView
    private lateinit var spinnerLoaiLoc: Spinner
    private lateinit var spinnerGiaTriLoc: Spinner
    private lateinit var etSearchTenDe: EditText
    private lateinit var btnSearchTenDe: Button
    private lateinit var btnTroVe: Button

    private val loaiLocOptions =
        listOf("Tất cả", "Môn học", "Mức độ", "Điểm", "Thời gian làm", "Ngày làm")

    // --- Vòng đời của Fragment ---

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        historyDAO = HistoryDAO(requireContext())
        // historyDAO.insertInitialData() // Chỉ chạy nếu cần thêm dữ liệu mẫu
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Ẩn thanh điều hướng dưới cùng
        (activity as? MainActivity)?.showBottomNavigation(false)
        // Dùng layout mới
        return inflater.inflate(R.layout.lich_su_lam_bai, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setControl(view)
        setEvent()
        populateLoaiLocSpinner()
        applyFilters() // Tải dữ liệu ban đầu
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Hiện lại thanh điều hướng khi rời khỏi màn hình
        (activity as? MainActivity)?.showBottomNavigation(true)
    }

    // --- Các hàm cài đặt và xử lý sự kiện ---

    private fun setControl(view: View) {
        rcv = view.findViewById(R.id.recyclerView)
        spinnerLoaiLoc = view.findViewById(R.id.spinnerLoaiLoc)
        spinnerGiaTriLoc = view.findViewById(R.id.spinnerGiaTriLoc)
        etSearchTenDe = view.findViewById(R.id.etSearchTenDe)
        btnSearchTenDe = view.findViewById(R.id.btnSearchTenDe)
        btnTroVe = view.findViewById(R.id.btnTroVeTrangChu)

        // Tích hợp chức năng xóa và sửa
        adapter = HistoryAdapter(
            emptyList(),
            onDeleteClicked = { history -> showDeleteConfirmationDialog(history) },
            onEditClicked = { history ->
                Toast.makeText(requireContext(), "Chức năng sửa cho ID: ${history.id}", Toast.LENGTH_SHORT).show()
            }
        )
        rcv.layoutManager = LinearLayoutManager(requireContext())
        rcv.adapter = adapter
    }

    private fun setEvent() {
        btnTroVe.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        spinnerLoaiLoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                populateGiaTriLocSpinner(loaiLocOptions[pos])
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        spinnerGiaTriLoc.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                applyFilters()
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        btnSearchTenDe.setOnClickListener { applyFilters() }
    }

    // --- Các hàm logic chính ---

    private fun showDeleteConfirmationDialog(history: HistoryEntity) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xác nhận xóa")
            .setMessage("Bạn có chắc chắn muốn xóa bản ghi '${history.tenDe}' không?")
            .setPositiveButton("Xóa") { _, _ ->
                val result = historyDAO.deleteById(history.id)
                if (result > 0) {
                    Toast.makeText(requireContext(), "Đã xóa thành công", Toast.LENGTH_SHORT).show()
                    applyFilters()
                } else {
                    Toast.makeText(requireContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun populateLoaiLocSpinner() {
        spinnerLoaiLoc.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, loaiLocOptions)
    }

    private fun populateGiaTriLocSpinner(loaiLoc: String) {
        val distinctValues: List<String> = when (loaiLoc) {
            "Môn học" -> historyDAO.getDistinctMonHoc()
            "Mức độ" -> historyDAO.getDistinctMucDo()
            "Điểm" -> historyDAO.getDistinctDiemRanges()
            "Thời gian làm" -> historyDAO.getDistinctThoiGianRanges()
            "Ngày làm" -> historyDAO.getDistinctMonths()
            else -> emptyList()
        }
        val displayList = distinctValues.toMutableList()
        if (displayList.isNotEmpty()) {
            displayList.add(0, "Tất cả")
        }
        spinnerGiaTriLoc.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, displayList)
    }

    private fun applyFilters() {
        val loai = spinnerLoaiLoc.selectedItem?.toString() ?: "Tất cả"
        val giaTri = spinnerGiaTriLoc.selectedItem?.toString() ?: "Tất cả"
        val queryTenDe = etSearchTenDe.text.toString().trim()

        val list = historyDAO.filterAndSearch(loai, giaTri, queryTenDe)
        adapter.update(list)
    }
}
package com.example.da.database

import android.content.Context
import com.example.da.model.HistoryEntity

/**
 * Lớp DAO (Data Access Object) chuyên xử lý logic truy vấn cho màn hình Lịch sử làm bài.
 */
class HistoryDAO(context: Context) {
    // Sử dụng DatabaseHelper để truy cập dữ liệu
    private val dbHelper = DatabaseHelper(context)

    // --- CÁC HÀM LỌC ---

    fun getDistinctDiemRanges(): List<String> {
        return dbHelper.getDistinctDiemRanges()
    }

    fun getDistinctThoiGianRanges(): List<String> {
        return dbHelper.getDistinctThoiGianRanges()
    }

    fun getDistinctMonths(): List<String> {
        return dbHelper.getDistinctMonths()
    }

    fun getDistinctMonHoc(): List<String> {
        return dbHelper.getDistinctMonHoc()
    }

    fun getDistinctMucDo(): List<String> {
        return dbHelper.getDistinctMucDo()
    }

    // --- TRUY XUẤT CHÍNH ---

    fun getAll(): List<HistoryEntity> {
        return dbHelper.getAllHistoryEntities()
    }

    fun filterAndSearch(loaiLoc: String, giaTri: String, queryTenDe: String): List<HistoryEntity> {
        return dbHelper.filterAndSearch(loaiLoc, giaTri, queryTenDe)
    }

    // --- CRUD ---

    /**
     * Xóa một kết quả bài kiểm tra dựa trên ID (result_id).
     */
    fun deleteById(resultId: Int): Int {
        return dbHelper.deleteTestResult(resultId)
    }

    // Thêm hàm chèn dữ liệu mẫu nếu cần
    fun insertInitialData() {
        // Chỉ nên chạy DBHelper.addSampleData() nếu bạn cần đảm bảo DB có dữ liệu TestResult.
        // Cần chỉnh sửa hàm addSampleData trong DatabaseHelper để đảm bảo các Test ID được chèn trước.
    }
}
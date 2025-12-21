package com.example.da.model

data class HistoryEntity(
    val id: Int, // ID của TestResult
    val monHoc: String, // Tên môn học
    val tenDe: String, // Tên bài kiểm tra
    val diem: String, // Điểm (String để dễ dàng hiển thị 8.5)
    val thoiGianLam: String, // Thời gian làm (ví dụ: "35 phút")
    val ngayLam: String, // Ngày làm (ví dụ: "20/11/25")
    val mucDo: String // Mức độ (Dễ, Trung bình, Khó)
)
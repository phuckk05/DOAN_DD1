# Hướng dẫn sử dụng SQLite Database cho Môn Học

## Các file đã tạo/cập nhật:

### 1. DatabaseHelper.kt (MỚI)
**Đường dẫn:** `app/src/main/java/com/example/da/database/DatabaseHelper.kt`

Class này quản lý SQLite database với các chức năng:
- Tạo database và bảng `subjects`
- Thêm môn học mới
- Lấy danh sách tất cả môn học
- Xóa môn học
- Cập nhật môn học
- Kiểm tra môn học đã tồn tại
- Thêm dữ liệu mẫu khi chạy lần đầu

### 2. TaoMonHocFragment.kt (CẬP NHẬT)
**Đường dẫn:** `app/src/main/java/com/example/da/fragment/TaoMonHocFragment.kt`

Các thay đổi:
- Thêm DatabaseHelper để lưu môn học vào SQLite
- Kiểm tra trùng lặp tên môn học trước khi thêm
- Hiển thị thông báo thành công/thất bại
- Xóa input và quay lại màn hình trước sau khi thêm thành công

### 3. TaoCauHoiFragment.kt (CẬP NHẬT)
**Đường dẫn:** `app/src/main/java/com/example/da/fragment/TaoCauHoiFragment.kt`

Các thay đổi:
- Load danh sách môn học từ database vào Spinner
- Tự động thêm 8 môn học mẫu khi chạy lần đầu
- Refresh danh sách môn học mỗi khi quay lại fragment (onResume)
- Kiểm tra có môn học trước khi lưu câu hỏi
- Hiển thị thông báo nếu chưa có môn học nào

## Cách sử dụng:

### Thêm môn học mới:
1. Vào màn hình "Tạo Môn Học"
2. Nhập tên môn học (ít nhất 2 ký tự)
3. Nhấn nút "Thêm"
4. Môn học sẽ được lưu vào SQLite database
5. Hệ thống kiểm tra trùng lặp tên

### Tạo câu hỏi với môn học:
1. Vào màn hình "Tạo Câu Hỏi"
2. Chọn môn học từ Spinner (danh sách load từ database)
3. Nhập câu hỏi và chọn độ khó
4. Nhấn "Thêm" để lưu

### Dữ liệu mẫu:
Khi chạy app lần đầu, hệ thống tự động thêm 8 môn học:
- Toán
- Văn
- Tiếng Anh
- Vật Lý
- Hóa Học
- Sinh Học
- Lịch Sử
- Địa Lý

## Cấu trúc Database:

### Bảng: subjects
| Cột | Kiểu | Mô tả |
|-----|------|-------|
| id | INTEGER | Primary key, auto increment |
| name | TEXT | Tên môn học (NOT NULL) |
| created_at | INTEGER | Timestamp tạo môn học |

## Lưu ý:
- Database được lưu tại: `/data/data/com.example.da/databases/QuizApp.db`
- Có thể xem database bằng Android Studio Database Inspector
- Khi update DATABASE_VERSION, dữ liệu cũ sẽ bị xóa (onUpgrade)


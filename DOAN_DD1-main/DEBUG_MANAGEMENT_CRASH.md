# 🐛 FIX: APP CRASH KHI VÀO QUẢN LÝ

## Các nguyên nhân có thể:

### 1. Database Version Conflict
- DatabaseHelper VERSION tăng từ 1 → 2
- Khi upgrade, tất cả dữ liệu cũ bị xóa
- App crash nếu code cố đọc dữ liệu không tồn tại

### 2. Null Pointer trong QuestionAdapter
- `submitList()` được gọi với list rỗng hoặc null
- RecyclerView crash khi bind dữ liệu

### 3. Methods không được gọi đúng
- `getQuestionsBySubject()` có thể trả về null
- Database query lỗi

## Cách kiểm tra:

1. **Xem Logcat** khi app crash để biết chính xác lỗi gì
2. **Xóa app** và cài lại để database được tạo mới với version 2
3. **Thêm try-catch** trong ManagementFragment

## Giải pháp tạm thời:

### Option 1: Xóa app và cài lại
```
- Uninstall app
- Build và install lại
- Database sẽ được tạo mới với version 2
```

### Option 2: Thêm try-catch trong onViewCreated
```kotlin
override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    try {
        // Initialize database
        dbHelper = DatabaseHelper(requireContext())
        
        // ... existing code ...
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }
}
```

### Option 3: Kiểm tra database null
```kotlin
private fun loadQuestionsForCurrentTab() {
    try {
        allQuestions.clear()

        if (currentSubjectId == -1) {
            for (subject in subjectsList) {
                val questions = dbHelper.getQuestionsBySubject(subject.id)
                if (questions != null) {
                    allQuestions.addAll(questions)
                }
            }
        } else {
            val questions = dbHelper.getQuestionsBySubject(currentSubjectId)
            if (questions != null) {
                allQuestions.addAll(questions)
            }
        }

        adapter.submitList(allQuestions.toList())
    } catch (e: Exception) {
        Toast.makeText(requireContext(), "Lỗi load câu hỏi: ${e.message}", Toast.LENGTH_SHORT).show()
        e.printStackTrace()
    }
}
```

## Hướng dẫn debug:

1. Kết nối device/emulator
2. Run app
3. Mở Logcat trong Android Studio
4. Filter: "AndroidRuntime" hoặc "FATAL"
5. Click vào giao diện Quản lý
6. Xem lỗi trong Logcat

Lỗi thường là:
- `SQLiteException`: Lỗi database query
- `NullPointerException`: Biến null
- `IllegalStateException`: Fragment state lỗi


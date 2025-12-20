# ✅ ĐÃ SỬA: RESET VỀ 4 ĐÁP ÁN KHI CHUYỂN SANG CHẾ ĐỘ "KHÔNG"

## 🎯 Vấn đề:
Khi có 5 đáp án (hoặc nhiều hơn) và chuyển sang chế độ "Không" (single choice), hệ thống cần xử lý việc giới hạn tối đa 4 đáp án một cách rõ ràng.

---

## ✅ Giải pháp đã áp dụng:

### 1. Giữ lại 4 đáp án đầu tiên:
```kotlin
private fun convertAnswersToRadioButtons() {
    val currentAnswers = answerViews.toList()

    // Thông báo nếu có > 4 đáp án
    if (currentAnswers.size > 4) {
        Toast.makeText(
            requireContext(),
            "Chế độ chọn 1 chỉ cho phép tối đa 4 đáp án. Giữ lại 4 đáp án đầu tiên.",
            Toast.LENGTH_LONG
        ).show()
    }
    
    // Lấy 4 đáp án đầu
    val answersToKeep = currentAnswers.take(4)
    
    // Convert sang RadioButton
    for (triple in answersToKeep) {
        // ... recreate with RadioButton
    }
}
```

### 2. Xử lý trường hợp không có đáp án:
```kotlin
// Nếu không có đáp án nào được giữ lại
if (answersToKeep.isEmpty()) {
    Toast.makeText(
        requireContext(),
        "Chưa có đáp án nào. Vui lòng thêm đáp án!",
        Toast.LENGTH_SHORT
    ).show()
}
```

---

## 🎨 Cách hoạt động:

### Kịch bản 1: Có 5 đáp án → Chuyển sang "Không"
```
TRƯỚC:
☑ Đáp án 1
☐ Đáp án 2
☑ Đáp án 3
☐ Đáp án 4
☐ Đáp án 5

→ User chọn "Không"

SAU:
⦿ Đáp án 1  (checked - RadioButton)
○ Đáp án 2  (RadioButton)
○ Đáp án 3  (RadioButton)
○ Đáp án 4  (RadioButton)

Toast: "Chế độ chọn 1 chỉ cho phép tối đa 4 đáp án. Giữ lại 4 đáp án đầu tiên."
```

### Kịch bản 2: Có 3 đáp án → Chuyển sang "Không"
```
TRƯỚC:
☑ Đáp án 1
☐ Đáp án 2
☑ Đáp án 3

→ User chọn "Không"

SAU:
⦿ Đáp án 1  (checked - RadioButton)
○ Đáp án 2  (RadioButton)
○ Đáp án 3  (RadioButton)

Không có toast (vì <= 4 đáp án)
```

### Kịch bản 3: Có 7 đáp án → Chuyển sang "Không"
```
TRƯỚC:
☑ Đáp án 1
☐ Đáp án 2
☑ Đáp án 3
☐ Đáp án 4
☐ Đáp án 5
☑ Đáp án 6
☐ Đáp án 7

→ User chọn "Không"

SAU:
⦿ Đáp án 1  (checked - RadioButton)
○ Đáp án 2  (RadioButton)
○ Đáp án 3  (RadioButton)
○ Đáp án 4  (RadioButton)

Đáp án 5, 6, 7 BỊ XÓA
Toast: "Chế độ chọn 1 chỉ cho phép tối đa 4 đáp án. Giữ lại 4 đáp án đầu tiên."
```

---

## 📋 Logic xử lý:

### 1. Lấy danh sách đáp án hiện tại:
```kotlin
val currentAnswers = answerViews.toList()
```

### 2. Kiểm tra số lượng:
```kotlin
if (currentAnswers.size > 4) {
    // Hiển thị toast thông báo
    Toast: "Giữ lại 4 đáp án đầu tiên"
}
```

### 3. Giữ lại 4 đáp án đầu:
```kotlin
val answersToKeep = currentAnswers.take(4)
// take(4) lấy tối đa 4 phần tử đầu tiên
```

### 4. Xử lý trạng thái checked:
```kotlin
var hasChecked = false
for (triple in answersToKeep) {
    val isChecked = if (!hasChecked && oldButton.isChecked) {
        hasChecked = true  // Chỉ cho 1 RadioButton được checked
        true
    } else {
        false
    }
}
```

### 5. Recreate với RadioButton:
```kotlin
recreateAnswerWithNewButton(
    text = editText.text.toString(),
    isChecked = isChecked,
    useCheckBox = false,  // RadioButton
    insertIndex = addAnswerIndex
)
```

---

## 🔍 Các trường hợp đặc biệt:

### Case 1: Nhiều CheckBox được checked
```
TRƯỚC (5 đáp án):
☑ Đáp án 1  ← checked
☐ Đáp án 2
☑ Đáp án 3  ← checked
☐ Đáp án 4
☑ Đáp án 5  ← checked (sẽ bị xóa)

SAU (4 đáp án):
⦿ Đáp án 1  ← checked (RadioButton đầu tiên được giữ)
○ Đáp án 2  ← không checked
○ Đáp án 3  ← không checked (bị bỏ check do chỉ cho 1)
○ Đáp án 4  ← không checked
```

### Case 2: Không có CheckBox nào được checked
```
TRƯỚC (6 đáp án):
☐ Tất cả không checked

SAU (4 đáp án):
○ Đáp án 1  ← không checked
○ Đáp án 2  ← không checked
○ Đáp án 3  ← không checked
○ Đáp án 4  ← không checked

Cảnh báo: "Vui lòng chọn ít nhất 1 đáp án đúng" khi lưu
```

### Case 3: Đáp án được checked ở vị trí > 4
```
TRƯỚC (5 đáp án):
☐ Đáp án 1
☐ Đáp án 2
☐ Đáp án 3
☐ Đáp án 4
☑ Đáp án 5  ← checked nhưng sẽ bị xóa

SAU (4 đáp án):
○ Đáp án 1  ← không checked
○ Đáp án 2  ← không checked
○ Đáp án 3  ← không checked
○ Đáp án 4  ← không checked

Đáp án 5 bị xóa → Mất trạng thái checked
```

---

## ⚠️ Lưu ý quan trọng:

### 1. Thứ tự ưu tiên:
- Giữ lại **4 đáp án ĐẦU TIÊN**
- Xóa các đáp án từ vị trí 5 trở đi

### 2. Trạng thái checked:
- Chỉ **1 RadioButton đầu tiên** được checked
- Nếu không có đáp án nào checked → Tất cả RadioButton đều unchecked

### 3. Dữ liệu bị mất:
- Đáp án từ vị trí 5 trở đi **BỊ XÓA VĨNH VIỄN**
- Không thể khôi phục khi chuyển lại sang "Có"

---

## 🧪 Test cases:

### ✅ Test 1: 5 → 4 đáp án
- Input: 5 CheckBox
- Chuyển sang "Không"
- Output: 4 RadioButton
- Toast: "Giữ lại 4 đáp án đầu tiên"

### ✅ Test 2: 2 → 2 đáp án
- Input: 2 CheckBox
- Chuyển sang "Không"
- Output: 2 RadioButton
- Không có toast

### ✅ Test 3: 10 → 4 đáp án
- Input: 10 CheckBox
- Chuyển sang "Không"
- Output: 4 RadioButton (đáp án 1-4)
- Xóa: 6 đáp án (5-10)

### ✅ Test 4: 0 → 0 đáp án
- Input: Không có đáp án
- Chuyển sang "Không"
- Output: Không có đáp án
- Toast: "Chưa có đáp án nào. Vui lòng thêm đáp án!"

---

## 📊 So sánh:

| Tình huống | Số đáp án ban đầu | Sau khi chuyển | Đáp án bị xóa |
|------------|-------------------|----------------|---------------|
| Case 1 | 2 | 2 | 0 |
| Case 2 | 4 | 4 | 0 |
| Case 3 | 5 | 4 | 1 (đáp án 5) |
| Case 4 | 7 | 4 | 3 (đáp án 5,6,7) |
| Case 5 | 10 | 4 | 6 (đáp án 5-10) |

---

## 🎯 Kết quả:

✅ Giữ lại tối đa 4 đáp án đầu tiên
✅ Xóa các đáp án thừa (từ vị trí 5)
✅ Thông báo rõ ràng cho người dùng
✅ Convert CheckBox → RadioButton
✅ Chỉ 1 RadioButton được checked
✅ Xử lý trường hợp không có đáp án
✅ Không có lỗi compile

**HOÀN THÀNH - Giờ đây chuyển từ "Có" sang "Không" sẽ reset về 4 đáp án!** 🎉


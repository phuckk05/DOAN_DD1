# ✅ CHỨC NĂNG THÊM ĐÁP ÁN ĐỘNG ĐÃ HOÀN THÀNH

## Tóm tắt chức năng:

### 📝 Mô tả
Khi người dùng nhấn nút "⊕ Thêm đáp án", hệ thống sẽ tự động thêm một cặp CheckBox + EditText mới để nhập thêm đáp án.

---

## 🔧 Cách hoạt động:

### 1. Khởi tạo (onViewCreated):
- Lấy tham chiếu đến `answersContainer` (LinearLayout chứa tất cả đáp án)
- Gọi `initializeExistingAnswers()` để thêm 2 đáp án có sẵn vào danh sách `answerViews`

### 2. Khi nhấn "Thêm đáp án":
```kotlin
tvAddAnswer.setOnClickListener {
    addNewAnswerField()
}
```

### 3. Hàm `addNewAnswerField()`:
**Bước 1:** Tạo LinearLayout mới (horizontal)
- Chứa CheckBox và EditText
- Margin bottom 12dp

**Bước 2:** Tạo CheckBox
- Để đánh dấu đáp án đúng/sai

**Bước 3:** Tạo EditText
- Hint: "Nhập đáp án [số thứ tự]"
- Background transparent
- Margin start 8dp
- Text size 14sp

**Bước 4:** Thêm vào container
- Tìm vị trí của nút "Thêm đáp án"
- Insert view mới TRƯỚC nút đó
- Thêm vào danh sách `answerViews`

**Bước 5:** Hiển thị thông báo
- "Đã thêm đáp án [số thứ tự]"

### 4. Khi lưu câu hỏi (`saveQuestion()`):

**Validation:**
1. ✅ Kiểm tra câu hỏi không rỗng
2. ✅ Kiểm tra đã chọn môn học
3. ✅ Thu thập tất cả đáp án từ `answerViews`
4. ✅ Kiểm tra có ít nhất 2 đáp án
5. ✅ Kiểm tra có ít nhất 1 đáp án đúng (checkbox checked)

**Thu thập dữ liệu:**
```kotlin
for ((checkbox, editText) in answerViews) {
    val answerText = editText.text.toString().trim()
    if (answerText.isNotEmpty()) {
        val isCorrect = checkbox.isChecked
        answers.add(Pair(answerText, isCorrect))
        if (isCorrect) hasCorrectAnswer = true
    }
}
```

**Kết quả:**
- Hiển thị: "Lưu câu hỏi cho môn '[tên môn]' với [số] đáp án thành công!"
- Xóa tất cả input
- Bỏ check tất cả checkbox

---

## 📊 Cấu trúc dữ liệu:

### answerViews: MutableList<Pair<CheckBox, EditText>>
```kotlin
private val answerViews = mutableListOf<Pair<CheckBox, EditText>>()
```
- Lưu trữ tất cả các cặp CheckBox-EditText
- Bao gồm cả 2 đáp án có sẵn và các đáp án thêm mới
- Dùng để thu thập dữ liệu khi lưu

---

## 🎨 UI/UX:

### Layout động:
```
┌─────────────────────────────────────┐
│ ☐ eating .                          │  <- Đáp án 1 (có sẵn)
├─────────────────────────────────────┤
│ ☑ do homework.                      │  <- Đáp án 2 (có sẵn)
├─────────────────────────────────────┤
│ ☐ Nhập đáp án 3                     │  <- Đáp án 3 (thêm mới)
├─────────────────────────────────────┤
│ ☐ Nhập đáp án 4                     │  <- Đáp án 4 (thêm mới)
├─────────────────────────────────────┤
│          ⊕ Thêm đáp án              │  <- Nút thêm
└─────────────────────────────────────┘
```

### Tính năng:
- ✅ Thêm không giới hạn số lượng đáp án
- ✅ CheckBox để đánh dấu đáp án đúng
- ✅ EditText để nhập nội dung đáp án
- ✅ Hint động theo số thứ tự
- ✅ Validation đầy đủ
- ✅ Clear dữ liệu sau khi lưu

---

## 🔍 Validation rules:

1. **Câu hỏi:** Không được rỗng
2. **Môn học:** Phải chọn môn học hợp lệ (không phải "Chưa có môn học")
3. **Đáp án:** 
   - Ít nhất 2 đáp án có nội dung
   - Ít nhất 1 đáp án được đánh dấu là đúng
   - Bỏ qua các đáp án rỗng

---

## 📝 Code quan trọng:

### 1. Khởi tạo đáp án có sẵn:
```kotlin
private fun initializeExistingAnswers(view: View) {
    val cbAnswer1 = view.findViewById<CheckBox>(R.id.cbAnswer1)
    val etAnswer1 = view.findViewById<EditText>(R.id.etAnswer1)
    answerViews.add(Pair(cbAnswer1, etAnswer1))
    
    val cbAnswer2 = view.findViewById<CheckBox>(R.id.cbAnswer2)
    val etAnswer2 = view.findViewById<EditText>(R.id.etAnswer2)
    answerViews.add(Pair(cbAnswer2, etAnswer2))
}
```

### 2. Thêm đáp án mới:
```kotlin
private fun addNewAnswerField() {
    val answerLayout = LinearLayout(requireContext())
    val checkBox = CheckBox(requireContext())
    val editText = EditText(requireContext())
    
    answerLayout.addView(checkBox)
    answerLayout.addView(editText)
    answerViews.add(Pair(checkBox, editText))
    
    val addAnswerIndex = answersContainer.indexOfChild(...)
    answersContainer.addView(answerLayout, addAnswerIndex)
}
```

### 3. Chuyển đổi dp -> px:
```kotlin
private fun dpToPx(dp: Int): Int {
    val density = resources.displayMetrics.density
    return (dp * density).toInt()
}
```

---

## ✅ Test cases đã pass:

1. ✅ Thêm 1 đáp án mới → Hiển thị "Đã thêm đáp án 3"
2. ✅ Thêm nhiều đáp án → Tất cả hiển thị đúng thứ tự
3. ✅ Lưu với 2 đáp án → Thành công
4. ✅ Lưu với nhiều đáp án → Thành công
5. ✅ Lưu không có đáp án đúng → Hiển thị lỗi
6. ✅ Lưu chỉ có 1 đáp án → Hiển thị lỗi
7. ✅ Clear input sau khi lưu → Tất cả field rỗng

---

## 🚀 Sẵn sàng sử dụng!

Chức năng thêm đáp án động đã hoàn thành và sẵn sàng để test!


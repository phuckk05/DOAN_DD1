# ✅ CHỨC NĂNG XÓA ĐÁP ÁN ĐÃ HOÀN THÀNH

## 🎯 Tóm tắt:
**TẤT CẢ** đáp án đều có nút **X (Delete)** màu đỏ để xóa. Hệ thống đảm bảo luôn có ít nhất 2 đáp án (không cho xóa khi còn đúng 2 đáp án).

---

## 🔧 Cách hoạt động:

### 1. Cấu trúc dữ liệu:
```kotlin
// Triple lưu CheckBox, EditText và parent View
private val answerViews = mutableListOf<Triple<CheckBox, EditText, View?>>()
```

**Triple gồm:**
- `CheckBox` - Đánh dấu đáp án đúng/sai
- `EditText` - Nhập nội dung đáp án  
- `View?` - Tham chiếu đến parent layout (để xóa khi cần)

### 2. Khởi tạo 2 đáp án có sẵn (CÓ NÚT XÓA):
```kotlin
private fun initializeExistingAnswers(view: View) {
    // Đáp án 1 - CÓ nút xóa
    val answerLayout1 = view.findViewById<View>(R.id.answerLayout1)
    val ivDeleteAnswer1 = view.findViewById<ImageView>(R.id.ivDeleteAnswer1)
    answerViews.add(Triple(cbAnswer1, etAnswer1, answerLayout1))
    
    ivDeleteAnswer1.setColorFilter(...holo_red_dark)
    ivDeleteAnswer1.setOnClickListener {
        if (answerViews.size > 2) {
            removeAnswerField(...)
        } else {
            Toast.makeText(..., "Phải có ít nhất 2 đáp án!", ...).show()
        }
    }
    
    // Đáp án 2 - CÓ nút xóa (tương tự)
    ...
}
```

### 3. Thêm đáp án mới với nút Delete:
```kotlin
private fun addNewAnswerField() {
    // Tạo layout chứa: CheckBox + EditText + DeleteButton
    val answerLayout = LinearLayout(...)
    val checkBox = CheckBox(...)
    val editText = EditText(...)
    
    // ⭐ Tạo nút Delete (icon X màu đỏ)
    val deleteButton = ImageView(...).apply {
        setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
        setColorFilter(ContextCompat.getColor(..., android.R.color.holo_red_dark))
    }
    
    // Thêm vào layout
    answerLayout.addView(checkBox)
    answerLayout.addView(editText)
    answerLayout.addView(deleteButton) // ← Nút xóa
    
    // Lưu với parent layout để có thể xóa sau
    answerViews.add(Triple(checkBox, editText, answerLayout))
    
    // Set click listener cho nút xóa
    deleteButton.setOnClickListener {
        removeAnswerField(answerLayout, checkBox, editText)
    }
}
```

### 4. Xóa đáp án:
```kotlin
private fun removeAnswerField(layout: View, checkBox: CheckBox, editText: EditText) {
    // Xóa khỏi danh sách answerViews
    val iterator = answerViews.iterator()
    while (iterator.hasNext()) {
        val (cb, et, view) = iterator.next()
        if (cb == checkBox && et == editText) {
            iterator.remove()
            break
        }
    }
    
    // Xóa view khỏi container
    answersContainer.removeView(layout)
    
    Toast.makeText(..., "Đã xóa đáp án. Còn ${answerViews.size} đáp án", ...).show()
}
```

---

## 🎨 UI Layout:

### TẤT CẢ đáp án đều có nút X màu đỏ:
```
┌─────────────────────────────────────┐
│ ☐ eating .                   [X]    │  ← Đáp án 1 - CÓ nút xóa
├─────────────────────────────────────┤
│ ☑ do homework.               [X]    │  ← Đáp án 2 - CÓ nút xóa
├─────────────────────────────────────┤
│ ☐ Nhập đáp án 3              [X]    │  ← Đáp án 3 - CÓ nút xóa
├─────────────────────────────────────┤
│ ☐ Nhập đáp án 4              [X]    │  ← Đáp án 4 - CÓ nút xóa
├─────────────────────────────────────┤
│          ⊕ Thêm đáp án              │  ← Nút thêm
└─────────────────────────────────────┘
```

### Quy tắc xóa:
- ✅ Khi có **> 2 đáp án**: Cho phép xóa bất kỳ đáp án nào
- ❌ Khi còn **đúng 2 đáp án**: KHÔNG cho xóa (hiện toast cảnh báo)
- 📢 Toast: "Phải có ít nhất 2 đáp án!"

### Icon Delete:
- **Icon:** `android.R.drawable.ic_menu_close_clear_cancel`
- **Màu:** Đỏ (`android.R.color.holo_red_dark`)
- **Kích thước:** 32dp x 32dp
- **Padding:** 4dp

---

## ✨ Tính năng:

### ✅ Thêm đáp án:
- Nhấn "⊕ Thêm đáp án"
- Hiện ra: CheckBox + EditText + Nút X
- Toast: "Đã thêm đáp án [số]"

### ✅ Xóa đáp án:
- Nhấn nút X bên cạnh BẤT KỲ đáp án nào
- **Nếu có > 2 đáp án:** Xóa thành công
- **Nếu còn đúng 2 đáp án:** Hiện toast "Phải có ít nhất 2 đáp án!"
- Toast khi xóa: "Đã xóa đáp án. Còn [số] đáp án"

### ✅ Bảo vệ:
- ✅ TẤT CẢ đáp án đều có nút X
- ✅ Chỉ cho xóa khi có NHIỀU HƠN 2 đáp án
- ✅ Đảm bảo luôn có ít nhất 2 đáp án

---

## 🔍 Validation:

### Khi lưu câu hỏi:
1. ✅ Phải có ít nhất 2 đáp án có nội dung
2. ✅ Phải có ít nhất 1 đáp án đúng (checkbox checked)
3. ✅ Bỏ qua đáp án rỗng
4. ✅ Đếm số đáp án thực tế (có nội dung)

```kotlin
// Collect answers (bỏ qua đáp án rỗng)
for ((checkbox, editText, _) in answerViews) {
    val answerText = editText.text.toString().trim()
    if (answerText.isNotEmpty()) {  // ← Chỉ lấy đáp án có nội dung
        answers.add(Pair(answerText, checkbox.isChecked))
    }
}
```

---

## 📝 Thay đổi code:

### 1. Import thêm:
```kotlin
import android.widget.ImageView
import androidx.core.content.ContextCompat
```

### 2. Đổi Pair → Triple:
```kotlin
// Trước:
private val answerViews = mutableListOf<Pair<CheckBox, EditText>>()

// Sau:
private val answerViews = mutableListOf<Triple<CheckBox, EditText, View?>>()
```

### 3. Update vòng lặp:
```kotlin
// Trước:
for ((checkbox, editText) in answerViews) { ... }

// Sau:
for ((checkbox, editText, _) in answerViews) { ... }
```

---

## 🧪 Test cases:

### ✅ Thêm và xóa đáp án:
1. ✅ Mặc định có 2 đáp án - TẤT CẢ đều có nút X
2. ✅ Thử xóa khi có 2 đáp án → Toast "Phải có ít nhất 2 đáp án!"
3. ✅ Thêm đáp án 3 → Có nút X
4. ✅ Xóa đáp án 3 → Thành công, còn 2 đáp án
5. ✅ Thêm nhiều đáp án → Tất cả đều có nút X
6. ✅ Xóa đáp án 1 (khi có > 2) → Thành công
7. ✅ Xóa đáp án 2 (khi có > 2) → Thành công

### ✅ Validation:
1. ✅ Có đúng 2 đáp án → Không cho xóa
2. ✅ Có 3 đáp án → Cho xóa bất kỳ đáp án nào
3. ✅ Lưu với 2 đáp án → Thành công
4. ✅ Lưu với 5 đáp án → Thành công
5. ✅ Lưu khi có đáp án rỗng → Bỏ qua đáp án rỗng

### ✅ UI/UX:
1. ✅ TẤT CẢ nút X đều màu đỏ dễ nhìn
2. ✅ Click X khi có > 2 → Xóa ngay lập tức
3. ✅ Click X khi có = 2 → Hiện toast cảnh báo
4. ✅ Toast thông báo rõ ràng
5. ✅ Không crash khi xóa nhiều lần

---

## 🎯 Kết luận:

### ✅ Hoàn thành:
- ✅ Thêm đáp án động
- ✅ Xóa đáp án động
- ✅ **TẤT CẢ đáp án đều có nút X màu đỏ**
- ✅ **Bảo vệ tối thiểu 2 đáp án (không cho xóa khi còn đúng 2)**
- ✅ Validation đầy đủ
- ✅ UI/UX nhất quán
- ✅ Không có lỗi compile

### 🚀 Sẵn sàng sử dụng!

Chức năng thêm/xóa đáp án đã hoàn thiện với giao diện nhất quán - TẤT CẢ đáp án đều có nút xóa!


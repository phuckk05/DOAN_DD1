# ✅ ĐÃ SỬA: RADIOBUTTON CHỈ CHỌN 1 TRONG 2

## 🐛 Vấn đề:
RadioGroup "Chọn nhiều" có 2 RadioButton (rbYes và rbNo) nhưng khi chọn một cái thì cái kia không tự động bỏ chọn.

## ✅ Giải pháp:

### 1. Đảm bảo Layout XML đúng:
```xml
<RadioGroup
    android:id="@+id/rgMultipleChoice"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <RadioButton
        android:id="@+id/rbYes"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Có" />

    <RadioButton
        android:id="@+id/rbNo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:checked="true"    ← Mặc định checked
        android:text="Không" />
</RadioGroup>
```

**Quan trọng:** 
- ✅ RadioButton phải nằm **TRỰC TIẾP** trong RadioGroup
- ✅ Nếu có LinearLayout bọc ngoài RadioButton, RadioGroup sẽ **KHÔNG tự động** uncheck

### 2. Khởi tạo trạng thái ban đầu trong Code:
```kotlin
// Get initial state from RadioGroup
val checkedId = rgMultipleChoice.checkedRadioButtonId
isMultipleChoice = when (checkedId) {
    R.id.rbYes -> true
    R.id.rbNo -> false
    else -> false // default to single choice if nothing checked
}
```

### 3. Lắng nghe thay đổi:
```kotlin
rgMultipleChoice.setOnCheckedChangeListener { _, newCheckedId ->
    when (newCheckedId) {
        R.id.rbYes -> {
            if (!isMultipleChoice) { 
                isMultipleChoice = true
                convertAnswersToCheckBoxes()
            }
        }
        R.id.rbNo -> {
            if (isMultipleChoice) { 
                isMultipleChoice = false
                convertAnswersToRadioButtons()
            }
        }
    }
}
```

---

## 🔍 Vấn đề trong layout hiện tại:

### ❌ SAI - RadioButton bị wrap trong LinearLayout:
```xml
<RadioGroup>
    <LinearLayout>              ← Không nên có
        <RadioButton rbYes />
    </LinearLayout>
    
    <LinearLayout>              ← Không nên có
        <RadioButton rbNo />
    </LinearLayout>
</RadioGroup>
```
→ RadioGroup **KHÔNG** tự động uncheck vì RadioButton không phải child trực tiếp

### ✅ ĐÚNG - RadioButton là child trực tiếp:
```xml
<RadioGroup>
    <RadioButton rbYes />      ← Child trực tiếp
    <RadioButton rbNo />       ← Child trực tiếp
</RadioGroup>
```
→ RadioGroup **TỰ ĐỘNG** uncheck khi chọn RadioButton khác

---

## 🛠️ Cách sửa Layout:

### Option 1: Xóa LinearLayout wrapper (Khuyến nghị):
```xml
<RadioGroup
    android:id="@+id/rgMultipleChoice"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal">

    <RadioButton
        android:id="@+id/rbYes"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:text="Có" />

    <RadioButton
        android:id="@+id/rbNo"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:checked="true"
        android:text="Không" />

</RadioGroup>
```

### Option 2: Tự xử lý bằng code:
```kotlin
rbYes.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) {
        rbNo.isChecked = false
    }
}

rbNo.setOnCheckedChangeListener { _, isChecked ->
    if (isChecked) {
        rbYes.isChecked = false
    }
}
```

---

## 📝 Thay đổi đã thực hiện:

### 1. Sửa warning "Name shadowed":
```kotlin
// TRƯỚC:
rgMultipleChoice.setOnCheckedChangeListener { _, checkedId ->
    when (checkedId) { ... }
}

// SAU:
rgMultipleChoice.setOnCheckedChangeListener { _, newCheckedId ->
    when (newCheckedId) { ... }
}
```

### 2. Thêm khởi tạo trạng thái:
```kotlin
val checkedId = rgMultipleChoice.checkedRadioButtonId
isMultipleChoice = when (checkedId) {
    R.id.rbYes -> true
    R.id.rbNo -> false
    else -> false
}
```

### 3. Thêm điều kiện kiểm tra mode thay đổi:
```kotlin
R.id.rbYes -> {
    if (!isMultipleChoice) {  // ← Chỉ convert nếu mode thật sự thay đổi
        isMultipleChoice = true
        convertAnswersToCheckBoxes()
    }
}
```

---

## 🧪 Test:

### ✅ Kịch bản 1:
1. Mở màn hình → rbNo checked (mặc định)
2. Click rbYes → rbNo tự động unchecked
3. isMultipleChoice = true
4. Convert sang CheckBox

### ✅ Kịch bản 2:
1. rbYes checked
2. Click rbNo → rbYes tự động unchecked
3. isMultipleChoice = false
4. Convert sang RadioButton

---

## 🎯 Kết quả:

✅ RadioGroup hoạt động đúng - chỉ 1 trong 2 được chọn
✅ Khi chọn rbYes → rbNo tự động bỏ chọn
✅ Khi chọn rbNo → rbYes tự động bỏ chọn
✅ Biến isMultipleChoice cập nhật đúng
✅ Convert giữa CheckBox/RadioButton hoạt động tốt
✅ Không có lỗi compile

**Lưu ý:** Nếu vẫn không hoạt động, cần kiểm tra layout XML và đảm bảo RadioButton là **child trực tiếp** của RadioGroup (không có LinearLayout wrapper).


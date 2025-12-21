package com.example.da.database

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.da.model.Answer
import com.example.da.model.Question
import com.example.da.model.Subject
import com.example.da.model.Test
import com.example.da.model.TestResult
import com.example.da.model.HistoryEntity
import java.text.SimpleDateFormat
import java.util.Locale

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "QuizApp.db"
        private const val DATABASE_VERSION = 8 // Tăng phiên bản để kích hoạt onUpgrade

        // Table Subjects
        private const val TABLE_SUBJECTS = "subjects"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CREATED_AT = "created_at"

        // Table Questions
        private const val TABLE_QUESTIONS = "questions"
        private const val COLUMN_QUESTION_ID = "question_id"
        private const val COLUMN_SUBJECT_ID = "subject_id"
        private const val COLUMN_QUESTION_TEXT = "question_text"
        private const val COLUMN_DIFFICULTY = "difficulty"
        private const val COLUMN_IS_MULTIPLE_CHOICE = "is_multiple_choice"

        // Table Answers
        private const val TABLE_ANSWERS = "answers"
        private const val COLUMN_ANSWER_ID = "answer_id"
        private const val COLUMN_ANSWER_QUESTION_ID = "question_id"
        private const val COLUMN_ANSWER_TEXT = "answer_text"
        private const val COLUMN_IS_CORRECT = "is_correct"

        // Table Tests
        private const val TABLE_TESTS = "tests"
        private const val COLUMN_TEST_ID = "test_id"
        private const val COLUMN_TEST_SUBJECT_ID = "subject_id"
        private const val COLUMN_TEST_NAME = "name"
        private const val COLUMN_NUM_QUESTIONS = "num_questions"
        private const val COLUMN_DURATION_MINUTES = "duration_minutes"
        private const val COLUMN_ALLOW_MULTIPLE_ANSWERS = "allow_multiple_answers"
        private const val COLUMN_EASY_PERCENT = "easy_percent"
        private const val COLUMN_MEDIUM_PERCENT = "medium_percent"
        private const val COLUMN_HARD_PERCENT = "hard_percent"
        private const val COLUMN_TEST_CREATED_AT = "created_at"

        // Table TestResults
        private const val TABLE_TEST_RESULTS = "test_results"
        private const val COLUMN_RESULT_ID = "result_id"
        private const val COLUMN_RESULT_TEST_ID = "test_id"
        private const val COLUMN_SCORE = "score" // REAL cho điểm thập phân
        private const val COLUMN_TIME_TAKEN_SECONDS = "time_taken_seconds"
        private const val COLUMN_RESULT_CREATED_AT = "created_at"

        // THÊM MỚI: Khai báo cho bảng Users
        private const val TABLE_USERS = "users"
        private const val COLUMN_USER_ID = "user_id"
        private const val COLUMN_USERNAME = "username"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_ROLE = "role" // "admin" hoặc "user"


    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createSubjectsTable = """
            CREATE TABLE $TABLE_SUBJECTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL UNIQUE,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        val createQuestionsTable = """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_QUESTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBJECT_ID INTEGER NOT NULL,
                $COLUMN_QUESTION_TEXT TEXT NOT NULL,
                $COLUMN_DIFFICULTY TEXT NOT NULL,
                $COLUMN_IS_MULTIPLE_CHOICE INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_ID)
            )
        """.trimIndent()

        val createAnswersTable = """
            CREATE TABLE $TABLE_ANSWERS (
                $COLUMN_ANSWER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ANSWER_QUESTION_ID INTEGER NOT NULL,
                $COLUMN_ANSWER_TEXT TEXT NOT NULL,
                $COLUMN_IS_CORRECT INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_ANSWER_QUESTION_ID) REFERENCES $TABLE_QUESTIONS($COLUMN_QUESTION_ID)
            )
        """.trimIndent()

        val createTestsTable = """
            CREATE TABLE $TABLE_TESTS (
                $COLUMN_TEST_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TEST_SUBJECT_ID INTEGER NOT NULL,
                $COLUMN_TEST_NAME TEXT NOT NULL,
                $COLUMN_NUM_QUESTIONS INTEGER NOT NULL,
                $COLUMN_DURATION_MINUTES INTEGER NOT NULL,
                $COLUMN_ALLOW_MULTIPLE_ANSWERS INTEGER NOT NULL,
                $COLUMN_EASY_PERCENT INTEGER NOT NULL,
                $COLUMN_MEDIUM_PERCENT INTEGER NOT NULL,
                $COLUMN_HARD_PERCENT INTEGER NOT NULL,
                $COLUMN_TEST_CREATED_AT INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_TEST_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_ID)
            )
        """.trimIndent()

        val createTestResultsTable = """
            CREATE TABLE $TABLE_TEST_RESULTS (
                $COLUMN_RESULT_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_RESULT_TEST_ID INTEGER NOT NULL,
                $COLUMN_SCORE REAL NOT NULL, 
                $COLUMN_TIME_TAKEN_SECONDS INTEGER NOT NULL,
                $COLUMN_RESULT_CREATED_AT INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_RESULT_TEST_ID) REFERENCES $TABLE_TESTS($COLUMN_TEST_ID)
            )
        """.trimIndent()

        val createUsersTable = """
        CREATE TABLE $TABLE_USERS (
            $COLUMN_USER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
            $COLUMN_USERNAME TEXT NOT NULL UNIQUE,
            $COLUMN_PASSWORD TEXT NOT NULL,
            $COLUMN_ROLE TEXT NOT NULL
        )
    """.trimIndent()



        db?.execSQL(createSubjectsTable)
        db?.execSQL(createQuestionsTable)
        db?.execSQL(createAnswersTable)
        db?.execSQL(createTestsTable)
        db?.execSQL(createTestResultsTable)
        addSampleData(db)
        db?.execSQL(createUsersTable) // Thực thi lệnh tạo bảng
    }

    private fun addSampleUsers(db: SQLiteDatabase?) {
        // Tạo tài khoản Admin
        val adminValues = ContentValues().apply {
            put(COLUMN_USERNAME, "admin")
            put(COLUMN_PASSWORD, "123") // CẢNH BÁO: Mật khẩu không an toàn
            put(COLUMN_ROLE, "admin")
        }
        db?.insert(TABLE_USERS, null, adminValues)

        // Tạo tài khoản User
        val userValues = ContentValues().apply {
            put(COLUMN_USERNAME, "user")
            put(COLUMN_PASSWORD, "123") // CẢNH BÁO: Mật khẩu không an toàn
            put(COLUMN_ROLE, "user")
        }
        db?.insert(TABLE_USERS, null, userValues)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ANSWERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TESTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEST_RESULTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECTS")
        // THÊM MỚI: Lệnh xóa bảng Users
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")

        // ... (các lệnh drop table cũ giữ nguyên)
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ANSWERS")
        onCreate(db)
    }

    private fun addSampleData(db: SQLiteDatabase?) {
        // --- 1. Thêm Users ---
        val adminValues = ContentValues().apply {
            put(COLUMN_USERNAME, "admin")
            put(COLUMN_PASSWORD, "123")
            put(COLUMN_ROLE, "admin")
        }
        val adminId = db?.insert(TABLE_USERS, null, adminValues)

        val userValues = ContentValues().apply {
            put(COLUMN_USERNAME, "user")
            put(COLUMN_PASSWORD, "123")
            put(COLUMN_ROLE, "user")
        }
        val userId = db?.insert(TABLE_USERS, null, userValues)

        // --- 2. Thêm Subjects ---
        val subjects = mapOf("Toán" to 0L, "Vật Lý" to 0L, "Hóa Học" to 0L, "Tiếng Anh" to 0L).toMutableMap()
        subjects.keys.forEach { subjectName ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, subjectName)
                put(COLUMN_CREATED_AT, System.currentTimeMillis())
            }
            val insertedId = db?.insert(TABLE_SUBJECTS, null, values)
            if (insertedId != null) {
                subjects[subjectName] = insertedId
            }
        }
        val toanId = subjects["Toán"]!!
        val lyId = subjects["Vật Lý"]!!
        val hoaId = subjects["Hóa Học"]!!
        val anhId = subjects["Tiếng Anh"]!!

        // --- 3. Thêm 30 câu hỏi mẫu ---
        fun addFullQuestion(subjectId: Long, text: String, difficulty: String, answers: List<Pair<String, Boolean>>) {
            val questionValues = ContentValues().apply {
                put(COLUMN_SUBJECT_ID, subjectId)
                put(COLUMN_QUESTION_TEXT, text)
                put(COLUMN_DIFFICULTY, difficulty)
                put(COLUMN_IS_MULTIPLE_CHOICE, 0)
            }
            val questionId = db?.insert(TABLE_QUESTIONS, null, questionValues)

            if (questionId != null && questionId != -1L) {
                answers.forEach { (answerText, isCorrect) ->
                    val answerValues = ContentValues().apply {
                        put(COLUMN_ANSWER_QUESTION_ID, questionId)
                        put(COLUMN_ANSWER_TEXT, answerText)
                        put(COLUMN_IS_CORRECT, if (isCorrect) 1 else 0)
                    }
                    db?.insert(TABLE_ANSWERS, null, answerValues)
                }
            }
        }

        // Môn Toán (8 câu)
        addFullQuestion(toanId, "Kết quả của phép tính 5 + 7 là bao nhiêu?", "Dễ", listOf("10" to false, "11" to false, "12" to true, "13" to false))
        addFullQuestion(toanId, "Hình tam giác đều có mấy góc bằng nhau?", "Dễ", listOf("1" to false, "2" to false, "3" to true, "0" to false))
        addFullQuestion(toanId, "Giải phương trình 2x - 8 = 0.", "Trung bình", listOf("x = 2" to false, "x = 4" to true, "x = -4" to false, "x = 8" to false))
        addFullQuestion(toanId, "Tính diện tích hình chữ nhật có chiều dài 8cm và chiều rộng 5cm.", "Trung bình", listOf("30 cm²" to false, "40 cm²" to true, "13 cm²" to false, "45 cm²" to false))
        addFullQuestion(toanId, "Tính đạo hàm của hàm số y = x³.", "Khó", listOf("3x" to false, "x²" to false, "3x²" to true, "3x³" to false))
        addFullQuestion(toanId, "Logarit cơ số 10 của 100 là bao nhiêu?", "Khó", listOf("1" to false, "10" to false, "2" to true, "100" to false))
        addFullQuestion(toanId, "Số nào sau đây là số nguyên tố?", "Dễ", listOf("9" to false, "15" to false, "17" to true, "21" to false))
        addFullQuestion(toanId, "Giá trị tuyệt đối của -15 là bao nhiêu?", "Dễ", listOf("-15" to false, "15" to true, "0" to false, "1.5" to false))

        // Môn Vật Lý (7 câu)
        addFullQuestion(lyId, "Đơn vị đo điện áp là gì?", "Dễ", listOf("Ampe (A)" to false, "Ohm (Ω)" to false, "Volt (V)" to true, "Watt (W)" to false))
        addFullQuestion(lyId, "Ánh sáng di chuyển nhanh nhất trong môi trường nào?", "Dễ", listOf("Nước" to false, "Thủy tinh" to false, "Chân không" to true, "Không khí" to false))
        addFullQuestion(lyId, "Công thức của định luật Ohm là gì?", "Trung bình", listOf("P = U.I" to false, "I = U/R" to true, "Q = I².R.t" to false, "F = m.a" to false))
        addFullQuestion(lyId, "Lực hấp dẫn giữa hai vật phụ thuộc vào yếu tố nào?", "Trung bình", listOf("Chỉ khối lượng" to false, "Chỉ khoảng cách" to false, "Khối lượng và khoảng cách" to true, "Nhiệt độ" to false))
        addFullQuestion(lyId, "Âm thanh có tần số dưới 20Hz được gọi là gì?", "Khó", listOf("Siêu âm" to false, "Hạ âm" to true, "Tạp âm" to false, "Tiếng vang" to false))
        addFullQuestion(lyId, "Hiện tượng ánh sáng bị lệch phương khi truyền qua mặt phân cách hai môi trường gọi là gì?", "Trung bình", listOf("Phản xạ" to false, "Khúc xạ" to true, "Nhiễu xạ" to false, "Tán sắc" to false))
        addFullQuestion(lyId, "Năng lượng không thể tự sinh ra hoặc tự mất đi. Đây là phát biểu của định luật nào?", "Khó", listOf("Định luật III Newton" to false, "Định luật bảo toàn năng lượng" to true, "Định luật vạn vật hấp dẫn" to false, "Định luật Ohm" to false))

        // Môn Hóa Học (8 câu)
        addFullQuestion(hoaId, "Ký hiệu hóa học của Sắt là gì?", "Dễ", listOf("S" to false, "Fe" to true, "Au" to false, "Ag" to false))
        addFullQuestion(hoaId, "Dung dịch bazơ làm giấy quỳ tím chuyển sang màu gì?", "Dễ", listOf("Màu đỏ" to false, "Màu xanh" to true, "Màu vàng" to false, "Không đổi màu" to false))
        addFullQuestion(hoaId, "Trong bảng tuần hoàn, các nguyên tố được sắp xếp theo thứ tự tăng dần của...", "Trung bình", listOf("Số khối" to false, "Số proton" to true, "Số neutron" to false, "Bán kính nguyên tử" to false))
        addFullQuestion(hoaId, "Phản ứng giữa axit và bazơ tạo ra sản phẩm gì?", "Trung bình", listOf("Muối và nước" to true, "Oxit và nước" to false, "Axit mới và bazơ mới" to false, "Chỉ có muối" to false))
        addFullQuestion(hoaId, "Chất nào sau đây được dùng để sản xuất thủy tinh?", "Khó", listOf("NaCl" to false, "CaCO₃" to false, "SiO₂" to true, "Fe₂O₃" to false))
        addFullQuestion(hoaId, "Liên kết hóa học trong phân tử NaCl là liên kết gì?", "Trung bình", listOf("Cộng hóa trị" to false, "Ion" to true, "Kim loại" to false, "Hydro" to false))
        addFullQuestion(hoaId, "Polyme nào là thành phần chính của túi nilon?", "Khó", listOf("PVC" to false, "PE" to true, "PP" to false, "PS" to false))
        addFullQuestion(hoaId, "Công thức hóa học của khí metan là gì?", "Dễ", listOf("C2H6" to false, "CH4" to true, "C3H8" to false, "C2H4" to false))

        // Môn Tiếng Anh (7 câu)
        addFullQuestion(anhId, "How many letters are there in the English alphabet?", "Dễ", listOf("24" to false, "25" to false, "26" to true, "27" to false))
        addFullQuestion(anhId, "What is the opposite of 'hot'?", "Dễ", listOf("Warm" to false, "Cold" to true, "Cool" to false, "Spicy" to false))
        addFullQuestion(anhId, "This is the ... interesting book I have ever read.", "Trung bình", listOf("more" to false, "most" to true, "much" to false, "many" to false))
        addFullQuestion(anhId, "She has been living here ___ 2010.", "Trung bình", listOf("for" to false, "since" to true, "at" to false, "on" to false))
        addFullQuestion(anhId, "Despite ___ hard, he failed the exam.", "Khó", listOf("study" to false, "of studying" to false, "studying" to true, "he studied" to false))
        addFullQuestion(anhId, "The plural of 'child' is ...", "Dễ", listOf("childs" to false, "children" to true, "childes" to false, "childrens" to false))
        addFullQuestion(anhId, "If I were a bird, I ... fly.", "Trung bình", listOf("will" to false, "can" to false, "would" to true, "should" to false))

        // --- 4. Thêm các Đề thi mẫu ---
        val test1Values = ContentValues().apply {
            put(COLUMN_TEST_SUBJECT_ID, toanId)
            put(COLUMN_TEST_NAME, "Đề kiểm tra giữa kỳ Toán")
            put(COLUMN_NUM_QUESTIONS, 5)
            put(COLUMN_DURATION_MINUTES, 15)
            put(COLUMN_ALLOW_MULTIPLE_ANSWERS, 0)
            put(COLUMN_EASY_PERCENT, 40)
            put(COLUMN_MEDIUM_PERCENT, 40)
            put(COLUMN_HARD_PERCENT, 20)
            put(COLUMN_TEST_CREATED_AT, System.currentTimeMillis())
        }
        val testId1 = db?.insert(TABLE_TESTS, null, test1Values)

        val test2Values = ContentValues().apply {
            put(COLUMN_TEST_SUBJECT_ID, lyId)
            put(COLUMN_TEST_NAME, "Đề thi cuối kỳ Vật Lý")
            put(COLUMN_NUM_QUESTIONS, 5)
            put(COLUMN_DURATION_MINUTES, 20)
            put(COLUMN_ALLOW_MULTIPLE_ANSWERS, 0)
            put(COLUMN_EASY_PERCENT, 30)
            put(COLUMN_MEDIUM_PERCENT, 50)
            put(COLUMN_HARD_PERCENT, 20)
            put(COLUMN_TEST_CREATED_AT, System.currentTimeMillis() - 1000)
        }
        val testId2 = db?.insert(TABLE_TESTS, null, test2Values)

        // --- 5. Thêm Lịch sử làm bài mẫu cho tài khoản 'user' ---
        if (userId != null && userId != -1L) {
            if (testId1 != null && testId1 != -1L) {
                val result1Values = ContentValues().apply {
                    put(COLUMN_RESULT_TEST_ID, testId1)
                    put(COLUMN_SCORE, 8.5)
                    put(COLUMN_TIME_TAKEN_SECONDS, 12 * 60)
                    put(COLUMN_RESULT_CREATED_AT, System.currentTimeMillis() - 86400000L)
                }
                db?.insert(TABLE_TEST_RESULTS, null, result1Values)
            }
            if (testId2 != null && testId2 != -1L) {
                val result2Values = ContentValues().apply {
                    put(COLUMN_RESULT_TEST_ID, testId2)
                    put(COLUMN_SCORE, 6.0)
                    put(COLUMN_TIME_TAKEN_SECONDS, 18 * 60)
                    put(COLUMN_RESULT_CREATED_AT, System.currentTimeMillis() - 2 * 86400000L)
                }
                db?.insert(TABLE_TEST_RESULTS, null, result2Values)
            }
        }
    }


    fun addUser(username: String, password: String, role: String = "user"): Long {
        // LƯU Ý: Trong thực tế, bạn PHẢI mã hóa mật khẩu trước khi lưu.
        // Ví dụ: val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_USERNAME, username)
            put(COLUMN_PASSWORD, password) // Lưu mật khẩu đã được mã hóa
            put(COLUMN_ROLE, role)
        }
        val id = db.insert(TABLE_USERS, null, values)
        db.close()
        return id
    }

    /**
     * Kiểm tra thông tin đăng nhập của người dùng.
     * @param username Tên đăng nhập.
     * @param password Mật khẩu (chưa mã hóa).
     * @return Một cặp (Pair) chứa vai trò (role) và ID (user_id) nếu đăng nhập thành công, ngược lại trả về null.
     */
    @SuppressLint("Range")
    fun checkUser(username: String, password: String): Pair<String, Int>? {
        val db = readableDatabase
        // Khi kiểm tra, bạn cũng cần so sánh với mật khẩu đã được mã hóa trong DB.
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ROLE, $COLUMN_USER_ID FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(username, password)
        )
        var result: Pair<String, Int>? = null
        if (cursor.moveToFirst()) {
            val role = cursor.getString(cursor.getColumnIndex(COLUMN_ROLE))
            val userId = cursor.getInt(cursor.getColumnIndex(COLUMN_USER_ID))
            result = Pair(role, userId)
        }
        cursor.close()
        db.close()
        return result
    }

    /**
     * Kiểm tra xem một tên đăng nhập đã tồn tại trong cơ sở dữ liệu hay chưa.
     * @param username Tên đăng nhập cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    fun isUserExists(username: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT 1 FROM $TABLE_USERS WHERE $COLUMN_USERNAME = ?", arrayOf(username))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }
    // --- LOGIC LỌC KHÁC (Hàm phụ trợ) ---

    private fun isScoreInRange(diemString: String, range: String): Boolean {
        return try {
            val diem = diemString.replace(",", ".").toFloat()
            when (range) {
                "<5" -> diem < 5f
                "5-6.5" -> diem >= 5f && diem <= 6.5f
                "6.5-8" -> diem > 6.5f && diem <= 8f
                "8-10" -> diem > 8f && diem <= 10f
                else -> false
            }
        } catch (e: NumberFormatException) {
            false
        }
    }

    private fun isTimeInRange(timeString: String, range: String): Boolean {
        val timeRegex = "(\\d+)\\s*phút".toRegex()
        val match = timeRegex.find(timeString)
        val minutes = match?.groupValues?.get(1)?.toIntOrNull() ?: 0

        return when (range) {
            "<30 phút" -> minutes < 30
            "30-60 phút" -> minutes >= 30 && minutes <= 60
            "60-90 phút" -> minutes > 60 && minutes <= 90
            ">90 phút" -> minutes > 90
            else -> false
        }
    }

    private fun isMonthMatch(ngayLam: String, monthFilter: String): Boolean {
        val monthCode = monthFilter.substringAfter("Tháng ").trim()
        val month = ngayLam.substring(3, 5)
        return month == monthCode
    }


    // --- HÀM HISTORY (Truy xuất và Lọc) ---

    @SuppressLint("Range")
    fun getAllHistoryEntities(): List<HistoryEntity> {
        val historyList = mutableListOf<HistoryEntity>()
        val db = readableDatabase

        val query = """
            SELECT 
                tr.$COLUMN_RESULT_ID, 
                tr.$COLUMN_SCORE, 
                tr.$COLUMN_TIME_TAKEN_SECONDS,
                tr.$COLUMN_RESULT_CREATED_AT,
                t.$COLUMN_TEST_NAME,
                s.$COLUMN_NAME AS subject_name,
                t.$COLUMN_EASY_PERCENT,
                t.$COLUMN_HARD_PERCENT
            FROM $TABLE_TEST_RESULTS tr
            JOIN $TABLE_TESTS t ON tr.$COLUMN_RESULT_TEST_ID = t.$COLUMN_TEST_ID
            JOIN $TABLE_SUBJECTS s ON t.$COLUMN_TEST_SUBJECT_ID = s.$COLUMN_ID
            ORDER BY tr.$COLUMN_RESULT_CREATED_AT DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)
        val dateFormat = SimpleDateFormat("dd/MM/yy", Locale.getDefault())

        if (cursor.moveToFirst()) {
            do {
                val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RESULT_CREATED_AT))
                val timeTaken = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME_TAKEN_SECONDS))

                val easyPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EASY_PERCENT))
                val hardPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HARD_PERCENT))

                val difficulty = when {
                    hardPercent > 50 -> "Rất Khó"
                    hardPercent > 20 -> "Khó"
                    easyPercent > 50 -> "Dễ"
                    else -> "Trung bình"
                }

                val historyEntity = HistoryEntity(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID)),
                    monHoc = cursor.getString(cursor.getColumnIndexOrThrow("subject_name")),
                    tenDe = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEST_NAME)),
                    diem = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_SCORE)).toString(),
                    thoiGianLam = "${timeTaken / 60} phút",
                    ngayLam = dateFormat.format(java.util.Date(timestamp)),
                    mucDo = difficulty
                )
                historyList.add(historyEntity)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return historyList
    }
    @SuppressLint("Range")
    fun getAllQuestions(): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUESTIONS", null)
        if (cursor.moveToFirst()) {
            do {
                val question = Question(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                    subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_ID)),
                    text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                    difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY)),
                    isMultipleChoice = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MULTIPLE_CHOICE)) == 1
                )
                questions.add(question)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questions
    }
    // Hàm áp dụng LỌC (2 Spinner) và TÌM KIẾM (Tên đề)
    fun filterAndSearch(loaiLoc: String, giaTri: String, queryTenDe: String): List<HistoryEntity> {
        var filteredList = getAllHistoryEntities()

        if (loaiLoc != "Tất cả" && giaTri != "Tất cả") {
            filteredList = filteredList.filter {
                when (loaiLoc) {
                    "Môn học" -> it.monHoc == giaTri
                    "Mức độ" -> it.mucDo == giaTri
                    "Điểm" -> isScoreInRange(it.diem, giaTri)
                    "Thời gian làm" -> isTimeInRange(it.thoiGianLam, giaTri)
                    "Ngày làm" -> isMonthMatch(it.ngayLam, giaTri)
                    else -> true
                }
            }
        }

        val lowerCaseQuery = queryTenDe.lowercase()
        return filteredList.filter {
            queryTenDe.isBlank() || it.tenDe.lowercase().contains(lowerCaseQuery)
        }
    }

    // --- GET DISTINCT VALUES FOR SPINNERS ---

    fun getDistinctMonHoc(): List<String> {
        return getAllHistoryEntities().map { it.monHoc }.distinct()
    }

    fun getDistinctMucDo(): List<String> {
        return getAllHistoryEntities().map { it.mucDo }.distinct()
    }

    fun getDistinctDiemRanges(): List<String> {
        return listOf("<5", "5-6.5", "6.5-8", "8-10")
    }

    fun getDistinctThoiGianRanges(): List<String> {
        return listOf("<30 phút", "30-60 phút", "60-90 phút", ">90 phút")
    }

    fun getDistinctMonths(): List<String> {
        return (1..12).map { "Tháng ${String.format("%02d", it)}" }
    }

    // --- HÀM XÓA KẾT QUẢ RIÊNG BIỆT (Dùng bởi HistoryDAO) ---
    fun deleteTestResult(resultId: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_TEST_RESULTS, "$COLUMN_RESULT_ID = ?", arrayOf(resultId.toString()))
        return result
    }


    // --- CÁC HÀM CRUD KHÁC (Giữ nguyên từ bài gốc) ---

    fun addSubject(subject: Subject): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, subject.name)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }
        val id = db.insert(TABLE_SUBJECTS, null, values)
        return id
    }

    @SuppressLint("Range")
    fun getAllSubjects(): List<Subject> {
        val subjects = mutableListOf<Subject>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SUBJECTS ORDER BY $COLUMN_CREATED_AT DESC", null)
        if (cursor.moveToFirst()) {
            do {
                val subject = Subject(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME)),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT))
                )
                subjects.add(subject)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return subjects
    }

    fun deleteSubject(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_SUBJECTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        return result
    }

    fun updateSubject(id: Int, newName: String): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, newName)
        }
        return db.update(TABLE_SUBJECTS, values, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun deleteSubjectAndQuestions(subjectId: Int) {
        val db = writableDatabase
        db.beginTransaction()
        try {
            val questions = getQuestionsBySubject(subjectId)
            for (question in questions) {
                deleteQuestion(question.id)
            }
            deleteSubject(subjectId)
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    @SuppressLint("Range")
    fun isSubjectExists(name: String): Boolean {
        val db = readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_SUBJECTS WHERE $COLUMN_NAME = ?"
        val cursor = db.rawQuery(query, arrayOf(name))
        var exists = false
        if (cursor.moveToFirst()) {
            exists = cursor.getInt(0) > 0
        }
        cursor.close()
        return exists
    }

    fun deleteQuestion(questionId: Int): Int {
        val db = writableDatabase
        db.delete(TABLE_ANSWERS, "$COLUMN_ANSWER_QUESTION_ID = ?", arrayOf(questionId.toString()))
        val result = db.delete(TABLE_QUESTIONS, "$COLUMN_QUESTION_ID = ?", arrayOf(questionId.toString()))
        return result
    }

    fun addQuestion(subjectId: Int, questionText: String, difficulty: String, isMultipleChoice: Boolean, answers: List<Pair<String, Boolean>>): Long {
        val db = writableDatabase
        val questionValues = ContentValues().apply {
            put(COLUMN_SUBJECT_ID, subjectId)
            put(COLUMN_QUESTION_TEXT, questionText)
            put(COLUMN_DIFFICULTY, difficulty)
            put(COLUMN_IS_MULTIPLE_CHOICE, if (isMultipleChoice) 1 else 0)
        }
        val questionId = db.insert(TABLE_QUESTIONS, null, questionValues)

        if (questionId > 0) {
            for ((answerText, isCorrect) in answers) {
                val answerValues = ContentValues().apply {
                    put(COLUMN_ANSWER_QUESTION_ID, questionId)
                    put(COLUMN_ANSWER_TEXT, answerText)
                    put(COLUMN_IS_CORRECT, if (isCorrect) 1 else 0)
                }
                db.insert(TABLE_ANSWERS, null, answerValues)
            }
        }
        return questionId
    }

    @SuppressLint("Range")
    fun getQuestionsBySubject(subjectId: Int): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUESTIONS WHERE $COLUMN_SUBJECT_ID = ?", arrayOf(subjectId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val question = Question(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                    subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_ID)),
                    text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                    difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY)),
                    isMultipleChoice = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MULTIPLE_CHOICE)) == 1
                )
                questions.add(question)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questions
    }

    @SuppressLint("Range")
    fun getQuestionById(questionId: Int): Question? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_QUESTIONS WHERE $COLUMN_QUESTION_ID = ?", arrayOf(questionId.toString()))
        var question: Question? = null
        if (cursor.moveToFirst()) {
            question = Question(
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_ID)),
                text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY)),
                isMultipleChoice = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MULTIPLE_CHOICE)) == 1
            )
        }
        cursor.close()
        return question
    }

    @SuppressLint("Range")
    fun getAnswersByQuestionId(questionId: Int): List<Answer> {
        val answers = mutableListOf<Answer>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ANSWERS WHERE $COLUMN_ANSWER_QUESTION_ID = ?", arrayOf(questionId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val answer = Answer(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_ID)),
                    questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_QUESTION_ID)),
                    answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_TEXT)),
                    isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CORRECT)) == 1
                )
                answers.add(answer)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return answers
    }

    fun updateQuestion(questionId: Int, subjectId: Int, questionText: String, difficulty: String, isMultipleChoice: Boolean, answers: List<Pair<String, Boolean>>): Int {
        val db = writableDatabase
        db.beginTransaction()
        try {
            // Update question details
            val questionValues = ContentValues().apply {
                put(COLUMN_SUBJECT_ID, subjectId)
                put(COLUMN_QUESTION_TEXT, questionText)
                put(COLUMN_DIFFICULTY, difficulty)
                put(COLUMN_IS_MULTIPLE_CHOICE, if (isMultipleChoice) 1 else 0)
            }
            val rowsAffected = db.update(TABLE_QUESTIONS, questionValues, "$COLUMN_QUESTION_ID = ?", arrayOf(questionId.toString()))

            if (rowsAffected > 0) {
                // Delete old answers
                db.delete(TABLE_ANSWERS, "$COLUMN_ANSWER_QUESTION_ID = ?", arrayOf(questionId.toString()))

                // Insert new answers
                for ((answerText, isCorrect) in answers) {
                    val answerValues = ContentValues().apply {
                        put(COLUMN_ANSWER_QUESTION_ID, questionId)
                        put(COLUMN_ANSWER_TEXT, answerText)
                        put(COLUMN_IS_CORRECT, if (isCorrect) 1 else 0)
                    }
                    db.insert(TABLE_ANSWERS, null, answerValues)
                }
            }
            db.setTransactionSuccessful()
            return rowsAffected
        } finally {
            db.endTransaction()
        }
    }

    fun addTest(test: Test): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEST_SUBJECT_ID, test.subjectId)
            put(COLUMN_TEST_NAME, test.name)
            put(COLUMN_NUM_QUESTIONS, test.numQuestions)
            put(COLUMN_DURATION_MINUTES, test.durationMinutes)
            put(COLUMN_ALLOW_MULTIPLE_ANSWERS, if (test.allowMultipleAnswers) 1 else 0)
            put(COLUMN_EASY_PERCENT, test.easyPercent)
            put(COLUMN_MEDIUM_PERCENT, test.mediumPercent)
            put(COLUMN_HARD_PERCENT, test.hardPercent)
            put(COLUMN_TEST_CREATED_AT, test.createdAt)
        }
        val id = db.insert(TABLE_TESTS, null, values)
        return id
    }

    @SuppressLint("Range")
    fun getAllTests(): List<Test> {
        val tests = mutableListOf<Test>()
        val db = readableDatabase
        val query = """
            SELECT t.*, s.$COLUMN_NAME as subject_name
            FROM $TABLE_TESTS t 
            JOIN $TABLE_SUBJECTS s ON t.$COLUMN_TEST_SUBJECT_ID = s.$COLUMN_ID 
            ORDER BY t.$COLUMN_TEST_CREATED_AT DESC
        """.trimIndent()

        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val test = Test(
                    testId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEST_ID)),
                    subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEST_SUBJECT_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEST_NAME)),
                    numQuestions = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUM_QUESTIONS)),
                    durationMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION_MINUTES)),
                    allowMultipleAnswers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALLOW_MULTIPLE_ANSWERS)) == 1,
                    easyPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EASY_PERCENT)),
                    mediumPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDIUM_PERCENT)),
                    hardPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HARD_PERCENT)),
                    createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TEST_CREATED_AT)),
                    subjectName = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"))
                )
                tests.add(test)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return tests
    }

    @SuppressLint("Range")
    fun getTestById(testId: Int): Test? {
        val db = readableDatabase
        val query = """
            SELECT t.*, s.$COLUMN_NAME as subject_name
            FROM $TABLE_TESTS t 
            JOIN $TABLE_SUBJECTS s ON t.$COLUMN_TEST_SUBJECT_ID = s.$COLUMN_ID 
            WHERE t.$COLUMN_TEST_ID = ?
        """.trimIndent()
        val cursor = db.rawQuery(query, arrayOf(testId.toString()))
        var test: Test? = null
        if (cursor.moveToFirst()) {
            test = Test(
                testId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEST_ID)),
                subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TEST_SUBJECT_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEST_NAME)),
                numQuestions = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_NUM_QUESTIONS)),
                durationMinutes = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_DURATION_MINUTES)),
                allowMultipleAnswers = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ALLOW_MULTIPLE_ANSWERS)) == 1,
                easyPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_EASY_PERCENT)),
                mediumPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_MEDIUM_PERCENT)),
                hardPercent = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_HARD_PERCENT)),
                createdAt = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TEST_CREATED_AT)),
                subjectName = cursor.getString(cursor.getColumnIndexOrThrow("subject_name"))
            )
        }
        cursor.close()
        return test
    }

    fun deleteTest(testId: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_TESTS, "$COLUMN_TEST_ID = ?", arrayOf(testId.toString()))
        return result
    }

    fun updateTest(testId: Int, updated: Test): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_TEST_SUBJECT_ID, updated.subjectId)
            put(COLUMN_TEST_NAME, updated.name)
            put(COLUMN_NUM_QUESTIONS, updated.numQuestions)
            put(COLUMN_DURATION_MINUTES, updated.durationMinutes)
            put(COLUMN_ALLOW_MULTIPLE_ANSWERS, if (updated.allowMultipleAnswers) 1 else 0)
            put(COLUMN_EASY_PERCENT, updated.easyPercent)
            put(COLUMN_MEDIUM_PERCENT, updated.mediumPercent)
            put(COLUMN_HARD_PERCENT, updated.hardPercent)
            // Don't touch created_at
        }
        val result = db.update(TABLE_TESTS, values, "$COLUMN_TEST_ID = ?", arrayOf(testId.toString()))
        return result
    }

    @SuppressLint("Range")
    fun getQuestionsForTest(test: Test): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase

        val numEasy = (test.numQuestions * (test.easyPercent / 100.0)).toInt()
        val numMedium = (test.numQuestions * (test.mediumPercent / 100.0)).toInt()
        val numHard = test.numQuestions - numEasy - numMedium

        val easyQuestions = getQuestionsByDifficulty(db, test.subjectId, "Dễ", numEasy)
        val mediumQuestions = getQuestionsByDifficulty(db, test.subjectId, "Trung bình", numMedium)
        val hardQuestions = getQuestionsByDifficulty(db, test.subjectId, "Khó", numHard)

        questions.addAll(easyQuestions)
        questions.addAll(mediumQuestions)
        questions.addAll(hardQuestions)

        return questions.shuffled()
    }

    @SuppressLint("Range")
    private fun getQuestionsByDifficulty(db: SQLiteDatabase, subjectId: Int, difficulty: String, limit: Int): List<Question> {
        val questions = mutableListOf<Question>()
        val query = "SELECT * FROM $TABLE_QUESTIONS WHERE $COLUMN_SUBJECT_ID = ? AND $COLUMN_DIFFICULTY = ? ORDER BY RANDOM() LIMIT ?"
        val cursor = db.rawQuery(query, arrayOf(subjectId.toString(), difficulty, limit.toString()))
        if (cursor.moveToFirst()) {
            do {
                val question = Question(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                    subjectId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SUBJECT_ID)),
                    text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                    difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY)),
                    isMultipleChoice = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_MULTIPLE_CHOICE)) == 1
                )
                questions.add(question)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return questions
    }

    @SuppressLint("Range")
    fun getAnswersByQuestion(questionId: Int): List<Answer> {
        val answers = mutableListOf<Answer>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ANSWERS WHERE $COLUMN_ANSWER_QUESTION_ID = ?", arrayOf(questionId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val answer = Answer(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_ID)),
                    questionId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_QUESTION_ID)),
                    answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_TEXT)),
                    isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CORRECT)) == 1
                )
                answers.add(answer)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return answers
    }

    fun addTestResult(testResult: TestResult): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_RESULT_TEST_ID, testResult.testId)
            put(COLUMN_SCORE, testResult.score)
            put(COLUMN_TIME_TAKEN_SECONDS, testResult.timeTakenSeconds)
            put(COLUMN_RESULT_CREATED_AT, testResult.timestamp)
        }
        val id = db.insert(TABLE_TEST_RESULTS, null, values)
        return id
    }

    @SuppressLint("Range")
    fun getTestResults(testId: Int): List<TestResult> {
        val testResults = mutableListOf<TestResult>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_TEST_RESULTS WHERE $COLUMN_RESULT_TEST_ID = ? ORDER BY $COLUMN_RESULT_CREATED_AT DESC", arrayOf(testId.toString()))
        if (cursor.moveToFirst()) {
            do {
                val testResult = TestResult(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_ID)),
                    testId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_RESULT_TEST_ID)),
                    score = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_SCORE)),
                    timeTakenSeconds = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TIME_TAKEN_SECONDS)),
                    timestamp = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_RESULT_CREATED_AT))
                )
                testResults.add(testResult)
            } while (cursor.moveToNext())
        }
        cursor.close()
        return testResults
    }
}
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
        private const val DATABASE_VERSION = 7 // Tăng phiên bản để kích hoạt onUpgrade

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

        db?.execSQL(createSubjectsTable)
        db?.execSQL(createQuestionsTable)
        db?.execSQL(createAnswersTable)
        db?.execSQL(createTestsTable)
        db?.execSQL(createTestResultsTable)
        addSampleData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ANSWERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TESTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_TEST_RESULTS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECTS")
        onCreate(db)
    }

    private fun addSampleData(db: SQLiteDatabase?) {
        // Add sample subjects
        val sampleSubjects = listOf("Toán", "Vật Lý", "Hóa Học", "Tiếng Anh")
        val subjectIds = mutableListOf<Long>()
        sampleSubjects.forEach { subjectName ->
            val values = ContentValues().apply {
                put(COLUMN_NAME, subjectName)
                put(COLUMN_CREATED_AT, System.currentTimeMillis())
            }
            val id = db?.insert(TABLE_SUBJECTS, null, values)
            id?.let { subjectIds.add(it) }
        }

        // Add sample tests
        val testIds = mutableListOf<Long>()
        if (subjectIds.isNotEmpty()) {
            val test1 = ContentValues().apply {
                put(COLUMN_TEST_SUBJECT_ID, subjectIds[0]) // Toán
                put(COLUMN_TEST_NAME, "Đề kiểm tra giữa kỳ Toán")
                put(COLUMN_NUM_QUESTIONS, 15)
                put(COLUMN_DURATION_MINUTES, 45)
                put(COLUMN_ALLOW_MULTIPLE_ANSWERS, 0)
                put(COLUMN_EASY_PERCENT, 40)
                put(COLUMN_MEDIUM_PERCENT, 40)
                put(COLUMN_HARD_PERCENT, 20)
                put(COLUMN_TEST_CREATED_AT, System.currentTimeMillis())
            }
            testIds.add(db?.insert(TABLE_TESTS, null, test1) ?: 0L)

            val test2 = ContentValues().apply {
                put(COLUMN_TEST_SUBJECT_ID, subjectIds[1]) // Vật Lý
                put(COLUMN_TEST_NAME, "Đề thi cuối kỳ Vật Lý")
                put(COLUMN_NUM_QUESTIONS, 20)
                put(COLUMN_DURATION_MINUTES, 60)
                put(COLUMN_ALLOW_MULTIPLE_ANSWERS, 0)
                put(COLUMN_EASY_PERCENT, 30)
                put(COLUMN_MEDIUM_PERCENT, 50)
                put(COLUMN_HARD_PERCENT, 20)
                put(COLUMN_TEST_CREATED_AT, System.currentTimeMillis() - 1000)
            }
            testIds.add(db?.insert(TABLE_TESTS, null, test2) ?: 0L)

            val test3 = ContentValues().apply {
                put(COLUMN_TEST_SUBJECT_ID, subjectIds[2]) // Hóa Học
                put(COLUMN_TEST_NAME, "Bài tập phản ứng hóa học")
                put(COLUMN_NUM_QUESTIONS, 10)
                put(COLUMN_DURATION_MINUTES, 30)
                put(COLUMN_ALLOW_MULTIPLE_ANSWERS, 0)
                put(COLUMN_EASY_PERCENT, 50)
                put(COLUMN_MEDIUM_PERCENT, 50)
                put(COLUMN_HARD_PERCENT, 0)
                put(COLUMN_TEST_CREATED_AT, System.currentTimeMillis() - 2000)
            }
            testIds.add(db?.insert(TABLE_TESTS, null, test3) ?: 0L)
        }

        // Add sample test results for history
        if (testIds.isNotEmpty()) {
            // 1. Toán - Trung bình (35 phút, 8.5 điểm, Tháng 11)
            db?.insert(TABLE_TEST_RESULTS, null, ContentValues().apply {
                put(COLUMN_RESULT_TEST_ID, testIds[0])
                put(COLUMN_SCORE, 8.5)
                put(COLUMN_TIME_TAKEN_SECONDS, 35 * 60)
                put(COLUMN_RESULT_CREATED_AT, System.currentTimeMillis() - 86400000)
            })
            // 2. Vật Lý - Khó (59.5 phút, 5.0 điểm, Tháng 10)
            db?.insert(TABLE_TEST_RESULTS, null, ContentValues().apply {
                put(COLUMN_RESULT_TEST_ID, testIds[1])
                put(COLUMN_SCORE, 5.0)
                put(COLUMN_TIME_TAKEN_SECONDS, 59 * 60 + 30)
                put(COLUMN_RESULT_CREATED_AT, System.currentTimeMillis() - 30L * 86400000)
            })
            // 3. Hóa Học - Dễ (20 phút, 9.8 điểm, Tháng 9)
            db?.insert(TABLE_TEST_RESULTS, null, ContentValues().apply {
                put(COLUMN_RESULT_TEST_ID, testIds[2])
                put(COLUMN_SCORE, 9.8)
                put(COLUMN_TIME_TAKEN_SECONDS, 20 * 60)
                put(COLUMN_RESULT_CREATED_AT, System.currentTimeMillis() - 60L * 86400000)
            })
        }
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
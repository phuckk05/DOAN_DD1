package com.example.da.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.da.model.Answer
import com.example.da.model.Question
import com.example.da.model.Subject

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "QuizApp.db"
        private const val DATABASE_VERSION = 2

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
    }

    override fun onCreate(db: SQLiteDatabase?) {
        // Create Subjects table
        val createSubjectsTable = """
            CREATE TABLE $TABLE_SUBJECTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        // Create Questions table
        val createQuestionsTable = """
            CREATE TABLE $TABLE_QUESTIONS (
                $COLUMN_QUESTION_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_SUBJECT_ID INTEGER NOT NULL,
                $COLUMN_QUESTION_TEXT TEXT NOT NULL,
                $COLUMN_DIFFICULTY TEXT NOT NULL,
                $COLUMN_IS_MULTIPLE_CHOICE INTEGER NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_SUBJECT_ID) REFERENCES $TABLE_SUBJECTS($COLUMN_ID)
            )
        """.trimIndent()

        // Create Answers table
        val createAnswersTable = """
            CREATE TABLE $TABLE_ANSWERS (
                $COLUMN_ANSWER_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_ANSWER_QUESTION_ID INTEGER NOT NULL,
                $COLUMN_ANSWER_TEXT TEXT NOT NULL,
                $COLUMN_IS_CORRECT INTEGER NOT NULL,
                FOREIGN KEY($COLUMN_ANSWER_QUESTION_ID) REFERENCES $TABLE_QUESTIONS($COLUMN_QUESTION_ID) ON DELETE CASCADE
            )
        """.trimIndent()

        db?.execSQL(createSubjectsTable)
        db?.execSQL(createQuestionsTable)
        db?.execSQL(createAnswersTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_ANSWERS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_QUESTIONS")
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECTS")
        onCreate(db)
    }

    // ========== SUBJECT METHODS ==========

    fun addSubject(subject: Subject): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, subject.name)
            put(COLUMN_CREATED_AT, System.currentTimeMillis())
        }
        val id = db.insert(TABLE_SUBJECTS, null, values)
        db.close()
        return id
    }

    fun getAllSubjects(): List<Subject> {
        val subjects = mutableListOf<Subject>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SUBJECTS ORDER BY $COLUMN_NAME ASC", null)

        if (cursor.moveToFirst()) {
            do {
                val subject = Subject(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)),
                    name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME))
                )
                subjects.add(subject)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return subjects
    }

    fun deleteSubject(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_SUBJECTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    fun updateSubject(subject: Subject): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, subject.name)
        }
        val result = db.update(TABLE_SUBJECTS, values, "$COLUMN_ID = ?", arrayOf(subject.id.toString()))
        db.close()
        return result
    }

    fun isSubjectExists(name: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SUBJECTS WHERE $COLUMN_NAME = ?", arrayOf(name))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    fun getSubjectCount(): Int {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_SUBJECTS", null)
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    fun addSampleSubjects() {
        if (getSubjectCount() == 0) {
            val sampleSubjects = listOf(
                "Toán", "Văn", "Tiếng Anh", "Vật Lý",
                "Hóa Học", "Sinh Học", "Lịch Sử", "Địa Lý"
            )
            for (subjectName in sampleSubjects) {
                addSubject(Subject(name = subjectName))
            }
        }
    }

    // ========== QUESTION METHODS ==========

    fun addQuestion(
        subjectId: Int,
        questionText: String,
        difficulty: String,
        isMultipleChoice: Boolean,
        answers: List<Pair<String, Boolean>>
    ): Long {
        val db = writableDatabase
        db.beginTransaction()

        try {
            // Insert question
            val questionValues = ContentValues().apply {
                put(COLUMN_SUBJECT_ID, subjectId)
                put(COLUMN_QUESTION_TEXT, questionText)
                put(COLUMN_DIFFICULTY, difficulty)
                put(COLUMN_IS_MULTIPLE_CHOICE, if (isMultipleChoice) 1 else 0)
                put(COLUMN_CREATED_AT, System.currentTimeMillis())
            }

            val questionId = db.insert(TABLE_QUESTIONS, null, questionValues)

            if (questionId > 0) {
                // Insert answers
                for ((answerText, isCorrect) in answers) {
                    val answerValues = ContentValues().apply {
                        put(COLUMN_ANSWER_QUESTION_ID, questionId)
                        put(COLUMN_ANSWER_TEXT, answerText)
                        put(COLUMN_IS_CORRECT, if (isCorrect) 1 else 0)
                    }
                    db.insert(TABLE_ANSWERS, null, answerValues)
                }
                db.setTransactionSuccessful()
            }

            db.endTransaction()
            db.close()
            return questionId
        } catch (e: Exception) {
            db.endTransaction()
            db.close()
            return -1
        }
    }

    fun getQuestionsBySubject(subjectId: Int): List<Question> {
        val questions = mutableListOf<Question>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_QUESTIONS WHERE $COLUMN_SUBJECT_ID = ? ORDER BY $COLUMN_CREATED_AT DESC",
            arrayOf(subjectId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val question = Question(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_ID)),
                    subject = getSubjectNameById(subjectId),
                    text = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_QUESTION_TEXT)),
                    difficulty = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DIFFICULTY))
                )
                questions.add(question)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return questions
    }

    fun getAnswersByQuestion(questionId: Int): List<Answer> {
        val answers = mutableListOf<Answer>()
        val db = readableDatabase

        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_ANSWERS WHERE $COLUMN_ANSWER_QUESTION_ID = ?",
            arrayOf(questionId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val answer = Answer(
                    id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_ID)),
                    questionId = questionId,
                    answerText = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ANSWER_TEXT)),
                    isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_IS_CORRECT)) == 1
                )
                answers.add(answer)
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return answers
    }

    fun deleteQuestion(questionId: Int): Int {
        val db = writableDatabase
        // Answers will be deleted automatically due to CASCADE
        val result = db.delete(TABLE_QUESTIONS, "$COLUMN_QUESTION_ID = ?", arrayOf(questionId.toString()))
        db.close()
        return result
    }

    fun getQuestionCountBySubject(subjectId: Int): Int {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT COUNT(*) FROM $TABLE_QUESTIONS WHERE $COLUMN_SUBJECT_ID = ?",
            arrayOf(subjectId.toString())
        )
        var count = 0
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0)
        }
        cursor.close()
        db.close()
        return count
    }

    private fun getSubjectNameById(subjectId: Int): String {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_NAME FROM $TABLE_SUBJECTS WHERE $COLUMN_ID = ?",
            arrayOf(subjectId.toString())
        )
        var name = ""
        if (cursor.moveToFirst()) {
            name = cursor.getString(0)
        }
        cursor.close()
        return name
    }
}


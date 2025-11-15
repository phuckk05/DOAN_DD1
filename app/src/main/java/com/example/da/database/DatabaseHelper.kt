package com.example.da.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.da.model.Subject

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "QuizApp.db"
        private const val DATABASE_VERSION = 1

        // Table Subjects
        private const val TABLE_SUBJECTS = "subjects"
        private const val COLUMN_ID = "id"
        private const val COLUMN_NAME = "name"
        private const val COLUMN_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val createSubjectsTable = """
            CREATE TABLE $TABLE_SUBJECTS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_CREATED_AT INTEGER NOT NULL
            )
        """.trimIndent()

        db?.execSQL(createSubjectsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TABLE_SUBJECTS")
        onCreate(db)
    }

    // Add a new subject
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

    // Get all subjects
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

    // Delete a subject
    fun deleteSubject(id: Int): Int {
        val db = writableDatabase
        val result = db.delete(TABLE_SUBJECTS, "$COLUMN_ID = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    // Update a subject
    fun updateSubject(subject: Subject): Int {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NAME, subject.name)
        }

        val result = db.update(TABLE_SUBJECTS, values, "$COLUMN_ID = ?", arrayOf(subject.id.toString()))
        db.close()
        return result
    }

    // Check if subject exists
    fun isSubjectExists(name: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_SUBJECTS WHERE $COLUMN_NAME = ?", arrayOf(name))
        val exists = cursor.count > 0
        cursor.close()
        db.close()
        return exists
    }

    // Get subject count
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

    // Add sample subjects (for first run)
//    fun addSampleSubjects() {
//        if (getSubjectCount() == 0) {
//            val sampleSubjects = listOf(
//                "Toán học"
//            )
//
//            for (subjectName in sampleSubjects) {
//                addSubject(Subject(name = subjectName))
//            }
//        }
//    }
}


package com.yigit.finalproject

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.yigit.finalproject.databinding.ActivityAddNoteBinding
import java.lang.Exception

class AddNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddNoteBinding
    private var isUpdate: Boolean = false
    private var noteID: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myDatabase = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null)
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes (noteID INTEGER PRIMARY KEY, title VARCHAR, content VARCHAR)")

        // Eğer not güncelleniyorsa, ID'sini al ve ilgili notun başlık ve içeriğini EditText'lere yerleştir
        val receivedIntent = intent
        noteID = receivedIntent.getIntExtra("id", -1)
        if (noteID != -1) {
            isUpdate = true
            displayNoteDetail(myDatabase, noteID)
        }

        binding.addNoteBtn.setOnClickListener {
            val noteTitle = binding.noteTitle.text.toString()
            val noteContent = binding.noteContent.text.toString()

            try {
                if (isUpdate) {
                    updateNoteInDatabase(myDatabase, noteID, noteTitle, noteContent)
                } else {
                    addNoteToDatabase(myDatabase, noteTitle, noteContent)
                }

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun displayNoteDetail(database: SQLiteDatabase, noteID: Int) {
        val cursor = database.rawQuery(
            "SELECT title, content FROM notes WHERE noteID = ?",
            arrayOf(noteID.toString())
        )

        val titleIX = cursor.getColumnIndex("title")
        val contentIX = cursor.getColumnIndex("content")

        while (cursor.moveToNext()) {
            val title = cursor.getString(titleIX)
            val content = cursor.getString(contentIX)
            binding.noteTitle.setText(title)
            binding.noteContent.setText(content)
        }

        cursor.close()
    }

    private fun addNoteToDatabase(database: SQLiteDatabase, title: String, content: String) {
        val insertQuery = "INSERT INTO notes (title, content) VALUES (?, ?)"
        database.execSQL(insertQuery, arrayOf(title, content))
    }

    private fun updateNoteInDatabase(database: SQLiteDatabase, noteID: Int, title: String, content: String) {
        val updateQuery = "UPDATE notes SET title = ?, content = ? WHERE noteID = ?"
        database.execSQL(updateQuery, arrayOf(title, content, noteID.toString()))
    }
}

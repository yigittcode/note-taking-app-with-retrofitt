package com.yigit.finalproject

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.yigit.finalproject.databinding.ActivityNoteDetailBinding
import java.lang.Exception

class NoteDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteDetailBinding
    private var noteID: Int = -1
    private var position: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val myDatabase = this.openOrCreateDatabase("Notes", MODE_PRIVATE, null)
        myDatabase.execSQL("CREATE TABLE IF NOT EXISTS notes (noteID INTEGER PRIMARY KEY, title VARCHAR, content VARCHAR)")

        val receivedIntent = intent
        noteID = receivedIntent.getIntExtra("id", -1)
        position = receivedIntent.getIntExtra("position", -1)

        if (noteID != -1) {
            displayNoteDetail(myDatabase, noteID)
        }

        binding.removeBtn.setOnClickListener {
            removeNoteFromDatabase(myDatabase, noteID)
        }

        binding.updateBtn.setOnClickListener {
            updateNoteInDatabase(myDatabase, noteID)
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
            binding.titleDetail.text = title
            binding.contentDetail.text = content
        }

        cursor.close()
    }

    private fun removeNoteFromDatabase(database: SQLiteDatabase, noteID: Int) {
        database.execSQL("DELETE FROM notes WHERE noteID = ?", arrayOf(noteID.toString()))
        navigateToMainActivity()
    }

    private fun updateNoteInDatabase(database: SQLiteDatabase, noteID: Int) {
        try {
            val intentToUpdate = Intent(this, AddNoteActivity::class.java)
            intentToUpdate.putExtra("id", noteID)
            startActivity(intentToUpdate)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun navigateToMainActivity() {
        val intentToBack = Intent(this, MainActivity::class.java)
        intentToBack.putExtra("idThatDeleted", noteID)
        intentToBack.putExtra("position", position)
        startActivity(intentToBack)

    }
}

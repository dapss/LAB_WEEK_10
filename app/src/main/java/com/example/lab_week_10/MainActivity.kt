package com.example.lab_week_10

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.lab_week_10.database.Total
import com.example.lab_week_10.database.TotalDatabase
import com.example.lab_week_10.database.TotalObject
import com.example.lab_week_10.viewmodels.TotalViewModel
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[TotalViewModel::class.java]
    }

    private val db by lazy { prepareDatabase() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeValueFromDatabase()
        prepareViewModel()
    }

    // BONUS: Show the last update date when app starts
    override fun onStart() {
        super.onStart()
        val totalList = db.totalDao().getTotal(ID)
        if (totalList.isNotEmpty()) {
            val lastDate = totalList.first().total.date
            if (lastDate.isNotEmpty()) {
                Toast.makeText(this, lastDate, Toast.LENGTH_LONG).show()
            }
        }
    }

    // BONUS: Save the value AND the current date when paused
    override fun onPause() {
        super.onPause()
        val currentValue = viewModel.total.value ?: 0
        val currentDate = Date().toString()
        db.totalDao().update(Total(ID, TotalObject(currentValue, currentDate)))
    }

    private fun updateText(total: Int) {
        findViewById<TextView>(R.id.text_total).text =
            getString(R.string.text_total, total)
    }

    private fun prepareViewModel() {
        viewModel.total.observe(this) { total ->
            updateText(total)
        }

        findViewById<Button>(R.id.button_increment).setOnClickListener {
            viewModel.incrementTotal()
        }
    }

    private fun prepareDatabase(): TotalDatabase {
        return Room.databaseBuilder(
            applicationContext,
            TotalDatabase::class.java,
            "total-database"
        ).allowMainThreadQueries().build()
    }

    // BONUS: Handle the new TotalObject structure
    private fun initializeValueFromDatabase() {
        val totalList = db.totalDao().getTotal(ID)
        if (totalList.isEmpty()) {
            db.totalDao().insert(Total(ID, TotalObject(0, "")))
        } else {
            viewModel.setTotal(totalList.first().total.value)
        }
    }

    companion object {
        const val ID: Long = 1
    }
}
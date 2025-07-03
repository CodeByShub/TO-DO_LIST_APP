package com.example.to_dolist

import Todo
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    private lateinit var todoList: MutableList<Todo>
    private lateinit var adapter: TodoAdapter
    private lateinit var preferences: android.content.SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "Today's Task"

        preferences = getSharedPreferences("todo_prefs", MODE_PRIVATE)
        todoList = loadTodos()

        val recyclerView = findViewById<RecyclerView>(R.id.rvTodos)
        val input = findViewById<EditText>(R.id.etTodo)
        val addBtn = findViewById<Button>(R.id.btnAddTOdo)

        adapter = TodoAdapter(
            todoList,
            onEditClick = { index -> showEditDialog(index) },
            onDeleteClick = { index -> deleteTask(index) },
            onStartClick = { index -> markTaskInProgress(index) },
            onCheckChanged = { index, isChecked -> handleCheckboxChange(index, isChecked) }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        addBtn.setOnClickListener {
            val taskText = input.text.toString().trim()
            if (taskText.isNotEmpty()) {
                val task = Todo(taskText, false, false, "Pending")
                todoList.add(task)
                adapter.notifyItemInserted(todoList.lastIndex)
                input.text.clear()
                saveTodos()
            }
        }
    }

    private fun handleCheckboxChange(index: Int, isChecked: Boolean) {
        if (index !in todoList.indices) return
        val task = todoList[index]

        if (task.status == "In Progress") {
            task.isChecked = isChecked
            task.status = if (isChecked) "Completed" else "In Progress"
            adapter.notifyItemChanged(index)
            saveTodos()
            checkIfAllTasksCompleted()
        } else {
            Toast.makeText(this, "Start the task first!", Toast.LENGTH_SHORT).show()
            adapter.notifyItemChanged(index)
        }
    }

    private fun markTaskInProgress(index: Int) {
        if (index in todoList.indices) {
            todoList[index].status = "In Progress"
            adapter.notifyItemChanged(index)
            saveTodos()
        }
    }

    private fun checkIfAllTasksCompleted() {
        if (todoList.isNotEmpty() && todoList.all { it.isChecked }) {
            Toast.makeText(this, "Today's tasks completed!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showEditDialog(index: Int) {
        if (index !in todoList.indices) return
        val task = todoList[index]

        val input = EditText(this).apply {
            setText(task.title)
        }

        AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(input)
            .setPositiveButton("Save") { _, _ ->
                val newText = input.text.toString().trim()
                if (newText.isNotEmpty()) {
                    task.title = newText
                    adapter.notifyItemChanged(index)
                    saveTodos()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteTask(index: Int) {
        if (index in todoList.indices) {
            todoList.removeAt(index)
            adapter.notifyItemRemoved(index)
            adapter.notifyItemRangeChanged(index, todoList.size)
            saveTodos()
            if (todoList.isEmpty()) {
                Toast.makeText(this, "All tasks removed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveTodos() {
        val json = Gson().toJson(todoList)
        preferences.edit().putString("todos", json).apply()
    }

    private fun loadTodos(): MutableList<Todo> {
        val json = preferences.getString("todos", null)
        return if (!json.isNullOrEmpty()) {
            try {
                val type = object : TypeToken<MutableList<Todo>>() {}.type
                Gson().fromJson(json, type) ?: mutableListOf()
            } catch (e: Exception) {
                mutableListOf()
            }
        } else {
            mutableListOf()
        }
    }
}

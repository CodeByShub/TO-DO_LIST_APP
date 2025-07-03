package com.example.to_dolist

import Todo
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class TodoAdapter(
    private val todos: MutableList<Todo>,
    private val onEditClick: (Int) -> Unit,
    private val onDeleteClick: (Int) -> Unit,
    private val onStartClick: (Int) -> Unit,
    private val onCheckChanged: (Int, Boolean) -> Unit
) : RecyclerView.Adapter<TodoAdapter.TodoViewHolder>() {

    inner class TodoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val cbDone: CheckBox = itemView.findViewById(R.id.cbDone)
        val btnEdit: ImageButton = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val btnStart: Button = itemView.findViewById(R.id.btnStart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_todo, parent, false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        if (position !in todos.indices) return

        val currentTask = todos[position]

        holder.tvTitle.text = currentTask.title
        holder.tvStatus.text = currentTask.status

        holder.cbDone.setOnCheckedChangeListener(null)
        holder.cbDone.isChecked = currentTask.isChecked

        holder.cbDone.setOnCheckedChangeListener { _, isChecked ->
            if (position in todos.indices) onCheckChanged(position, isChecked)
        }

        holder.btnEdit.setOnClickListener {
            if (position in todos.indices) onEditClick(position)
        }

        holder.btnDelete.setOnClickListener {
            if (position in todos.indices) onDeleteClick(position)
        }

        holder.btnStart.setOnClickListener {
            if (position in todos.indices) onStartClick(position)
        }
    }

    override fun getItemCount(): Int = todos.size
}

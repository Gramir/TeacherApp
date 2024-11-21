package com.example.teacherapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.R
import com.example.teacherapp.databinding.ItemAssignmentBinding
import com.example.teacherapp.domain.model.Assignment

class AssignmentAdapter(
    private val onEditClick: (Assignment) -> Unit,
    private val onDeleteClick: (Assignment) -> Unit
) : ListAdapter<Assignment, AssignmentAdapter.AssignmentViewHolder>(AssignmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AssignmentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AssignmentViewHolder(
        private val binding: ItemAssignmentBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(assignment: Assignment) {
            binding.apply {
                titleText.text = assignment.title
                descriptionText.text = assignment.description
                dueDateText.text = root.context.getString(R.string.due_date_format, assignment.dueDate)
                statusText.text = assignment.status

                // Establecer color según el estado
                val statusColor = when (assignment.status.lowercase()) {
                    "pendiente" -> "#FFA000"
                    "en progreso" -> "#1976D2"
                    "completado" -> "#388E3C"
                    else -> "#757575"
                }
                statusText.setBackgroundColor(android.graphics.Color.parseColor(statusColor))
                statusText.setTextColor(android.graphics.Color.WHITE)

                // Configurar botones
                editButton.setText(R.string.edit)
                deleteButton.setText(R.string.delete)

                editButton.setOnClickListener { onEditClick(assignment) }
                deleteButton.setOnClickListener { onDeleteClick(assignment) }
            }
        }
    }

    class AssignmentDiffCallback : DiffUtil.ItemCallback<Assignment>() {
        override fun areItemsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Assignment, newItem: Assignment): Boolean {
            return oldItem == newItem
        }
    }
}
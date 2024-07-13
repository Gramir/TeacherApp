package com.example.teacherapp.ui.assignment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.data.model.Assignment
import com.example.teacherapp.databinding.ItemAssignmentBinding

class AssignmentAdapter(
    private val onEditClick: (Assignment) -> Unit,
    private val onDeleteClick: (Assignment) -> Unit
) : ListAdapter<Assignment, AssignmentAdapter.AssignmentViewHolder>(AssignmentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AssignmentViewHolder {
        val binding = ItemAssignmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AssignmentViewHolder(binding, onEditClick, onDeleteClick)
    }

    override fun onBindViewHolder(holder: AssignmentViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AssignmentViewHolder(
        private val binding: ItemAssignmentBinding,
        private val onEditClick: (Assignment) -> Unit,
        private val onDeleteClick: (Assignment) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(assignment: Assignment) {
            binding.assignmentTitleTextView.text = assignment.title
            binding.assignmentDescriptionTextView.text = assignment.description
            binding.assignmentDueDateTextView.text = assignment.dueDate
            binding.assignmentStatusTextView.text = assignment.status

            binding.editAssignmentButton.setOnClickListener { onEditClick(assignment) }
            binding.deleteAssignmentButton.setOnClickListener { onDeleteClick(assignment) }
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
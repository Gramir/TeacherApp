package com.example.teacherapp.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.databinding.ItemStudentBinding
import com.example.teacherapp.domain.model.Student

class StudentAdapter(
    private val onItemClick: ((Student) -> Unit)? = null
) : ListAdapter<Student, StudentAdapter.StudentViewHolder>(StudentDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val binding = ItemStudentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return StudentViewHolder(binding, onItemClick)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = getItem(position)
        holder.bind(student)
    }

    class StudentViewHolder(
        private val binding: ItemStudentBinding,
        private val onItemClick: ((Student) -> Unit)?
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(student: Student) {
            with(binding) {
                studentNameTextView.text = buildString {
                    append(student.name)
                    append(" ")
                    append(student.lastName)
                }

                studentBirthDateTextView.text = buildString {
                    append("Birth Date: ")
                    append(student.birthDate)
                }

                studentPhoneTextView.text = buildString {
                    append("Phone: ")
                    append(student.phone)
                }

                studentEmailTextView.text = buildString {
                    append("Email: ")
                    append(student.email)
                }

                // Card click listener
                root.setOnClickListener {
                    onItemClick?.invoke(student)
                }
            }
        }
    }

    class StudentDiffCallback : DiffUtil.ItemCallback<Student>() {
        override fun areItemsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Student, newItem: Student): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TAG = "StudentAdapter"
    }
}
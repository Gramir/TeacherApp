package com.example.teacherapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.databinding.ItemCourseBinding
import com.example.teacherapp.domain.model.Course

class CourseAdapter(
    private val onAssignmentsClick: (String) -> Unit,
    private val onAttendanceClick: (String) -> Unit,
    private val onStudentsClick: (String) -> Unit
) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CourseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class CourseViewHolder(
        private val binding: ItemCourseBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(course: Course) {
            binding.apply {
                courseNameText.text = course.name
                // Log: Ver el ID del curso cuando se bindea
                Log.d("CourseAdapter", "Binding course con ID: ${course.id}")
                assignmentsButton.setOnClickListener {  // Log: Ver el ID cuando se hace click
                    Log.d("CourseAdapter", "Click en attendance para curso ID: ${course.id}")
                    onAssignmentsClick(course.id) }
                attendanceButton.setOnClickListener { onAttendanceClick(course.id) }
                studentsButton.setOnClickListener { onStudentsClick(course.id) }
            }
        }
    }

    class CourseDiffCallback : DiffUtil.ItemCallback<Course>() {
        override fun areItemsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Course, newItem: Course): Boolean {
            return oldItem == newItem
        }
    }
}
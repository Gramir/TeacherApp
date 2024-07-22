package com.example.teacherapp.ui.course

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.data.model.Course
import com.example.teacherapp.databinding.ItemCourseBinding

class CourseAdapter(private val listener: CourseClickListener) : ListAdapter<Course, CourseAdapter.CourseViewHolder>(CourseDiffCallback()) {

    interface CourseClickListener {
        fun onAttendanceClick(courseId: Int)
        fun onAssignmentsClick(courseId: Int)
        fun onStudentsClick(courseId: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CourseViewHolder {
        val binding = ItemCourseBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CourseViewHolder(binding, listener)
    }

    override fun onBindViewHolder(holder: CourseViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CourseViewHolder(private val binding: ItemCourseBinding, private val listener: CourseClickListener) : RecyclerView.ViewHolder(binding.root) {
        fun bind(course: Course) {
            binding.courseNameTextView.text = course.name
            binding.attendanceButton.setOnClickListener { listener.onAttendanceClick(course.id) }
            binding.assignmentsButton.setOnClickListener { listener.onAssignmentsClick(course.id) }
            binding.studentsButton.setOnClickListener {
                Log.d("CourseAdapter", "Students button clicked for course: ${course.id}")
                listener.onStudentsClick(course.id) }
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
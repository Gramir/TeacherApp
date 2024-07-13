package com.example.teacherapp.ui.attendance

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.data.model.Attendance
import com.example.teacherapp.databinding.ItemAttendanceBinding

class AttendanceAdapter : ListAdapter<Attendance, AttendanceAdapter.AttendanceViewHolder>(AttendanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class AttendanceViewHolder(private val binding: ItemAttendanceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: Attendance) {
            binding.studentNameTextView.text = attendance.studentName
            binding.presentCheckBox.isChecked = attendance.present
            binding.presentCheckBox.setOnCheckedChangeListener { _, isChecked ->
                attendance.present = isChecked
            }
        }
    }

    class AttendanceDiffCallback : DiffUtil.ItemCallback<Attendance>() {
        override fun areItemsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
            return oldItem.studentId == newItem.studentId && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: Attendance, newItem: Attendance): Boolean {
            return oldItem == newItem
        }
    }
}
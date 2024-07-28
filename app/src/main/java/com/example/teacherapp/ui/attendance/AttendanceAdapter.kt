package com.example.teacherapp.ui.attendance

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.data.dao.AttendanceWithStudentName
import com.example.teacherapp.databinding.ItemAttendanceBinding

class AttendanceAdapter(private val onAttendanceChanged: (AttendanceWithStudentName) -> Unit) :
    ListAdapter<AttendanceWithStudentName, AttendanceAdapter.AttendanceViewHolder>(AttendanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AttendanceViewHolder(binding, onAttendanceChanged)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val item = getItem(position)
        Log.d("AttendanceAdapter", "Binding item at position $position: $item")
        holder.bind(item)
    }

    class AttendanceViewHolder(
        private val binding: ItemAttendanceBinding,
        private val onAttendanceChanged: (AttendanceWithStudentName) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: AttendanceWithStudentName) {
            binding.studentNameTextView.text = attendance.studentName
            binding.presentCheckBox.isChecked = attendance.present
            binding.presentCheckBox.setOnCheckedChangeListener { _, isChecked ->
                attendance.present = isChecked
                onAttendanceChanged(attendance)
            }
        }
    }

    class AttendanceDiffCallback : DiffUtil.ItemCallback<AttendanceWithStudentName>() {
        override fun areItemsTheSame(oldItem: AttendanceWithStudentName, newItem: AttendanceWithStudentName): Boolean {
            return oldItem.studentId == newItem.studentId && oldItem.date == newItem.date
        }

        override fun areContentsTheSame(oldItem: AttendanceWithStudentName, newItem: AttendanceWithStudentName): Boolean {
            return oldItem == newItem
        }
    }
}
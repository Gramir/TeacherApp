package com.example.teacherapp.presentation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.databinding.ItemAttendanceBinding
import com.example.teacherapp.presentation.viewmodel.attendance.AttendanceWithStudent

class AttendanceAdapter(
    private val onAttendanceChanged: (String, Boolean) -> Unit
) : ListAdapter<AttendanceWithStudent, AttendanceAdapter.AttendanceViewHolder>(AttendanceDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceViewHolder {
        val binding = ItemAttendanceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AttendanceViewHolder(binding, onAttendanceChanged)
    }

    override fun onBindViewHolder(holder: AttendanceViewHolder, position: Int) {
        val item = getItem(position)
        Log.d(TAG, "Binding item at position $position: $item")
        holder.bind(item)
    }

    class AttendanceViewHolder(
        private val binding: ItemAttendanceBinding,
        private val onAttendanceChanged: (String, Boolean) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(attendance: AttendanceWithStudent) {
            with(binding) {
                studentNameTextView.text = attendance.studentName
                presentCheckBox.apply {
                    setOnCheckedChangeListener(null) // Evita llamadas recursivas
                    isChecked = attendance.present
                    setOnCheckedChangeListener { _, isChecked ->
                        onAttendanceChanged(attendance.studentId, isChecked)
                    }
                }
            }
        }
    }

    class AttendanceDiffCallback : DiffUtil.ItemCallback<AttendanceWithStudent>() {
        override fun areItemsTheSame(
            oldItem: AttendanceWithStudent,
            newItem: AttendanceWithStudent
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: AttendanceWithStudent,
            newItem: AttendanceWithStudent
        ): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val TAG = "AttendanceAdapter"
    }
}
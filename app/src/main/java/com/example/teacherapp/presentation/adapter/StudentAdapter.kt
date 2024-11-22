import androidx.recyclerview.widget.DiffUtil
import com.example.teacherapp.R
import com.example.teacherapp.domain.model.Student
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.teacherapp.databinding.ItemStudentBinding

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
            binding.apply {
                // Establecer las iniciales
                initialsTextView.text = getInitials(student)

                // Establecer el nombre completo
                studentNameTextView.text = "${student.name} ${student.lastName}"

                // Establecer fecha de nacimiento
                studentBirthDateTextView.text = buildString {
                    append(root.context.getString(R.string.student_birth_date))
                    append(": ")
                    append(student.birthDate)
                }

                // Establecer teléfono
                studentPhoneTextView.text = buildString {
                    append(root.context.getString(R.string.student_phone))
                    append(": ")
                    append(student.phone)
                }

                // Establecer email
                studentEmailTextView.text = buildString {
                    append(root.context.getString(R.string.student_email))
                    append(": ")
                    append(student.email)
                }

                // Click listener en la tarjeta
                root.setOnClickListener {
                    onItemClick?.invoke(student)
                }
            }
        }

        private fun getInitials(student: Student): String {
            val nameInitial = student.name.firstOrNull()?.uppercase() ?: ""
            val lastNameInitial = student.lastName.firstOrNull()?.uppercase() ?: ""
            return nameInitial + lastNameInitial
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
package com.example.teacherapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.TeacherRepository
import com.example.teacherapp.databinding.ActivityLoginBinding
import com.example.teacherapp.ui.MainActivity
import com.example.teacherapp.viewmodel.LoginViewModel
import com.example.teacherapp.viewmodel.LoginViewModelFactory

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = TeacherRepository(database.teacherDao())
        val factory = LoginViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(username, password)
        }

        viewModel.loginResult.observe(this) { teacher ->
            if (teacher != null) {
                Log.d("LoginActivity", "Login successful: $teacher")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("TEACHER_ID", teacher.id)
                startActivity(intent)
                finish()
            } else {
                binding.usernameInputLayout.error = "Datos invalidos"
                binding.passwordInputLayout.error = "Datos invalidos"
            }
        }
    }
}
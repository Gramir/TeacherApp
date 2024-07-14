package com.example.teacherapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.teacherapp.data.database.AppDatabase
import com.example.teacherapp.data.repository.TeacherRepository
import com.example.teacherapp.databinding.ActivityLoginBinding
import com.example.teacherapp.ui.MainActivity
import com.example.teacherapp.viewmodel.LoginResult
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
        viewModel = ViewModelProvider(this, LoginViewModelFactory(repository))[LoginViewModel::class.java]

        binding.loginButton.setOnClickListener {
            val username = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.login(username, password)
        }

        viewModel.loginResult.observe(this) { result ->
            when (result) {
                is LoginResult.Success -> {
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("TEACHER_ID", result.teacher.id)
                    startActivity(intent)
                    finish()
                }
                is LoginResult.Error -> {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
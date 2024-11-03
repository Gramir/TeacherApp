package com.example.teacherapp.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.databinding.ActivityLoginBinding
import com.example.teacherapp.presentation.viewmodel.login.LoginState
import com.example.teacherapp.presentation.viewmodel.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViews()
        observeState()
    }

    private fun setupViews() {
        binding.loginButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (username.isBlank() || password.isBlank()) {
                Snackbar.make(binding.root, "Please fill all fields", Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.login(username, password)
        }
    }

    private fun observeState() {
        lifecycleScope.launch {
            viewModel.loginState.collect { state ->
                when (state) {
                    is LoginState.Initial -> {
                        // Initial state, no action needed
                    }
                    is LoginState.Loading -> {
                        binding.loginButton.isEnabled = false
                        binding.progressBar.show()
                    }
                    is LoginState.Success -> {
                        binding.progressBar.hide()
                        navigateToMain(state.teacher.id)
                    }
                    is LoginState.Error -> {
                        binding.loginButton.isEnabled = true
                        binding.progressBar.hide()
                        Snackbar.make(binding.root, state.message, Snackbar.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun navigateToMain(teacherId: String) {
        val intent = Intent(this, MainActivity::class.java).apply {
            putExtra("TEACHER_ID", teacherId)
        }
        startActivity(intent)
        finish()
    }
}
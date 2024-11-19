package com.example.teacherapp.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.data.datasource.remote.firebase.SessionManager
import com.example.teacherapp.databinding.ActivityLoginBinding
import com.example.teacherapp.presentation.viewmodel.login.LoginState
import com.example.teacherapp.presentation.viewmodel.login.LoginViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si ya hay sesión activa
        if (sessionManager.isLoggedIn()) {
            navigateToMain(sessionManager.getCurrentUserId() ?: return)
            finish()
            return
        }

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
                Snackbar.make(binding.root, "Por favor completa todos los campos", Snackbar.LENGTH_SHORT).show()
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
                        binding.loginButton.isEnabled = true
                        binding.progressBar.hide()
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
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("TEACHER_ID", teacherId)
        }
        startActivity(intent)
        finish()
    }
}
package com.example.teacherapp.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.teacherapp.databinding.ActivityLoginBinding
import com.example.teacherapp.presentation.viewmodel.login.LoginState
import com.example.teacherapp.presentation.viewmodel.login.LoginViewModel
import com.example.teacherapp.util.TestDataInserter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    @Inject
    lateinit var testDataInserter: TestDataInserter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

            insertTestDataIfNeeded()

        setupViews()
        observeState()
    }

    private fun insertTestDataIfNeeded() {
        val sharedPrefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        val testDataInserted = sharedPrefs.getBoolean("test_data_inserted", false)

        if (!testDataInserted) {
            lifecycleScope.launch {
                try {
                    testDataInserter.insertTestData()
                    sharedPrefs.edit().putBoolean("test_data_inserted", true).apply()


                    Snackbar.make(
                        binding.root,
                        "Datos de prueba creados exitosamente",
                        Snackbar.LENGTH_LONG
                    ).show()
                } catch (e: Exception) {
                    Snackbar.make(
                        binding.root,
                        "Error al crear datos de prueba: ${e.message}",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
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
            putExtra("TEACHER_ID", teacherId)
        }
        startActivity(intent)
        finish()
    }
}
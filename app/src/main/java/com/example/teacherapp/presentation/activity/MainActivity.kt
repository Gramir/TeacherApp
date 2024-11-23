package com.example.teacherapp.presentation.activity

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.teacherapp.R
import com.example.teacherapp.data.datasource.remote.firebase.SessionManager
import com.example.teacherapp.data.service.NotificationService
import com.example.teacherapp.databinding.ActivityMainBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    @Inject
    lateinit var notificationService: NotificationService

    @Inject
    lateinit var sessionManager: SessionManager

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            handleNotificationPermissionGranted()
        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                showNotificationPermissionDialog()
            } else {
                showNotificationSettingsDialog()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!sessionManager.isLoggedIn()) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupNavigation()
        setupFirebaseMessaging()
        askNotificationPermission()
    }

    private fun setupNavigation() {
        setSupportActionBar(binding.toolbar)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.courseListFragment)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun setupFirebaseMessaging() {
        val teacherId = intent.getStringExtra("TEACHER_ID")
        if (teacherId != null) {
            // Suscribirse a notificaciones específicas para este profesor
            FirebaseMessaging.getInstance().subscribeToTopic("teacher_$teacherId")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Subscribed to teacher notifications")
                    } else {
                        Log.w(TAG, "Failed to subscribe to teacher notifications", task.exception)
                    }
                }

            // Obtener y registrar el token FCM
            FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val token = task.result
                    Log.d(TAG, "FCM Token: $token")
                } else {
                    Log.w(TAG, "Failed to get FCM token", task.exception)
                }
            }
        } else {
            Log.e(TAG, "Teacher ID is null")
        }
    }

    private fun handleNotificationPermissionGranted() {
        Toast.makeText(this, "Notificaciones activadas", Toast.LENGTH_SHORT).show()
        val teacherId = intent.getStringExtra("TEACHER_ID")
        if (teacherId != null) {
            FirebaseMessaging.getInstance().subscribeToTopic("teacher_$teacherId")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d(TAG, "Successfully subscribed to notifications")
                    } else {
                        Log.w(TAG, "Failed to subscribe to notifications", task.exception)
                    }
                }
        }
        notificationService.createNotificationChannel()
    }

    private fun showNotificationPermissionDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.notification_permission_title)
            .setMessage(R.string.notification_permission_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                askNotificationPermission()
            }
            .setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    this,
                    R.string.notifications_disabled_message,
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    private fun showNotificationSettingsDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.enable_notifications)
            .setMessage(R.string.enable_notifications_settings_message)
            .setPositiveButton(R.string.settings) { _, _ ->
                openNotificationSettings()
            }
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
                Toast.makeText(
                    this,
                    R.string.notifications_disabled_message,
                    Toast.LENGTH_LONG
                ).show()
            }
            .show()
    }

    private fun openNotificationSettings() {
        try {
            val intent = Intent().apply {
                action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                data = Uri.fromParts("package", packageName, null)
            }
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(
                this,
                R.string.unable_to_open_settings,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                handleNotificationPermissionGranted()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            handleNotificationPermissionGranted()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}
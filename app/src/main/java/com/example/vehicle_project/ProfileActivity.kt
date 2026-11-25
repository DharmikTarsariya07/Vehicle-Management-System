package com.example.vehicle_project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ProfileActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private var userEmail: String = ""
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView

    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val updatedEmail = result.data?.getStringExtra("UPDATED_EMAIL")
            if (!updatedEmail.isNullOrEmpty()) {
                userEmail = updatedEmail
            }
            loadUserData()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        tvUsername = findViewById(R.id.tv_profile_username)
        tvEmail = findViewById(R.id.tv_profile_email)

        val optionEditProfile = findViewById<View>(R.id.option_edit_profile)
        val optionLogout = findViewById<View>(R.id.option_logout)
        val optionRemoveAccount = findViewById<View>(R.id.option_remove_account)

        loadUserData()

        // 1. Edit Profile: Refers to RegisterActivity with IS_EDIT_MODE = true
        optionEditProfile.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java).apply {
                putExtra("IS_EDIT_MODE", true)
                putExtra("USER_EMAIL", userEmail)
            }
            editProfileLauncher.launch(intent)
        }

        // 2. Logout: AlertDialog confirmation -> refers to MainActivity (Login)
        optionLogout.setOnClickListener {
            showLogoutConfirmationDialog()
        }

        // 3. Remove Account: AlertDialog confirmation -> delete user record -> refers to MainActivity (Login)
        optionRemoveAccount.setOnClickListener {
            showRemoveAccountConfirmationDialog()
        }

        // Bottom Navigation
        findViewById<View>(R.id.nav_home).setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.nav_insurance).setOnClickListener {
            val intent = Intent(this, InsuranceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }

        findViewById<View>(R.id.nav_service).setOnClickListener {
            val intent = Intent(this, ServiceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadUserData()
    }

    private fun loadUserData() {
        if (userEmail.isNotEmpty()) {
            val username = dbHelper.getUsernameByEmail(userEmail)
            tvUsername.text = if (username.isNotEmpty()) username else "User"
            tvEmail.text = userEmail
        }
    }

    private fun showLogoutConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Logout")
            .setMessage("Are you sure you want to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun showRemoveAccountConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Remove Account")
            .setMessage("Are you sure you want to delete your account? All your vehicle, insurance, and service records will be permanently removed.")
            .setPositiveButton("Yes") { _, _ ->
                val isDeleted = dbHelper.deleteUserData(userEmail)
                if (isDeleted) {
                    Toast.makeText(this, "Account removed successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to remove account", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}

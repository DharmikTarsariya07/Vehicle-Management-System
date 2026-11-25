package com.example.vehicle_project

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val databaseHelper = DatabaseHelper(this)

        val nameInput = findViewById<EditText>(R.id.name_input)
        val emailInput = findViewById<EditText>(R.id.email_input)
        val passwordInput = findViewById<EditText>(R.id.password_input)
        val confirmPasswordInput = findViewById<EditText>(R.id.confirm_password_input)
        
        val registerButton = findViewById<Button>(R.id.register_button)
        val loginText = findViewById<TextView>(R.id.login_text)

        val eyeIcon1 = findViewById<ImageView>(R.id.eye_icon_1)
        val eyeIcon2 = findViewById<ImageView>(R.id.eye_icon_2)

        val isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        val currentEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val titleText = findViewById<TextView>(R.id.title_text)
        val subtitleText = findViewById<TextView>(R.id.subtitle_text)

        if (isEditMode) {
            titleText.text = "Edit Profile"
            subtitleText.text = "Update your account details below."
            registerButton.text = "Update Profile"
            loginText.visibility = android.view.View.GONE
            findViewById<android.view.View>(R.id.login_text).parent?.let { parentView ->
                if (parentView is android.view.View) {
                    parentView.visibility = android.view.View.GONE
                }
            }

            // Pre-fill existing user info
            val userDetails = databaseHelper.getUserDetails(currentEmail)
            if (userDetails != null) {
                nameInput.setText(userDetails["username"])
                emailInput.setText(userDetails["email"])
                passwordInput.setText(userDetails["password"])
                confirmPasswordInput.setText(userDetails["confirm_password"])
            }
        }

        registerButton.setOnClickListener {
            val name = nameInput.text.toString().trim()
            val email = emailInput.text.toString().trim()
            val password = passwordInput.text.toString().trim()
            val confirmPassword = confirmPasswordInput.text.toString().trim()

            if (name.isEmpty()) {
                nameInput.error = "Full Name is required"
                return@setOnClickListener
            }
            if (!name.matches(Regex("^[a-zA-Z\\s]+$"))) {
                nameInput.error = "Only alphabets are allowed"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                emailInput.error = "Email Address is required"
                return@setOnClickListener
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailInput.error = "Invalid Email Address"
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                passwordInput.error = "Password is required"
                return@setOnClickListener
            }
            if (confirmPassword.isEmpty()) {
                confirmPasswordInput.error = "Confirm Password is required"
                return@setOnClickListener
            }
            if (password != confirmPassword) {
                confirmPasswordInput.error = "Passwords do not match"
                return@setOnClickListener
            }

            if (isEditMode) {
                val isUpdated = databaseHelper.updateUserData(currentEmail, name, email, password, confirmPassword)
                if (isUpdated) {
                    Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("UPDATED_EMAIL", email)
                    setResult(RESULT_OK, resultIntent)
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update profile (email might already exist)", Toast.LENGTH_SHORT).show()
                }
            } else {
                val isInserted = databaseHelper.insertUserData(name, email, password, confirmPassword)
                if (isInserted) {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Registration failed", Toast.LENGTH_SHORT).show()
                }
            }
        }

        var isPasswordVisible1 = false
        eyeIcon1.setOnClickListener {
            isPasswordVisible1 = !isPasswordVisible1
            if (isPasswordVisible1) {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                passwordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            passwordInput.setSelection(passwordInput.text.length)
        }

        var isPasswordVisible2 = false
        eyeIcon2.setOnClickListener {
            isPasswordVisible2 = !isPasswordVisible2
            if (isPasswordVisible2) {
                confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                confirmPasswordInput.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            confirmPasswordInput.setSelection(confirmPasswordInput.text.length)
        }

        loginText.setOnClickListener {
            //finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent);
        }
    }
}

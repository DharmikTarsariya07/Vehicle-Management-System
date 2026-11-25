package com.example.vehicle_project

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddInsuranceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_insurance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        val isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        val insuranceId = intent.getIntExtra("INSURANCE_ID", -1)

        val tvTitle = findViewById<TextView>(R.id.tv_insurance_form_title)
        val etVehicleNumber = findViewById<EditText>(R.id.et_insurance_vehicle_number)
        val etCompany = findViewById<EditText>(R.id.et_insurance_company)
        val etPolicyNumber = findViewById<EditText>(R.id.et_insurance_policy_number)
        val etExpiryDate = findViewById<EditText>(R.id.et_insurance_expiry_date)
        val etPremium = findViewById<EditText>(R.id.et_insurance_premium)
        val btnSave = findViewById<Button>(R.id.btn_save_insurance)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        val calendar = Calendar.getInstance()

        // Setup DatePicker for Expiry Date
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            etExpiryDate.setText(sdf.format(calendar.time))
        }

        etExpiryDate.setOnClickListener {
            val dpd = DatePickerDialog(
                this,
                dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            dpd.show()
        }

        if (isEditMode) {
            tvTitle.text = "Update Insurance"
            btnSave.text = "Update Insurance"

            etVehicleNumber.setText(intent.getStringExtra("INSURANCE_VEHICLE_NUMBER") ?: "")
            etCompany.setText(intent.getStringExtra("INSURANCE_COMPANY") ?: "")
            etPolicyNumber.setText(intent.getStringExtra("INSURANCE_POLICY_NUMBER") ?: "")
            val existingDate = intent.getStringExtra("INSURANCE_EXPIRY_DATE") ?: ""
            etExpiryDate.setText(existingDate)
            etPremium.setText(intent.getStringExtra("INSURANCE_PREMIUM") ?: "")

            if (existingDate.isNotEmpty()) {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    sdf.parse(existingDate)?.let { date ->
                        calendar.time = date
                    }
                } catch (_: Exception) {}
            }
        }

        btnBack.setOnClickListener {
            if (isTaskRoot) {
                val intent = Intent(this, InsuranceActivity::class.java)
                intent.putExtra("USER_EMAIL", userEmail)
                startActivity(intent)
            }
            finish()
        }

        btnSave.setOnClickListener {
            val vehicleNumber = etVehicleNumber.text.toString().trim()
            val company = etCompany.text.toString().trim()
            val policyNumber = etPolicyNumber.text.toString().trim()
            val expiryDate = etExpiryDate.text.toString().trim()
            val premium = etPremium.text.toString().trim()

            if (vehicleNumber.isEmpty() || company.isEmpty() || policyNumber.isEmpty() || expiryDate.isEmpty() || premium.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if vehicle exists (Foreign Key validation)
            val vehicleExists = dbHelper.doesVehicleExist(vehicleNumber, userEmail)
            if (!vehicleExists) {
                Toast.makeText(
                    this,
                    "Vehicle '$vehicleNumber' is not available. Please add the vehicle details first.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                val isUpdated = dbHelper.updateInsuranceData(
                    insuranceId,
                    vehicleNumber,
                    company,
                    policyNumber,
                    expiryDate,
                    premium
                )
                if (isUpdated) {
                    Toast.makeText(this, "Insurance updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update insurance", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (userEmail.isNotEmpty()) {
                    val isInserted = dbHelper.insertInsuranceData(
                        userEmail,
                        vehicleNumber,
                        company,
                        policyNumber,
                        expiryDate,
                        premium
                    )
                    if (isInserted) {
                        Toast.makeText(this, "Insurance added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to add insurance", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User error. Please login again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

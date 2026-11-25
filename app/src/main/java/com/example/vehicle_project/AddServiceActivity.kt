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

class AddServiceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        val isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        val serviceId = intent.getIntExtra("SERVICE_ID", -1)

        val tvTitle = findViewById<TextView>(R.id.tv_service_form_title)
        val etVehicleNumber = findViewById<EditText>(R.id.et_service_vehicle_number)
        val etServiceType = findViewById<EditText>(R.id.et_service_type)
        val etServiceDate = findViewById<EditText>(R.id.et_service_date)
        val etServiceCost = findViewById<EditText>(R.id.et_service_cost)
        val btnSave = findViewById<Button>(R.id.btn_save_service)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        val calendar = Calendar.getInstance()

        // Setup DatePicker for Service Date
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, monthOfYear)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            etServiceDate.setText(sdf.format(calendar.time))
        }

        etServiceDate.setOnClickListener {
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
            tvTitle.text = "Update Service"
            btnSave.text = "Update Service"

            etVehicleNumber.setText(intent.getStringExtra("SERVICE_VEHICLE_NUMBER") ?: "")
            etServiceType.setText(intent.getStringExtra("SERVICE_TYPE") ?: "")
            val existingDate = intent.getStringExtra("SERVICE_DATE") ?: ""
            etServiceDate.setText(existingDate)
            etServiceCost.setText(intent.getStringExtra("SERVICE_COST") ?: "")

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
                val intent = Intent(this, ServiceActivity::class.java)
                intent.putExtra("USER_EMAIL", userEmail)
                startActivity(intent)
            }
            finish()
        }

        btnSave.setOnClickListener {
            val vehicleNumber = etVehicleNumber.text.toString().trim()
            val serviceType = etServiceType.text.toString().trim()
            val serviceDate = etServiceDate.text.toString().trim()
            val serviceCost = etServiceCost.text.toString().trim()

            if (vehicleNumber.isEmpty() || serviceType.isEmpty() || serviceDate.isEmpty() || serviceCost.isEmpty()) {
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
                val isUpdated = dbHelper.updateServiceData(
                    serviceId,
                    vehicleNumber,
                    serviceType,
                    serviceDate,
                    serviceCost
                )
                if (isUpdated) {
                    Toast.makeText(this, "Service updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update service", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (userEmail.isNotEmpty()) {
                    val isInserted = dbHelper.insertServiceData(
                        userEmail,
                        vehicleNumber,
                        serviceType,
                        serviceDate,
                        serviceCost
                    )
                    if (isInserted) {
                        Toast.makeText(this, "Service added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to add service", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User error. Please login again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

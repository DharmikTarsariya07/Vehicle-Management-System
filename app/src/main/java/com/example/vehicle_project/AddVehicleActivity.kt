package com.example.vehicle_project

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddVehicleActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_vehicle)

        dbHelper = DatabaseHelper(this)
        val userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val isEditMode = intent.getBooleanExtra("IS_EDIT_MODE", false)
        val vehicleId = intent.getIntExtra("VEHICLE_ID", -1)

        val tvTitle = findViewById<TextView>(R.id.tv_add_vehicle_title)
        val etName = findViewById<EditText>(R.id.et_vehicle_name)
        val etNumber = findViewById<EditText>(R.id.et_vehicle_number)
        val etType = findViewById<EditText>(R.id.et_vehicle_type)
        val etBrand = findViewById<EditText>(R.id.et_brand)
        val etFuel = findViewById<EditText>(R.id.et_fuel_type)
        val btnSave = findViewById<Button>(R.id.btn_save_vehicle)
        val btnBack = findViewById<ImageView>(R.id.btn_back)

        if (isEditMode) {
            tvTitle.text = "Update Vehicle"
            btnSave.text = "Update Vehicle"

            etName.setText(intent.getStringExtra("VEHICLE_NAME") ?: "")
            etNumber.setText(intent.getStringExtra("VEHICLE_NUMBER") ?: "")
            etType.setText(intent.getStringExtra("VEHICLE_TYPE") ?: "")
            etBrand.setText(intent.getStringExtra("VEHICLE_BRAND") ?: "")
            etFuel.setText(intent.getStringExtra("VEHICLE_FUEL") ?: "")
        }

        btnBack.setOnClickListener {
            if (isTaskRoot) {
                val homeIntent = Intent(this, HomeActivity::class.java)
                homeIntent.putExtra("USER_EMAIL", userEmail)
                startActivity(homeIntent)
            }
            finish()
        }

        btnSave.setOnClickListener {
            val name = etName.text.toString().trim()
            val number = etNumber.text.toString().trim()
            val type = etType.text.toString().trim()
            val brand = etBrand.text.toString().trim()
            val fuel = etFuel.text.toString().trim()

            if (name.isEmpty() || number.isEmpty() || type.isEmpty() || brand.isEmpty() || fuel.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isEditMode) {
                val isUpdated = dbHelper.updateVehicleData(vehicleId, name, number, type, brand, fuel)
                if (isUpdated) {
                    Toast.makeText(this, "Vehicle updated successfully", Toast.LENGTH_SHORT).show()
                    finish()
                } else {
                    Toast.makeText(this, "Failed to update vehicle", Toast.LENGTH_SHORT).show()
                }
            } else {
                if (userEmail.isNotEmpty()) {
                    val isInserted = dbHelper.insertVehicleData(userEmail, name, number, type, brand, fuel)
                    if (isInserted) {
                        Toast.makeText(this, "Vehicle added successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Failed to add vehicle", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "User error. Please login again.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}

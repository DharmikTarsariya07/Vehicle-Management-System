package com.example.vehicle_project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class HomeActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvVehicles: RecyclerView
    private lateinit var tvEmptyVehicles: TextView
    private lateinit var vehicleAdapter: VehicleAdapter
    private var vehicleList = ArrayList<Vehicle>()
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""
        
        val tvGreeting = findViewById<TextView>(R.id.tv_greeting)
        val btnAddVehicle = findViewById<Button>(R.id.btn_add_vehicle)
        rvVehicles = findViewById(R.id.rv_vehicles)
        tvEmptyVehicles = findViewById(R.id.tv_empty_vehicles)
        
        rvVehicles.layoutManager = LinearLayoutManager(this)

        if (userEmail.isNotEmpty()) {
            val username = dbHelper.getUsernameByEmail(userEmail)
            tvGreeting.text = "Hello, $username"
        }

        btnAddVehicle.setOnClickListener {
            val intent = Intent(this, AddVehicleActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_insurance).setOnClickListener {
            val intent = Intent(this, InsuranceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_service).setOnClickListener {
            val intent = Intent(this, ServiceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        loadVehicles()
    }

    private fun loadVehicles() {
        try {
            vehicleList = dbHelper.getVehiclesByUser(userEmail)
            vehicleAdapter = VehicleAdapter(
                vehicleList,
                onUpdateClick = { vehicle ->
                    val intent = Intent(this, AddVehicleActivity::class.java).apply {
                        putExtra("USER_EMAIL", userEmail)
                        putExtra("IS_EDIT_MODE", true)
                        putExtra("VEHICLE_ID", vehicle.id)
                        putExtra("VEHICLE_NAME", vehicle.name)
                        putExtra("VEHICLE_NUMBER", vehicle.number)
                        putExtra("VEHICLE_TYPE", vehicle.type)
                        putExtra("VEHICLE_BRAND", vehicle.brand)
                        putExtra("VEHICLE_FUEL", vehicle.fuelType)
                    }
                    startActivity(intent)
                },
                onDeleteClick = { vehicle ->
                    showDeleteConfirmationDialog(vehicle)
                }
            )
            rvVehicles.adapter = vehicleAdapter
            updateEmptyView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDeleteConfirmationDialog(vehicle: Vehicle) {
        AlertDialog.Builder(this)
            .setTitle("Delete Vehicle")
            .setMessage("Are you sure you want to delete ${vehicle.name}?")
            .setPositiveButton("Yes") { dialog, _ ->
                val isDeleted = dbHelper.deleteVehicleData(vehicle.id)
                if (isDeleted) {
                    Toast.makeText(this, "Vehicle deleted successfully", Toast.LENGTH_SHORT).show()
                    loadVehicles()
                } else {
                    Toast.makeText(this, "Failed to delete vehicle", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun updateEmptyView() {
        if (vehicleList.isEmpty()) {
            tvEmptyVehicles.visibility = View.VISIBLE
            rvVehicles.visibility = View.GONE
        } else {
            tvEmptyVehicles.visibility = View.GONE
            rvVehicles.visibility = View.VISIBLE
        }
    }
}

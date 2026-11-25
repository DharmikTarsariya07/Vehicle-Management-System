package com.example.vehicle_project

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ServiceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvService: RecyclerView
    private lateinit var tvEmptyService: TextView
    private lateinit var serviceAdapter: ServiceAdapter
    private var serviceList = ArrayList<ServiceRecord>()
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_service)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val btnAddService = findViewById<Button>(R.id.btn_add_service)
        rvService = findViewById(R.id.rv_service)
        tvEmptyService = findViewById(R.id.tv_empty_service)

        rvService.layoutManager = LinearLayoutManager(this)

        btnAddService.setOnClickListener {
            val intent = Intent(this, AddServiceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        // Bottom Navigation Tabs
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

        findViewById<View>(R.id.nav_profile).setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadServices()
    }

    private fun loadServices() {
        try {
            serviceList = dbHelper.getServicesByUser(userEmail)
            serviceAdapter = ServiceAdapter(
                serviceList,
                onUpdateClick = { record ->
                    val intent = Intent(this, AddServiceActivity::class.java).apply {
                        putExtra("USER_EMAIL", userEmail)
                        putExtra("IS_EDIT_MODE", true)
                        putExtra("SERVICE_ID", record.id)
                        putExtra("SERVICE_VEHICLE_NUMBER", record.vehicleNumber)
                        putExtra("SERVICE_TYPE", record.serviceType)
                        putExtra("SERVICE_DATE", record.serviceDate)
                        putExtra("SERVICE_COST", record.serviceCost)
                    }
                    startActivity(intent)
                },
                onDeleteClick = { record ->
                    showDeleteConfirmationDialog(record)
                }
            )
            rvService.adapter = serviceAdapter
            updateEmptyView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDeleteConfirmationDialog(record: ServiceRecord) {
        AlertDialog.Builder(this)
            .setTitle("Delete Service Record")
            .setMessage("Are you sure you want to delete ${record.serviceType} for ${record.vehicleNumber}?")
            .setPositiveButton("Yes") { dialog, _ ->
                val isDeleted = dbHelper.deleteServiceData(record.id)
                if (isDeleted) {
                    Toast.makeText(this, "Service record deleted", Toast.LENGTH_SHORT).show()
                    loadServices()
                } else {
                    Toast.makeText(this, "Failed to delete service record", Toast.LENGTH_SHORT).show()
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
        if (serviceList.isEmpty()) {
            tvEmptyService.visibility = View.VISIBLE
            rvService.visibility = View.GONE
        } else {
            tvEmptyService.visibility = View.GONE
            rvService.visibility = View.VISIBLE
        }
    }
}

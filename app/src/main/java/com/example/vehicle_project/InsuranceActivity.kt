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

class InsuranceActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var rvInsurance: RecyclerView
    private lateinit var tvEmptyInsurance: TextView
    private lateinit var insuranceAdapter: InsuranceAdapter
    private var insuranceList = ArrayList<Insurance>()
    private var userEmail: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_insurance)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dbHelper = DatabaseHelper(this)
        userEmail = intent.getStringExtra("USER_EMAIL") ?: ""

        val btnAddInsurance = findViewById<Button>(R.id.btn_add_insurance)
        val navHome = findViewById<View>(R.id.nav_home)
        val navService = findViewById<View>(R.id.nav_service)
        val navProfile = findViewById<View>(R.id.nav_profile)
        rvInsurance = findViewById(R.id.rv_insurance)
        tvEmptyInsurance = findViewById(R.id.tv_empty_insurance)

        rvInsurance.layoutManager = LinearLayoutManager(this)

        btnAddInsurance.setOnClickListener {
            val intent = Intent(this, AddInsuranceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
        }

        navHome.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }

        navService.setOnClickListener {
            val intent = Intent(this, ServiceActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }

        navProfile.setOnClickListener {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("USER_EMAIL", userEmail)
            startActivity(intent)
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        loadInsurances()
    }

    private fun loadInsurances() {
        try {
            insuranceList = dbHelper.getInsurancesByUser(userEmail)
            insuranceAdapter = InsuranceAdapter(
                insuranceList,
                onUpdateClick = { insurance ->
                    val intent = Intent(this, AddInsuranceActivity::class.java).apply {
                        putExtra("USER_EMAIL", userEmail)
                        putExtra("IS_EDIT_MODE", true)
                        putExtra("INSURANCE_ID", insurance.id)
                        putExtra("INSURANCE_VEHICLE_NUMBER", insurance.vehicleNumber)
                        putExtra("INSURANCE_COMPANY", insurance.company)
                        putExtra("INSURANCE_POLICY_NUMBER", insurance.policyNumber)
                        putExtra("INSURANCE_EXPIRY_DATE", insurance.expiryDate)
                        putExtra("INSURANCE_PREMIUM", insurance.premiumAmount)
                    }
                    startActivity(intent)
                },
                onDeleteClick = { insurance ->
                    showDeleteConfirmationDialog(insurance)
                }
            )
            rvInsurance.adapter = insuranceAdapter
            updateEmptyView()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showDeleteConfirmationDialog(insurance: Insurance) {
        AlertDialog.Builder(this)
            .setTitle("Delete Insurance")
            .setMessage("Are you sure you want to delete policy ${insurance.policyNumber} for ${insurance.vehicleNumber}?")
            .setPositiveButton("Yes") { dialog, _ ->
                val isDeleted = dbHelper.deleteInsuranceData(insurance.id)
                if (isDeleted) {
                    Toast.makeText(this, "Insurance policy deleted", Toast.LENGTH_SHORT).show()
                    loadInsurances()
                } else {
                    Toast.makeText(this, "Failed to delete insurance policy", Toast.LENGTH_SHORT).show()
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
        if (insuranceList.isEmpty()) {
            tvEmptyInsurance.visibility = View.VISIBLE
            rvInsurance.visibility = View.GONE
        } else {
            tvEmptyInsurance.visibility = View.GONE
            rvInsurance.visibility = View.VISIBLE
        }
    }
}

package com.example.vehicle_project

data class Insurance(
    val id: Int,
    val vehicleNumber: String,
    val company: String,
    val policyNumber: String,
    val expiryDate: String,
    val premiumAmount: String
)

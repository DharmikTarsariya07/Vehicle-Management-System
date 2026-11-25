package com.example.vehicle_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class InsuranceAdapter(
    private val insuranceList: ArrayList<Insurance>,
    private val onUpdateClick: (Insurance) -> Unit,
    private val onDeleteClick: (Insurance) -> Unit
) : RecyclerView.Adapter<InsuranceAdapter.InsuranceViewHolder>() {

    class InsuranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvCompany: TextView = itemView.findViewById(R.id.tv_insurance_company)
        val tvVehicleNumber: TextView = itemView.findViewById(R.id.tv_insurance_vehicle_number)
        val tvPolicyNumber: TextView = itemView.findViewById(R.id.tv_insurance_policy_number)
        val tvExpiry: TextView = itemView.findViewById(R.id.tv_insurance_expiry)
        val tvPremium: TextView = itemView.findViewById(R.id.tv_insurance_premium)
        val btnUpdate: ImageView = itemView.findViewById(R.id.btn_update_insurance)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_insurance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InsuranceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_insurance, parent, false)
        return InsuranceViewHolder(view)
    }

    override fun onBindViewHolder(holder: InsuranceViewHolder, position: Int) {
        val currentItem = insuranceList[position]
        holder.tvCompany.text = currentItem.company
        holder.tvVehicleNumber.text = "Vehicle: ${currentItem.vehicleNumber}"
        holder.tvPolicyNumber.text = "Policy: ${currentItem.policyNumber}"
        holder.tvExpiry.text = "Expires: ${currentItem.expiryDate}"
        holder.tvPremium.text = "₹ ${currentItem.premiumAmount}"

        holder.btnUpdate.setOnClickListener {
            onUpdateClick(currentItem)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return insuranceList.size
    }
}

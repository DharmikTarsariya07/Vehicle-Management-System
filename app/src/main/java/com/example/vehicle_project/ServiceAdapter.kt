package com.example.vehicle_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ServiceAdapter(
    private val serviceList: ArrayList<ServiceRecord>,
    private val onUpdateClick: (ServiceRecord) -> Unit,
    private val onDeleteClick: (ServiceRecord) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvType: TextView = itemView.findViewById(R.id.tv_service_type)
        val tvVehicleNumber: TextView = itemView.findViewById(R.id.tv_service_vehicle_number)
        val tvDate: TextView = itemView.findViewById(R.id.tv_service_date)
        val tvCost: TextView = itemView.findViewById(R.id.tv_service_cost)
        val btnUpdate: ImageView = itemView.findViewById(R.id.btn_update_service)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_service)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val currentItem = serviceList[position]
        holder.tvType.text = currentItem.serviceType
        holder.tvVehicleNumber.text = "Vehicle: ${currentItem.vehicleNumber}"
        holder.tvDate.text = "Date: ${currentItem.serviceDate}"
        holder.tvCost.text = "₹ ${currentItem.serviceCost}"

        holder.btnUpdate.setOnClickListener {
            onUpdateClick(currentItem)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return serviceList.size
    }
}

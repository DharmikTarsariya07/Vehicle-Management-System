package com.example.vehicle_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class VehicleAdapter(
    private val vehicleList: ArrayList<Vehicle>,
    private val onUpdateClick: (Vehicle) -> Unit,
    private val onDeleteClick: (Vehicle) -> Unit
) : RecyclerView.Adapter<VehicleAdapter.VehicleViewHolder>() {

    class VehicleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_vehicle_name)
        val tvNumber: TextView = itemView.findViewById(R.id.tv_vehicle_number)
        val tvDetails: TextView = itemView.findViewById(R.id.tv_vehicle_details)
        val btnUpdate: ImageView = itemView.findViewById(R.id.btn_update_vehicle)
        val btnDelete: ImageView = itemView.findViewById(R.id.btn_delete_vehicle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VehicleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vehicle, parent, false)
        return VehicleViewHolder(view)
    }

    override fun onBindViewHolder(holder: VehicleViewHolder, position: Int) {
        val currentItem = vehicleList[position]
        holder.tvName.text = currentItem.name
        holder.tvNumber.text = currentItem.number
        holder.tvDetails.text = "${currentItem.brand} | ${currentItem.type} | ${currentItem.fuelType}"

        holder.btnUpdate.setOnClickListener {
            onUpdateClick(currentItem)
        }

        holder.btnDelete.setOnClickListener {
            onDeleteClick(currentItem)
        }
    }

    override fun getItemCount(): Int {
        return vehicleList.size
    }
}

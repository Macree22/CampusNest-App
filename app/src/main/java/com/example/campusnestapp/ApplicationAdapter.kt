package com.example.campusnestapp

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView

class ApplicationAdapter(private val apps: List<UserApplication>) : RecyclerView.Adapter<ApplicationAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvAppTitle)
        val tvAddress: TextView = view.findViewById(R.id.tvAppAddress)
        val tvStatus: TextView = view.findViewById(R.id.tvAppStatus)
        val tvDate: TextView = view.findViewById(R.id.tvAppDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]
        val context = holder.itemView.context

        holder.tvTitle.text = app.title
        holder.tvAddress.text = app.address
        holder.tvDate.text = "Move-in: ${app.preferredMoveInDate}"
        holder.tvStatus.text = app.applicationStatus

        // Apply Color Coding based on status
        when (app.applicationStatus.lowercase()) {
            "approved" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved)
                holder.tvStatus.setTextColor(Color.parseColor("#047857")) // Dark Green
            }
            "rejected" -> {
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected)
                holder.tvStatus.setTextColor(Color.parseColor("#B91C1C")) // Dark Red
            }
            else -> { // Pending
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.tvStatus.setTextColor(Color.parseColor("#B45309")) // Dark Amber
            }
        }
    }

    override fun getItemCount() = apps.size
}
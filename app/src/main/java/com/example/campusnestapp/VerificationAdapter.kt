package com.example.campusnestapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

@SuppressLint("SetTextI18n", "NotifyDataSetChanged") // Silences the string formatting warnings
class VerificationAdapter(
    private var listings: List<PendingListing>,
    private val onVerifyClick: (Int) -> Unit
) : RecyclerView.Adapter<VerificationAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvVerifyTitle)
        val tvAddress: TextView = view.findViewById(R.id.tvVerifyAddress)
        val tvOwner: TextView = view.findViewById(R.id.tvVerifyOwner)
        val tvRent: TextView = view.findViewById(R.id.tvVerifyRent)
        val btnVerify: Button = view.findViewById(R.id.btnVerify)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_verification, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        // FIXED: Changed ')' to ']' here
        val item = listings[position]

        holder.tvTitle.text = item.title
        holder.tvAddress.text = "${item.address}, ${item.city}"
        holder.tvOwner.text = "Owner: ${item.firstName} ${item.lastName}"
        holder.tvRent.text = "R ${item.monthlyRent.toInt()} / month"

        holder.btnVerify.setOnClickListener {
            onVerifyClick(item.listingID)
        }
    }

    override fun getItemCount() = listings.size

    fun updateData(newListings: List<PendingListing>) {
        listings = newListings
        notifyDataSetChanged()
    }
}
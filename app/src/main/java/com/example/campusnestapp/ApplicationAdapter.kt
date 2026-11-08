package com.example.campusnestapp

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ApplicationAdapter(
    private val apps: List<UserApplication>,
    private val onAcceptLease: (Int) -> Unit,
    private val onViewLease: (Int) -> Unit,
    private val onPrintReceipt: (Int) -> Unit
) : RecyclerView.Adapter<ApplicationAdapter.AppViewHolder>() {

    class AppViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvAppTitle)
        val tvAddress: TextView = view.findViewById(R.id.tvAppAddress)
        val tvStatus: TextView = view.findViewById(R.id.tvAppStatus)
        val tvDate: TextView = view.findViewById(R.id.tvAppDate)
        val btnSignLease: Button = view.findViewById(R.id.btnSignLease)
        val btnViewLease: Button = view.findViewById(R.id.btnViewLease)
        val btnPrintReceipt: Button = view.findViewById(R.id.btnPrintReceipt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_application, parent, false)
        return AppViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
        val app = apps[position]

        holder.tvTitle.text = app.title
        holder.tvAddress.text = app.address
        holder.tvDate.text = "Move-in: ${app.preferredMoveInDate}"

        val status = app.applicationStatus
        val isApproved = status.equals("Approved", ignoreCase = true)
        val isSigned = app.studentAccepted == 1
        val effectiveBookingID = app.bookingID ?: app.applicationID

        // Color coding & badge text
        when (status.lowercase()) {
            "approved" -> {
                if (isSigned) {
                    holder.tvStatus.text = "Approved (Signed)"
                } else {
                    holder.tvStatus.text = "Approved"
                }
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_approved)
                holder.tvStatus.setTextColor(Color.parseColor("#047857")) // Dark Green
            }
            "rejected" -> {
                holder.tvStatus.text = app.applicationStatus
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_rejected)
                holder.tvStatus.setTextColor(Color.parseColor("#B91C1C")) // Dark Red
            }
            else -> { // Pending / Cancelled / etc.
                holder.tvStatus.text = app.applicationStatus
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_pending)
                holder.tvStatus.setTextColor(Color.parseColor("#B45309")) // Dark Amber
            }
        }

        // Button Visibility & Action Logic
        if (isApproved) {

            if (!isSigned) {
                // Show "Sign Lease" button for approved applications that aren't signed yet
                holder.btnSignLease.visibility = View.VISIBLE
                holder.btnSignLease.text = "Sign Lease"
                holder.btnSignLease.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4F46E5")) // Purple
                holder.btnSignLease.setOnClickListener {
                    onAcceptLease(effectiveBookingID)
                }
                holder.btnViewLease.visibility = View.GONE
                holder.tvStatus.setOnClickListener(null)
            } else {
                // Show "View Lease" button for signed applications
                holder.btnSignLease.visibility = View.GONE
                holder.btnViewLease.visibility = View.VISIBLE
                holder.btnViewLease.text = "View Lease"
                holder.btnViewLease.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4F46E5")) // Purple
                holder.btnViewLease.setOnClickListener {
                    onViewLease(effectiveBookingID)
                }
                holder.tvStatus.setOnClickListener {
                    onViewLease(effectiveBookingID)
                }
            }

            // Print Receipt button (if paymentID is available)
            if (app.paymentID != null) {
                holder.btnPrintReceipt.visibility = View.VISIBLE
                holder.btnPrintReceipt.text = "Print Receipt"
                holder.btnPrintReceipt.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#10B981")) // Green
                holder.btnPrintReceipt.setOnClickListener {
                    onPrintReceipt(app.paymentID)
                }
            } else {
                holder.btnPrintReceipt.visibility = View.GONE
            }

        } else {
            // Hide all buttons for non-approved applications
            holder.btnSignLease.visibility = View.GONE
            holder.btnViewLease.visibility = View.GONE
            holder.btnPrintReceipt.visibility = View.GONE
            holder.tvStatus.setOnClickListener(null)
        }
    }

    override fun getItemCount() = apps.size
}

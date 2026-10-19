package com.example.campusnestapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class ListingAdapter(private var listings: List<Listing>) :
    RecyclerView.Adapter<ListingAdapter.ListingViewHolder>() {

    class ListingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgListing: ImageView = itemView.findViewById(R.id.imgListing)
        val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvRoomType: TextView = itemView.findViewById(R.id.tvRoomType)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListingViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_listing, parent, false)
        return ListingViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListingViewHolder, position: Int) {
        val listing = listings[position]

        holder.tvTitle.text = listing.title
        holder.tvAddress.text = "${listing.address}, ${listing.city}"
        holder.tvPrice.text = "R ${listing.monthlyRent.toInt()} / month"
        holder.tvRoomType.text = listing.roomType

        // Use Glide to load the image from the URL
        Glide.with(holder.itemView.context)
            .load(listing.imagePath)
            .placeholder(R.color.primary_light) // Shows while loading
            .into(holder.imgListing)
    }

    override fun getItemCount(): Int = listings.size

    // Helper function to update the list
    fun updateData(newListings: List<Listing>) {
        listings = newListings
        notifyDataSetChanged()
    }
}
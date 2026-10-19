package com.example.campusnestapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide

@SuppressLint("SetTextI18n") // Suppresses the text concatenation warnings
class ListingDetailActivity : AppCompatActivity() {

    @Suppress("DEPRECATION") // Suppresses the getSerializableExtra deprecation warning
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listing_detail)

        // Retrieve the listing object from the Intent
        val listing = intent.getSerializableExtra("LISTING_DATA") as? Listing

        if (listing != null) {
            findViewById<TextView>(R.id.tvDetailTitle).text = listing.title
            findViewById<TextView>(R.id.tvDetailAddress).text = "${listing.address}, ${listing.city}"
            findViewById<TextView>(R.id.tvDetailRoomType).text = listing.roomType
            findViewById<TextView>(R.id.tvDetailPrice).text = "R ${listing.monthlyRent.toInt()} / m"
            findViewById<TextView>(R.id.tvDetailAmenities).text = listing.amenities ?: "WiFi, Security, Water Included"

            Glide.with(this)
                .load(listing.imagePath)
                .into(findViewById<ImageView>(R.id.imgDetailHero))

            // Go back to the previous screen when clicking the image
            findViewById<ImageView>(R.id.imgDetailHero).setOnClickListener {
                finish()
            }

            // Handle Apply Button
            findViewById<Button>(R.id.btnDetailApply).setOnClickListener {
                // You can either trigger the API call here, or finish() to go back to the list
                finish()
            }
        }
    }
}
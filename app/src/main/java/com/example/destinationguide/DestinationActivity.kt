package com.example.destinationguide

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast

class DestinationActivity : Activity() {

    private lateinit var tvTitle: TextView
    private lateinit var tvDescription: TextView
    private lateinit var ivDestination: ImageView

    private lateinit var btnReadMore: Button
    private lateinit var btnBackHome: Button

    private var articleUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_destination)

        // Initialize views

        tvTitle = findViewById(R.id.tvTitle)
        tvDescription = findViewById(R.id.tvDescription)
        ivDestination = findViewById(R.id.ivDestination)

        btnReadMore = findViewById(R.id.btnReadMore)
        btnBackHome = findViewById(R.id.btnBackHome)

        // Receive data from MainActivity

        val title = intent.getStringExtra("title")
            ?: "Unknown Destination"

        val description = intent.getStringExtra("description")
            ?: "No description available."

        articleUrl = intent.getStringExtra("articleUrl") ?: ""

        // Display destination details

        tvTitle.text = title
        tvDescription.text = description

        // Receive and display destination image

        val bitmap: Bitmap? = if (Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("image", Bitmap::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("image")
        }

        if (bitmap != null) {
            ivDestination.setImageBitmap(bitmap)
        } else {
            ivDestination.setImageResource(
                android.R.drawable.ic_menu_gallery
            )
        }

        // Read More button

        btnReadMore.setOnClickListener {

            if (articleUrl.isNotEmpty()) {

                val webIntent = Intent(
                    this,
                    WebActivity::class.java
                )

                webIntent.putExtra("articleUrl", articleUrl)

                startActivity(webIntent)

            } else {

                Toast.makeText(
                    this,
                    "Article is not available",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        // Back to Home button

        btnBackHome.setOnClickListener {
            finish()
        }
    }
}
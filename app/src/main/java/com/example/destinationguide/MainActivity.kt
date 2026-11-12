package com.example.destinationguide

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.view.View
import android.widget.*
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MainActivity : Activity() {

    private lateinit var etDestination: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var searchProgress: ProgressBar

    private lateinit var cardParis: LinearLayout
    private lateinit var cardTaj: LinearLayout
    private lateinit var cardBali: LinearLayout

    private val executor = Executors.newSingleThreadExecutor()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        try {
            etDestination = findViewById(R.id.etDestination)
            btnSearch = findViewById(R.id.btnSearch)
            searchProgress = findViewById(R.id.searchProgress)

            cardParis = findViewById(R.id.cardParis)
            cardTaj = findViewById(R.id.cardTaj)
            cardBali = findViewById(R.id.cardBali)

            btnSearch.setOnClickListener {
                val destination = etDestination.text.toString().trim()

                if (destination.isEmpty()) {
                    etDestination.error = "Enter destination name"
                } else {
                    searchDestination(destination)
                }
            }

            cardParis.setOnClickListener {
                searchDestination("Paris")
            }

            cardTaj.setOnClickListener {
                searchDestination("Taj Mahal")
            }

            cardBali.setOnClickListener {
                searchDestination("Bali")
            }

        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Home screen error: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun searchDestination(destination: String) {

        btnSearch.isEnabled = false
        searchProgress.visibility = View.VISIBLE

        executor.execute {

            var connection: HttpURLConnection? = null

            try {
                val apiUrl =
                    "https://en.wikipedia.org/api/rest_v1/page/summary/" +
                            Uri.encode(destination.replace(" ", "_"))

                connection = URL(apiUrl).openConnection() as HttpURLConnection

                connection.requestMethod = "GET"
                connection.connectTimeout = 15000
                connection.readTimeout = 15000

                connection.setRequestProperty(
                    "User-Agent",
                    "DestinationGuide/1.0 (Android application)"
                )

                val responseCode = connection.responseCode

                if (responseCode != 200) {
                    throw Exception("Destination not found. Try another name.")
                }

                val response = connection.inputStream.bufferedReader().use {
                    it.readText()
                }

                val json = JSONObject(response)

                val title = json.optString("title", destination)

                val description = json.optString(
                    "extract",
                    "No description available."
                )

                val imageUrl = json.optJSONObject("thumbnail")
                    ?.optString("source", "") ?: ""

                val articleUrl = json.optJSONObject("content_urls")
                    ?.optJSONObject("desktop")
                    ?.optString("page", "") ?: ""

                var bitmap: Bitmap? = null

                if (imageUrl.isNotEmpty()) {
                    try {
                        val imageConnection =
                            URL(imageUrl).openConnection() as HttpURLConnection

                        imageConnection.connectTimeout = 10000
                        imageConnection.readTimeout = 10000

                        imageConnection.setRequestProperty(
                            "User-Agent",
                            "DestinationGuide/1.0"
                        )

                        bitmap = imageConnection.inputStream.use {
                            BitmapFactory.decodeStream(it)
                        }

                        imageConnection.disconnect()

                    } catch (e: Exception) {
                        bitmap = null
                    }
                }

                runOnUiThread {

                    searchProgress.visibility = View.GONE
                    btnSearch.isEnabled = true

                    val intent = Intent(
                        this,
                        DestinationActivity::class.java
                    )

                    intent.putExtra("title", title)
                    intent.putExtra("description", description)
                    intent.putExtra("articleUrl", articleUrl)
                    intent.putExtra("image", bitmap)

                    startActivity(intent)
                }

            } catch (e: Exception) {

                runOnUiThread {

                    searchProgress.visibility = View.GONE
                    btnSearch.isEnabled = true

                    Toast.makeText(
                        this,
                        e.message ?: "Unable to load destination",
                        Toast.LENGTH_LONG
                    ).show()
                }

            } finally {
                connection?.disconnect()
            }
        }
    }

    override fun onDestroy() {
        executor.shutdownNow()
        super.onDestroy()
    }
}
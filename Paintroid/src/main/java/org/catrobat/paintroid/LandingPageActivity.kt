package org.catrobat.paintroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class LandingPageActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocketpaint_landing_page)

        val imagePreview = findViewById<ImageView>(R.id.pocketpaint_image_preview)
        val editCircleIcon = findViewById<ImageView>(R.id.pocketpaint_image_edit_circle)

        val imagePreviewClickListener = View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imagePreview.setOnClickListener(imagePreviewClickListener)
        editCircleIcon.setOnClickListener(imagePreviewClickListener)
    }
}
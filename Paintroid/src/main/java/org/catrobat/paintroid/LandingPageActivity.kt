package org.catrobat.paintroid

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.catrobat.paintroid.data.local.database.ProjectDatabase
import org.catrobat.paintroid.data.local.database.ProjectDatabaseProvider

class LandingPageActivity: AppCompatActivity() {
    companion object {
        lateinit var projectDB: ProjectDatabase
        val FAB_ACTION = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocketpaint_landing_page)

        projectDB = ProjectDatabaseProvider.getDatabase(applicationContext)

        val imagePreview = findViewById<ImageView>(R.id.pocketpaint_image_preview)
        val editCircleIcon = findViewById<ImageView>(R.id.pocketpaint_image_edit_circle)

        val imagePreviewClickListener = View.OnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        imagePreview.setOnClickListener(imagePreviewClickListener)
        editCircleIcon.setOnClickListener(imagePreviewClickListener)

        val mainActivityIntent = Intent(this, MainActivity::class.java)

        val newImage = findViewById<FloatingActionButton>(R.id.pocketpaint_fab_new_image)
        newImage.setOnClickListener {
            mainActivityIntent.putExtra(FAB_ACTION, "new_image")
            startActivity(mainActivityIntent)
        }

        val loadImage = findViewById<FloatingActionButton>(R.id.pocketpaint_fab_load_image)
        loadImage.setOnClickListener {
            mainActivityIntent.putExtra(FAB_ACTION, "load_image")
            startActivity(mainActivityIntent)
        }
    }
}
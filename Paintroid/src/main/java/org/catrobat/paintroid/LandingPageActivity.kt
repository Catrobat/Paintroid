package org.catrobat.paintroid

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.catrobat.paintroid.adapter.ProjectAdapter
import org.catrobat.paintroid.data.local.database.ProjectDatabase
import org.catrobat.paintroid.data.local.database.ProjectDatabaseProvider
import org.catrobat.paintroid.model.Project

class LandingPageActivity: AppCompatActivity() {
    companion object {
        lateinit var projectDB: ProjectDatabase
        private lateinit var projectsRecyclerView: RecyclerView
        private lateinit var projectsList: ArrayList<Project>
        lateinit var projectAdapter: ProjectAdapter
        var latestProject: Project? = null
        lateinit var imagePreview: ImageView
        val FAB_ACTION = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pocketpaint_landing_page)

        projectDB = ProjectDatabaseProvider.getDatabase(applicationContext)
        val allProjects = projectDB.dao.getProjects()
        latestProject = allProjects.firstOrNull()

        init()

        imagePreview = findViewById(R.id.pocketpaint_image_preview)
        val editCircleIcon = findViewById<ImageView>(R.id.pocketpaint_image_edit_circle)

        latestProject?.let {
            imagePreview.setImageURI(Uri.parse(it.imagePreviewPath))
            imagePreview.scaleType = ImageView.ScaleType.CENTER
        }

        val imagePreviewClickListener = View.OnClickListener {
            if (allProjects.isNotEmpty()){
                val loadProjectIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    putExtra(FAB_ACTION, "load_project")
                    putExtra("PROJECT_URI", latestProject?.path)
                    putExtra("PROJECT_NAME", latestProject?.name)
                    putExtra("PROJECT_IMAGE_PREVIEW_URI", latestProject?.imagePreviewPath)
                }
                startActivity(loadProjectIntent)
            }else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
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

    private fun init() {
        projectsRecyclerView = findViewById(R.id.pocketpaint_projects_list)
        projectsRecyclerView.layoutManager = LinearLayoutManager(this)

        projectsList = ArrayList()
        projectDB.dao.getProjects().forEach {
            projectsList.add(it)
        }

        projectAdapter = ProjectAdapter(projectsList, supportFragmentManager)
        projectsRecyclerView.adapter = projectAdapter

        projectAdapter.setOnItemClickListener(object: ProjectAdapter.OnItemClickListener{
            override fun onItemClick(
                position: Int,
                projectUri: String,
                projectName: String,
                projectImagePreviewUri: String
            ) {
                val loadProjectIntent = Intent(applicationContext, MainActivity::class.java).apply {
                    putExtra(FAB_ACTION, "load_project")
                    putExtra("PROJECT_URI", projectUri)
                    putExtra("PROJECT_NAME", projectName)
                    putExtra("PROJECT_IMAGE_PREVIEW_URI", projectImagePreviewUri)
                }
                startActivity(loadProjectIntent)
            }
        })
    }
}
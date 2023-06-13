package org.catrobat.paintroid.adapter

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import org.catrobat.paintroid.LandingPageActivity.Companion.imagePreview
import org.catrobat.paintroid.LandingPageActivity.Companion.latestProject
import org.catrobat.paintroid.R
import org.catrobat.paintroid.common.PROJECT_DELETE_DIALOG_FRAGMENT_TAG
import org.catrobat.paintroid.common.PROJECT_DETAILS_DIALOG_FRAGMENT_TAG
import org.catrobat.paintroid.dialog.ProjectDeleteDialog
import org.catrobat.paintroid.dialog.ProjectDetailsDialog
import org.catrobat.paintroid.model.Project
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ProjectAdapter(
    val context: Context,
    var projectList: ArrayList<Project>,
    private val supportFragmentManager: FragmentManager
) : RecyclerView.Adapter<ProjectAdapter.ItemViewHolder>() {
    private var itemClickListener: OnItemClickListener? = null

    companion object {
        private val TAG = ProjectAdapter::class.java.simpleName
        private const val MEGABYTE = 1.0
        private const val KILOBYTE = 1024
    }

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemImageView: ImageView = itemView.findViewById(R.id.iv_pocket_paint_project_thumbnail_image)
        val itemNameText: TextView = itemView.findViewById(R.id.tv_pocket_paint_project_name)
        val itemLastModifiedText: TextView = itemView.findViewById(R.id.tv_pocket_paint_project_lastmodified)
        val itemMoreOption: ImageView = itemView.findViewById(R.id.iv_pocket_paint_project_more)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.pocketpaint_item_project, parent, false)
        return ItemViewHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = projectList[position]
        val id = item.id
        val name = item.name.substringBefore(".catrobat-image")
        val resolution = item.resolution
        val creationDate = item.creationDate
        val lastModifiedDate = item.lastModified
        val format = item.format
        val size = item.size

        val dateTimeFormat = SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault())

        val formattedLastModified = dateTimeFormat.format(Date(lastModifiedDate))
        val formattedCreationDate = dateTimeFormat.format(Date(creationDate))

        val formattedSize = if (size >= MEGABYTE) {
            val formattedValue = String.format("%.2f", size)
            "${formattedValue}MB"
        } else {
            val formattedValue = String.format("%.1f", size * KILOBYTE)
            "${formattedValue}KB"
        }

        val imageFile = getFileFromUri(Uri.parse(item.imagePreviewPath), context)
        val imageUri: Uri? = if (imageFile?.exists() == true) {
            Uri.fromFile(imageFile)
        } else {
            null
        }

        if (imageUri != null) {
            holder.itemImageView.setImageURI(Uri.parse(item.imagePreviewPath))
        } else {
            holder.itemImageView.setImageResource(R.drawable.pocketpaint_logo_small)
        }
        holder.itemNameText.text = name
        holder.itemLastModifiedText.text = formattedLastModified

        val projectDetailsMenu = holder.itemMoreOption
        projectDetailsMenu.setOnClickListener { view ->
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.menu_pocketpaint_project_details)
            popupMenu.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.project_details -> {
                        val projectDetails = ProjectDetailsDialog(name, resolution, formattedLastModified, formattedCreationDate, format, formattedSize)
                        projectDetails.show(supportFragmentManager, PROJECT_DETAILS_DIALOG_FRAGMENT_TAG)
                        true
                    }
                    R.id.project_delete -> {
                        val projectDelete = ProjectDeleteDialog(id, name, position)
                        projectDelete.show(supportFragmentManager, PROJECT_DELETE_DIALOG_FRAGMENT_TAG)
                        true
                    }
                    else -> false
                }
            }
            popupMenu.show()
        }

        holder.itemView.setOnClickListener {
            val clickedItem = projectList[position]
            val projectUri = clickedItem.path
            val projectName = clickedItem.name
            val projectImagePreviewUri = clickedItem.imagePreviewPath
            itemClickListener?.onItemClick(position, projectUri, projectName, projectImagePreviewUri)
        }
    }

    private fun getFileFromUri(uri: Uri, context: Context): File? {
        val filePath: String?
        val file: File?
        if (uri.scheme == "content") {
            val contentResolver = context.contentResolver
            val cursor = contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                if (it.moveToFirst()) {
                    val columnIndex = it.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA)
                    filePath = it.getString(columnIndex)
                    file = File(filePath)
                    return file
                }
            }
        }
        try {
            file = File(uri.path)
            return file
        } catch (e: IOException) {
            Log.e(TAG, "Error occurred while accessing the file: ${e.message}")
        }
        return null
    }

    fun insertProject(project: Project) {
        projectList.add(0, project)
        imagePreview.setImageURI(Uri.parse(project?.imagePreviewPath))
        notifyItemInserted(0)
    }

    fun updateProject(filename: String, imagePreviewPath: String, projectUri: String, lastModified: Long) {
        val index = projectList.indexOfFirst { it.name == filename }
        if (index != -1) {
            val updatedProject = projectList[index].copy(
                path = projectUri,
                imagePreviewPath = imagePreviewPath,
                lastModified = lastModified,
            )
            projectList[index] = updatedProject
            notifyItemChanged(index)
        }
    }

    fun removeProject(projectId: Int, position: Int) {
        if (projectList.isNotEmpty()) {
            projectList.removeAt(position)
        }
        val iterator = projectList.iterator()
        while (iterator.hasNext()) {
            val project = iterator.next()
            if (project.id == projectId) {
                iterator.remove()
                break
            }
        }
        if (projectList.isNotEmpty()) {
            latestProject = projectList[0]
            imagePreview.setImageURI(Uri.parse(latestProject?.imagePreviewPath))
        } else {
            imagePreview.setImageResource(R.drawable.pocketpaint_checkeredbg_repeat)
        }
    }

    override fun getItemCount(): Int = projectList.size

    interface OnItemClickListener {
        fun onItemClick(position: Int, projectUri: String, projectName: String, projectImagePreviewUri: String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        itemClickListener = listener
    }
}

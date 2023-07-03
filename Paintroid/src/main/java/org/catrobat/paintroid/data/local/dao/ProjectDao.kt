package org.catrobat.paintroid.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import org.catrobat.paintroid.model.Project

@Dao
interface ProjectDao {

    @Insert
    fun insertProject(project: Project)

    @Query("UPDATE Project SET path= :projectUri, imagePreviewPath= :imagePreviewPath, lastModified= :lastModified WHERE name= :name")
    fun updateProject(name: String, imagePreviewPath: String, projectUri: String, lastModified: Long)

    @Query("SELECT * FROM Project ORDER BY lastModified DESC")
    fun getProjects(): List<Project>

    @Query("SELECT * FROM Project WHERE name= :name")
    fun getProject(name: String): Project

    @Query("DELETE FROM Project WHERE id= :id")
    fun deleteProject(id: Int)

    @Query("DELETE FROM Project")
    fun deleteAllProjects()
}

package org.catrobat.paintroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    var name: String,
    val path: String,
    var lastModified: String,
    val creationDate: String,
    val resolution: String,
    val format: String,
    val size: Int,
    val imagePreviewPath: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
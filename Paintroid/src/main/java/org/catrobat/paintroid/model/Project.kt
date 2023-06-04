package org.catrobat.paintroid.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Project(
    var name: String,
    val path: String,
    var lastModified: Long,
    val creationDate: Long,
    val resolution: String,
    val format: String,
    val size: Double,
    val imagePreviewPath: String,
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0
)
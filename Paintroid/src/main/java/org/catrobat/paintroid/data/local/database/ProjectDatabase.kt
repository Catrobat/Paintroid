package org.catrobat.paintroid.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import org.catrobat.paintroid.data.local.dao.ProjectDao
import org.catrobat.paintroid.model.Project

@Database(
    entities = [Project::class],
    version = 1
)
abstract class ProjectDatabase: RoomDatabase() {

    abstract val dao: ProjectDao
}
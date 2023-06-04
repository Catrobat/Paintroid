package org.catrobat.paintroid.data.local.database

import android.content.Context
import androidx.room.Room

object ProjectDatabaseProvider {
    var projectDatabase: ProjectDatabase? = null

    fun getDatabase(context: Context): ProjectDatabase{
        return projectDatabase?: synchronized(this){
            projectDatabase ?: buildDatabase(context).also { projectDatabase = it }
        }
    }

    fun buildDatabase(context: Context): ProjectDatabase{
        return Room.databaseBuilder(context, ProjectDatabase::class.java, "projects.db")
            .allowMainThreadQueries()
            .build()
    }
}
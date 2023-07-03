package org.catrobat.paintroid.test.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import junit.framework.Assert.assertFalse
import junit.framework.Assert.assertTrue
import junit.framework.Assert.assertEquals
import org.catrobat.paintroid.data.local.dao.ProjectDao
import org.catrobat.paintroid.data.local.database.ProjectDatabase
import org.catrobat.paintroid.model.Project
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectDaoTest {

    private lateinit var database: ProjectDatabase
    private lateinit var dao: ProjectDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ProjectDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.dao
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertProject() {
        val project = Project(
            "name",
            "catrobat/path",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path",
            1)
        dao.insertProject(project)
        val allProjects = dao.getProjects()
        assertTrue(allProjects.contains(project))
    }

    @Test
    fun getProject() {
        val project = Project(
            "name",
            "catrobat/path",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path",
            1)
        dao.insertProject(project)
        val insertedProject = dao.getProject(project.name)
        assertTrue(insertedProject == project)
    }

    @Test
    fun deleteProject() {
        val project = Project(
            "name",
            "catrobat/path",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path",
            1)
        dao.insertProject(project)
        dao.deleteProject(project.id)
        val allProjects = dao.getProjects()
        assertFalse(allProjects.contains(project))
    }

    @Test
    fun deleteAllProjects() {
        val project1 = Project(
            "name1",
            "catrobat/path1",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path1",
            1)
        val project2 = Project(
            "name2",
            "catrobat/path2",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path2",
            2)
        dao.insertProject(project1)
        dao.insertProject(project2)
        dao.deleteAllProjects()
        val allProjects = dao.getProjects()
        assertTrue(allProjects.isEmpty())
    }

    @Test
    fun updateProjectUri() {
        val project = Project(
            "name",
            "catrobat/path",
            0,
            0,
            "0x0",
            "CATROBAT",
            0.0,
            "paintroid/path",
            1)
        dao.insertProject(project)
        dao.updateProject(
            "name",
            "paintroid/upath",
            "catrobat/upath",
            1)
        val allProjects = dao.getProjects()
        val updatedProject = allProjects.find { it.name == "name" }
        assertEquals("paintroid/upath", updatedProject?.imagePreviewPath)
        assertEquals("catrobat/upath", updatedProject?.path)
        assertEquals(1L, updatedProject?.lastModified)
    }
}

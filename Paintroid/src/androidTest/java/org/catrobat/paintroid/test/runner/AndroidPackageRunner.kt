/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2023 The Catrobat Team
 * (<http://developer.catrobat.org/credits>)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.test.runner

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import dalvik.system.DexFile
import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.InitializationError
import org.junit.runners.model.RunnerBuilder
import java.lang.annotation.Inherited
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.Collections
import kotlin.collections.ArrayList

class AndroidPackageRunner(klass: Class<*>, builder: RunnerBuilder) : ParentRunner<Runner>(klass) {
    @Retention(RetentionPolicy.RUNTIME)
    @Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
    @Inherited
    annotation class PackagePath(val value: String)

    private val runners: List<Runner>

    init {
        val suiteClasses = getAllClassesInAnnotatedPath(klass)
        val runners = builder.runners(klass, suiteClasses)
        this.runners = Collections.unmodifiableList(runners)
    }

    override fun getChildren(): List<Runner> = runners

    override fun describeChild(child: Runner): Description = child.description

    override fun runChild(runner: Runner, notifier: RunNotifier) = runner.run(notifier)

    companion object {
        private val TAG = AndroidPackageRunner::class.java.simpleName
        @Throws(InitializationError::class)
        private fun getAllClassesInAnnotatedPath(klass: Class<*>): Array<Class<*>> {
            val annotation = klass.getAnnotation(PackagePath::class.java)
                    ?: throw InitializationError(String.format("class '%s' must have a PackagePath annotation", klass.name))
            val classes = ArrayList<Class<*>>()
            try {
                val packageCodePath = InstrumentationRegistry.getInstrumentation().context.packageCodePath
                val dexFile = DexFile(packageCodePath)
                val iter = dexFile.entries()
                while (iter.hasMoreElements()) {
                    val className = iter.nextElement()
                    if (className.contains(annotation.value) && className.endsWith("Test")) {
                        classes.add(Class.forName(className))
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                throw InitializationError("Exception during loading Test classes from Dex")
            }
            return classes.toTypedArray()
        }
    }
}

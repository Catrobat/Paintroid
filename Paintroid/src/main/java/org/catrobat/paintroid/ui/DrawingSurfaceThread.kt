/*
 * Paintroid: An image manipulation application for Android.
 * Copyright (C) 2010-2021 The Catrobat Team
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
package org.catrobat.paintroid.ui

import android.util.Log
import kotlin.jvm.Synchronized

internal class DrawingSurfaceThread(
    private val drawingSurface: DrawingSurface,
    private val threadRunnable: Runnable
) {
    private val internalThread: Thread
    private var running = false

    init {
        internalThread = Thread(InternalRunnable(), "DrawingSurfaceThread")
        internalThread.isDaemon = true
    }

    companion object {
        private val TAG = DrawingSurfaceThread::class.java.simpleName
    }

    private fun internalRun() {
        while (running) {
            threadRunnable.run()
        }
    }

    @Synchronized
    fun start() {
        Log.d(TAG, "DrawingSurfaceThread.start")
        if (running || internalThread.state == Thread.State.TERMINATED) {
            Log.d(TAG, "DrawingSurfaceThread.start returning")
            return
        }
        if (!internalThread.isAlive) {
            running = true
            internalThread.start()
        }
        drawingSurface.refreshDrawingSurface()
    }

    @Synchronized
    fun stop() {
        Log.d(TAG, "DrawingSurfaceThread.stop")
        running = false
        Log.d(TAG, "DrawingSurfaceThread.join")
        while (internalThread.isAlive) {
            try {
                internalThread.interrupt()
                internalThread.join()
                Log.d(TAG, "DrawingSurfaceThread.stopped")
            } catch (e: InterruptedException) {
                Log.e(TAG, "Interrupt while joining DrawingSurfaceThread\n", e)
            }
        }
    }

    private inner class InternalRunnable : Runnable {
        override fun run() {
            Thread.yield()
            internalRun()
        }
    }
}

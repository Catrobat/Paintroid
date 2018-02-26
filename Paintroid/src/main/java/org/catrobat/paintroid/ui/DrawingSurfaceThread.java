/**
 *  Paintroid: An image manipulation application for Android.
 *  Copyright (C) 2010-2015 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui;

import android.util.Log;

import org.catrobat.paintroid.PaintroidApplication;

class DrawingSurfaceThread {
	private static final String TAG = DrawingSurfaceThread.class.getSimpleName();
	private Thread internalThread;
	private Runnable threadRunnable;
	private boolean running;

	DrawingSurfaceThread(Runnable runnable) {
		threadRunnable = runnable;
		internalThread = new Thread(new InternalRunnable(),
				"DrawingSurfaceThread");
		internalThread.setDaemon(true);
	}

	private void internalRun() {
		while (running) {
			threadRunnable.run();
		}
	}

	/**
	 * Starts the internal thread only if the thread runnable is not null, the
	 * internal thread has not been terminated and the thread is not already
	 * alive.
	 */
	synchronized void start() {
		Log.d(TAG, "DrawingSurfaceThread.start");
		if (running || threadRunnable == null || internalThread == null
				|| internalThread.getState().equals(Thread.State.TERMINATED)) {
			Log.d(TAG, "DrawingSurfaceThread.start returning");
			return;
		}
		if (!internalThread.isAlive()) {
			running = true;
			internalThread.start();
		}
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
	}

	synchronized void stop() {
		Log.d(TAG, "DrawingSurfaceThread.stop");
		running = false;
		PaintroidApplication.drawingSurface.refreshDrawingSurface();
		if (internalThread != null && internalThread.isAlive()) {
			Log.w(TAG, "DrawingSurfaceThread.join");
			boolean retry = true;
			while (retry) {
				try {
					internalThread.join();
					retry = false;
					Log.d(TAG, "DrawingSurfaceThread.stopped");
				} catch (InterruptedException e) {
					Log.e(TAG, "Interrupt while joining DrawingSurfaceThread\n", e);
				}
			}
		}
	}

	private class InternalRunnable implements Runnable {
		@Override
		public void run() {
			Thread.yield();
			internalRun();
		}
	}
}

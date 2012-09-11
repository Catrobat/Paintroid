/**
 *  Catroid: An on-device graphical programming language for Android devices
 *  Copyright (C) 2010-2011 The Catroid Team
 *  (<http://code.google.com/p/catroid/wiki/Credits>)
 *  
 *  Paintroid: An image manipulation application for Android, part of the
 *  Catroid project and Catroid suite of software.
 *  
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *  
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid_license_additional_term
 *  
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Affero General Public License for more details.
 *   
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package at.tugraz.ist.paintroid.ui.implementation;

import android.util.Log;
import at.tugraz.ist.paintroid.PaintroidApplication;

class DrawingSurfaceThread {
	private Thread internalThread;
	private Runnable threadRunnable;
	private boolean running;

	private class InternalRunnable implements Runnable {
		@Override
		public void run() {
			internalRun();
		}
	}

	DrawingSurfaceThread(Runnable runnable) {
		threadRunnable = runnable;
		internalThread = new Thread(new InternalRunnable());
		internalThread.setDaemon(true);
	}

	private void internalRun() {
		while (running) {
			threadRunnable.run();
		}
	}

	/**
	 * Starts the internal thread only if the thread runnable is not null, the internal thread has not been terminated
	 * and the thread is not already alive.
	 */
	synchronized void start() {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.start");
		if (running || threadRunnable == null || internalThread == null
				|| internalThread.getState().equals(Thread.State.TERMINATED)) {
			Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.start returning");
			return;
		}
		// Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.start up");
		if (!internalThread.isAlive()) {
			running = true;
			internalThread.start();
			// Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.started");
		}
	}

	synchronized void stop() {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.stop");
		running = false;
		if (internalThread != null && internalThread.isAlive()) {
			Log.w(PaintroidApplication.TAG, "DrawingSurfaceThread.join");
			boolean retry = true;
			while (retry) {
				try {
					internalThread.join();
					retry = false;
					Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.stopped");
				} catch (InterruptedException e) {
					Log.e(PaintroidApplication.TAG, "Interrupt while joining DrawingSurfaceThread\n", e);
				}
			}
		}
	}

	synchronized void setRunnable(Runnable runnable) {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.setRunnable");
		threadRunnable = runnable;
	}
}

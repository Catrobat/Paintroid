/**
 *  Catroid: An on-device visual programming system for Android devices
 *  Copyright (C) 2010-2012 The Catrobat Team
 *  (<http://developer.catrobat.org/credits>)
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Affero General Public License as
 *  published by the Free Software Foundation, either version 3 of the
 *  License, or (at your option) any later version.
 *
 *  An additional term exception under section 7 of the GNU Affero
 *  General Public License, version 3, is available at
 *  http://www.catroid.org/catroid/licenseadditionalterm
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  GNU Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.catrobat.paintroid.ui.implementation;

import org.catrobat.paintroid.PaintroidApplication;

import android.util.Log;

class DrawingSurfaceThread {
	private Thread internalThread;
	private Runnable threadRunnable;
	private boolean running;
	boolean mPause = false;
	boolean mWhileLoopIsPaused = false;

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
			((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).mPendingDoDraw++;
			if (mPause == false) {
				mWhileLoopIsPaused = false;
				if (((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).mPendingDoDraw < 2) {
					threadRunnable.run();
				} else {
					((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).mPendingDoDraw--;
				}
			} else {
				mWhileLoopIsPaused = true;
				((DrawingSurfaceImplementation) PaintroidApplication.DRAWING_SURFACE).mPendingDoDraw--;
				try {
					Thread.sleep(10);
				} catch (InterruptedException exception) {
					Log.e(PaintroidApplication.TAG, exception.toString());
				}
			}
		}
	}

	/**
	 * Starts the internal thread only if the thread runnable is not null, the
	 * internal thread has not been terminated and the thread is not already
	 * alive.
	 */
	synchronized void start() {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.start");
		if (running || threadRunnable == null || internalThread == null
				|| internalThread.getState().equals(Thread.State.TERMINATED)) {
			Log.d(PaintroidApplication.TAG,
					"DrawingSurfaceThread.start returning");
			return;
		}
		if (!internalThread.isAlive()) {
			running = true;
			mPause = false;
			internalThread.start();
		}
	}

	synchronized void stop() {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.stop");
		mPause = true;
		running = false;
		if (internalThread != null && internalThread.isAlive()) {
			Log.w(PaintroidApplication.TAG, "DrawingSurfaceThread.join");
			boolean retry = true;
			while (retry) {
				try {
					internalThread.join();
					retry = false;
					Log.d(PaintroidApplication.TAG,
							"DrawingSurfaceThread.stopped");
				} catch (InterruptedException e) {
					Log.e(PaintroidApplication.TAG,
							"Interrupt while joining DrawingSurfaceThread\n", e);
				}
			}
		}
	}

	synchronized void setRunnable(Runnable runnable) {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.setRunnable");
		threadRunnable = runnable;
	}
}

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
	 * Starts the internal thread only if the thread runnable is not null, the internal thread has
	 * not been terminated and the thread is not already alive. If the internal thread is paused, it
	 * is unpaused by calling setPaused(false).
	 */
	synchronized void start() {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.start");
		if (threadRunnable == null || internalThread.getState().equals(Thread.State.TERMINATED)) {
			return;
		}
		running = true;
		if (!internalThread.isAlive()) {
			internalThread.start();
		}
	}

	synchronized void stop() {
		Log.d(PaintroidApplication.TAG, "DrawingSurfaceThread.stop");
		running = false;
		if (internalThread.isAlive()) {
			Log.w(PaintroidApplication.TAG, "DrawingSurfaceThread.join");
			boolean retry = true;
			while (retry) {
				try {
					internalThread.join();
					retry = false;
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

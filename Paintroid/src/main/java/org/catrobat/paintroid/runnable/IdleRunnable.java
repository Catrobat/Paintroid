package org.catrobat.paintroid.runnable;

public class IdleRunnable implements Runnable {
	private boolean stop = false;

	public synchronized void stopThread() {
		this.stop = true;
	}

	protected synchronized boolean isRunning() {
		return !this.stop;
	}

	@Override
	public void run() {
	}
}

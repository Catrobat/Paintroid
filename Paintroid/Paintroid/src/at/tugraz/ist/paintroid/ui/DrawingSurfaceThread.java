package at.tugraz.ist.paintroid.ui;

class DrawingSurfaceThread {
	private Thread internalThread;
	private Runnable threadRunnable;
	private boolean running;
	private boolean paused;

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
			synchronized (internalThread) {
				if (paused) {
					try {
						internalThread.wait();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} else {
					threadRunnable.run();
				}
			}
		}
	}

	synchronized void start() {
		if (threadRunnable == null || internalThread.getState().equals(Thread.State.TERMINATED)) {
			return;
		}
		running = true;
		if (paused) {
			setPaused(false);
		} else if (!internalThread.isAlive()) {
			internalThread.start();
		}
	}

	synchronized void stop() {
		running = false;
		setPaused(false);
		if (internalThread.isAlive()) {
			boolean retry = true;
			while (retry) {
				try {
					internalThread.join();
					retry = false;
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	synchronized void setPaused(boolean pause) {
		synchronized (internalThread) {
			if (!pause && paused) {
				internalThread.notify();
			}
			paused = pause;
		}
	}

	synchronized void setRunnable(Runnable runnable) {
		threadRunnable = runnable;
	}
}

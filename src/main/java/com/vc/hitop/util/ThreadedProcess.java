package com.vc.hitop.util;

import com.vc.hitop.log.Log;

public abstract class ThreadedProcess {
	private final Thread thread;

	protected ThreadedProcess() {
		thread = new Thread(this::run);
	}

	protected ThreadedProcess(String name) {
		thread = new Thread(this::run, name);
	}

	public void start() {
		thread.start();
	}

	protected abstract void run();

	public void stop() throws Exception {
		if (!thread.isAlive()) {
			Log.info("%s process (%s) already stopped.", getClass().getSimpleName(), thread.getName());
			return;
		}

		try {
			thread.interrupt();
			thread.join();
			Log.info("%s process (%s) stopped.", getClass().getSimpleName(), thread.getName());
		} catch (InterruptedException e) {
			Log.info("%s process (%s) interrupted while stopping.", getClass().getSimpleName(), thread.getName());
		}
	}

	public boolean isAlive() {
		return thread.isAlive();
	}
}

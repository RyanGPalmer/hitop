package com.vc.hitop.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;

import com.vc.hitop.log.Log;

public class Server {
	private final Thread thread = new Thread(this::receive, "server");
	private final String host;
	private final int port;
	private final int timeout;
	private final Dispatcher dispatcher;

	private boolean running = false;

	private Server(String host, int port, int timeout, int threads) {
		this.host = host;
		this.port = port;
		this.timeout = timeout;
		this.dispatcher = new Dispatcher(threads);
	}

	public void start() {
		Log.info("Starting server...");
		running = true;
		thread.start();

		while (!thread.isAlive()) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException ignore) {
			}
		}

		Log.info("Server startup complete");
	}

	public void stop() {
		Log.info("Stopping server...");
		running = false;
		thread.interrupt();

		try {
			thread.join();
		} catch (InterruptedException ignore) {
		}

		dispatcher.stop();
		Log.info("Server shutdown complete");
	}

	private void receive() {
		try (ServerSocket server = new ServerSocket()) {
			server.bind(new InetSocketAddress(host, port));
			server.setSoTimeout(timeout);
			Log.info("Server started at %s (Port: %d)", server.getInetAddress().toString(), server.getLocalPort());
			while (running) {
				try {
					dispatcher.process(server.accept());
				} catch (SocketTimeoutException ignore) {
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static class Builder {
		private String host = "localhost";
		private int port = 8888;
		private int timeout = 5000;
		private int threads = 32;

		public Builder withHost(String host) {
			this.host = host;
			return this;
		}

		public Builder withPort(int port) {
			this.port = port;
			return this;
		}

		public Builder withTimeout(int timeout) {
			this.timeout = timeout;
			return this;
		}

		public Builder withThreads(int threads) {
			this.threads = threads;
			return this;
		}

		public Server build() {
			return new Server(host, port, timeout, threads);
		}
	}
}

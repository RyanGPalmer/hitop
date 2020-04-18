package com.vc.hitop.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.SocketException;

import com.vc.hitop.log.Log;
import com.vc.hitop.util.ThreadedProcess;

public final class Server extends ThreadedProcess implements Runnable {
	private final ConnectionManager connectionManager = new ConnectionManager();
	private final ServerSocket server = new ServerSocket();
	private final String host;
	private final int port;
	private final int threads;

	private Server(String host, int port, int threads) throws IOException {
		super("server");
		this.host = host;
		this.port = port;
		this.threads = threads;
	}

	@Override
	public void start() {
		super.start();
		connectionManager.start();
		Log.info("Server started.");
	}

	@Override
	public final void stop() {
		try {
			server.close();
			super.stop();
			connectionManager.stop();
		} catch (Exception e) {
			Log.info("ERROR: Something went wrong while stopping server: %s", e.toString());
		}
	}

	@Override
	public final void run() {
		try {
			server.bind(new InetSocketAddress(host, port));
			while (!server.isClosed()) {
				try {
					connectionManager.add(server.accept());
				} catch (SocketException e) {
					Log.info(e.getMessage());
				}
			}
		} catch (IOException e) {
			Log.info("An error occurred during server execution: %s", e.toString());
		}
	}

	public static class Builder {
		private String host = "localhost";
		private int port = 8888;
		private int threads = 32;

		public Builder withHost(String host) {
			this.host = host;
			return this;
		}

		public Builder withPort(int port) {
			this.port = port;
			return this;
		}

		public Builder withThreads(int threads) {
			this.threads = threads;
			return this;
		}

		public Server build() throws IOException {
			return new Server(host, port, threads);
		}
	}
}

package com.vc.hitop.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

import com.vc.hitop.log.Log;
import com.vc.hitop.util.ThreadedProcess;

public class Connection extends ThreadedProcess implements Runnable {
	private final Socket socket;

	public Connection(Socket socket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		try (HttpRequestReader input = new HttpRequestReader(socket.getInputStream())) {
			while (socket.isConnected() && !socket.isClosed())
				handleRequest(input.read());
		} catch (IOException e) {
			Log.info("Connection issue: %s", e.toString());
		}
	}

	private void handleRequest(HttpRequest input) throws IOException {
		try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {
			bw.write(String.format("HTTP/1.1 200 OK\n\n%s", input));
			bw.flush();
		}
	}

	@Override
	public void stop() throws Exception {
		socket.close();
		super.stop();
	}

	@Override
	public boolean isAlive() {
		return super.isAlive() || !socket.isClosed();
	}
}

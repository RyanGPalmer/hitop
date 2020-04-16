package com.vc.hitop.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class InputReader implements AutoCloseable {
	private final BufferedReader reader;

	public InputReader(Socket connection) throws IOException {
		reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	}

	public boolean ready() throws IOException {
		return reader.ready();
	}

	public String readLine() throws IOException {
		return reader.readLine();
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}

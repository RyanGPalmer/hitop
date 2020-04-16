package com.vc.hitop.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;

public class OutputWriter implements AutoCloseable {
	private final BufferedWriter writer;

	public OutputWriter(Socket connection) throws IOException {
		writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
	}

	public void write(String line) throws IOException {
		writer.write(line);
	}

	@Override
	public void close() throws IOException {
		writer.flush();
		writer.close();
	}
}
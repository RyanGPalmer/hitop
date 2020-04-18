package com.vc.hitop.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.vc.hitop.log.Log;

public class HttpRequestReader implements AutoCloseable {
	private static final String HEADER_REGEX = "^.*:.*$";

	private final BufferedReader input;

	public HttpRequestReader(InputStream input) {
		this.input = new BufferedReader(new InputStreamReader(input));
	}

	public HttpRequest read() throws IOException {
		HttpRequest.StartLine startLine = readStartLine();
		Map<String, String> headers = readHeaders();

		HttpRequest request = new HttpRequest(startLine, headers);
		Log.info("New request: %s", request);

		return request;
	}

	private HttpRequest.StartLine readStartLine() throws IOException {
		String[] line = input.readLine().split(" ");
		return new HttpRequest.StartLine(line[0], line[1], line[2]);
	}

	private Map<String, String> readHeaders() throws IOException {
		Map<String, String> headers = new HashMap<>();

		while (input.ready()) {
			String line = input.readLine();
			if (!line.matches(HEADER_REGEX))
				break;

			int delim = line.indexOf(':');
			String header = line.substring(0, delim);
			String value = line.substring(delim + 1);
			headers.put(header, value);
		}

		return headers;
	}

	@Override
	public void close() throws IOException {
		input.close();
	}
}

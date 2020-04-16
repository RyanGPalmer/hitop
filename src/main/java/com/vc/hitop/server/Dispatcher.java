package com.vc.hitop.server;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.vc.hitop.log.Log;

public class Dispatcher {
	private final ExecutorService executor;

	Dispatcher(int threads) {
		this.executor = Executors.newFixedThreadPool(threads);
		Log.info("Dispatcher initialized with %s threads", threads);
	}

	void stop() {
		executor.shutdown();

		try {
			Log.info("Waiting for dispatcher tasks to complete");
			if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
				Log.info("Dispatcher thread pool failed to finish after 30 seconds. Forcing termination...");
				executor.shutdownNow();
				if (!executor.awaitTermination(30, TimeUnit.SECONDS))
					Log.info("Forced termination of dispatcher thread pool failed!");
			}
		} catch (InterruptedException ignore) {
		}

		Log.info("Dispatcher shutdown complete");
	}

	void process(Socket connection) {
		executor.execute(() -> {
			try {
				process0(connection);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});
	}

	private void process0(Socket connection) throws IOException {
		try (InputReader reader = new InputReader(connection); OutputWriter writer = new OutputWriter(connection)) {
			String input = getInput(reader).trim();
			if (input.isEmpty()) {
				Log.info("Empty request");
				return;
			}

			Log.info("Input received (Type: %s) (Path: %s)", input.split("\\s")[0], input.split("\\s")[1]);

			sendOutput(input, writer);
		}
	}

	private String getInput(InputReader reader) throws IOException {
		StringBuilder input = new StringBuilder();

		while (reader.ready())
			input.append(reader.readLine()).append('\n');

		return input.toString();
	}

	private void sendOutput(String input, OutputWriter writer) throws IOException {
		writer.write(String.format("HTTP/1.1 200 OK\n\nReceived input:\n%s", input));
	}
}

package com.vc.server;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.vc.hitop.server.Server;

public class ServerStressTest {
	private static final int THREAD_COUNT = 10;
	private static final ExecutorService EXEC = Executors.newFixedThreadPool(THREAD_COUNT);
	private static Server SERVER;

	@BeforeClass
	public static void startServer() throws IOException {
		SERVER = new Server.Builder().build();
		SERVER.start();
	}

	@AfterClass
	public static void stopServer() {
		SERVER.stop();
	}

	@Test
	public void testManyConnections() throws Exception {
		List<Future<Boolean>> futures = new ArrayList<>();
		for (int i = 0; i < THREAD_COUNT; i++)
			futures.add(EXEC.submit(this::testConnection));

		EXEC.shutdown();
		assertTrue(EXEC.awaitTermination(30, TimeUnit.SECONDS));

		for (Future<Boolean> future : futures)
			assertTrue(future.get());
	}

	private boolean testConnection() {
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8888").openConnection();
			connection.setRequestMethod("GET");
			return connection.getResponseCode() == 200;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
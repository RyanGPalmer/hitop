package com.vc.hitop;

import com.vc.hitop.server.Server;

public class Main {
	private static final Server SERVER = new Server.Builder().build();

	public static void main(String[] args) {
		Runtime.getRuntime().addShutdownHook(new Thread(Main::onShutdown, "shutdown"));
		SERVER.start();
	}

	private static void onShutdown() {
		SERVER.stop();
	}
}

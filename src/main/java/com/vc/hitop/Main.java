package com.vc.hitop;

import java.io.IOException;

import com.vc.hitop.server.Server;

public class Main {
	public static void main(String[] args) throws IOException {
		Server server = new Server.Builder().build();
		Runtime.getRuntime().addShutdownHook(new Thread(server::stop, "shutdown"));
		server.start();
	}
}

package com.vc.hitop.log;

public class Log {
	public static void info(String message, Object... args) {
		log(String.format(message, args));
	}

	private static void log(String message) {
		System.out.printf("%s %s\n", Thread.currentThread().getName(), message);
	}
}

package com.vc.hitop.server;

import java.util.Collections;
import java.util.Map;

public class HttpRequest {
	public enum Type {
		GET,
		POST,
		INVALID;

		public static Type of(String s) {
			if (s == null)
				return INVALID;

			switch (s.toUpperCase().trim()) {
				case "GET":
					return GET;
				case "POST":
					return POST;
				default:
					return INVALID;
			}
		}
	}

	public static class StartLine {
		private final Type type;
		private final String path;
		private final String protocol;

		public StartLine(String type, String path, String protocol) {
			this.type = Type.of(type);
			this.path = path;
			this.protocol = protocol;
		}

		@Override
		public String toString() {
			return String.format("[Type: %s] [Protocol: %s] [Path: %s]", type, protocol, path);
		}
	}

	private final StartLine startLine;
	private final Map<String, String> headers;

	public HttpRequest(StartLine startLine, Map<String, String> headers) {
		this.startLine = startLine;
		this.headers = Collections.unmodifiableMap(headers);
	}

	@Override
	public String toString() {
		return String.format("%s", startLine);
	}
}

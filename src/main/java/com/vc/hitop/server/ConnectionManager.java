package com.vc.hitop.server;

import java.net.Socket;

import com.vc.hitop.log.Log;
import com.vc.hitop.util.ThreadedProcess;

public class ConnectionManager extends ThreadedProcess {
	private final Node head = new Node(null);
	private Node tail = head;

	public synchronized void add(Socket socket) {
		Connection connection = new Connection(socket);

		Node node = new Node(connection);
		tail.setNext(node);
		tail = node;

		connection.start();
	}

	@Override
	public void start() {
		super.start();
		Log.info("Connection manager started.");
	}

	@Override
	protected void run() {
		while (true) {
			try {
				Thread.sleep(5000);
				sweepConnections();
			} catch (InterruptedException ignore) {
				break;
			}
		}
	}

	@Override
	public void stop() throws Exception {
		super.stop();
		sweepConnections();
		stopConnections();
	}

	private synchronized void sweepConnections() {
		int count = 0;
		Node last;
		for (last = head; last.getNext() != null; ) {
			Node node = last.getNext();
			if (!node.getValue().isAlive()) {
				last.setNext(node.getNext());
				count++;
			} else last = last.getNext();
		}

		tail = last;
		if (count > 0)
			Log.info("Connection sweeper cleaned %d connections.", count);
	}

	private synchronized void stopConnections() throws Exception {
		for (Node n = head.getNext(); n != null; n = n.getNext())
			n.getValue().stop();
	}

	private static class Node {
		private final Connection value;
		private Node next;

		public Node(Connection value) {
			this.value = value;
		}

		public void setNext(Node next) {
			this.next = next;
		}

		public Node getNext() {
			return next;
		}

		public Connection getValue() {
			return value;
		}
	}
}


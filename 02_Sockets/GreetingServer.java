package edu.thi.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;

/*
 * Quelle: https://www.tutorialspoint.com/java/java_networking.htm
 */
public class GreetingServer implements Runnable {
	private ServerSocket serverSocket;

	public GreetingServer(int port) throws IOException {
		serverSocket = new ServerSocket(port);
		serverSocket.setSoTimeout(100000000); // Timeout: 10 secs
	}

	public void run() {
		// Typisch für einen Server: Warten in Endlosschleife
		while (true) {
			try {
				System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
				Socket server = serverSocket.accept();

				System.out.println("Just connected to " + server.getRemoteSocketAddress());
				DataInputStream in = new DataInputStream(server.getInputStream());


				DataOutputStream out = new DataOutputStream(server.getOutputStream());
				out.writeUTF(
						"Server replies: Thank you for connecting to " + server.getLocalSocketAddress() + "\nGoodbye!");
				server.close();

			} catch (SocketTimeoutException s) {
				System.out.println("Socket timed out after 10 secs!");
				break;
			} catch (IOException e) {
				e.printStackTrace();
				break;
			}
		}
	}

	public static void main(String[] args) {
		int port = 3141;
		try {
			Thread t = new Thread(new GreetingServer(port));
			t.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

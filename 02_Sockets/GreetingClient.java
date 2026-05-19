package edu.thi.socket;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/*
 * Quelle: https://www.tutorialspoint.com/java/java_networking.htm
 */
public class GreetingClient {
	public static void main(String[] args) throws InterruptedException {
		String serverName = "localhost";
		int port = 3141;
		for (int versuch = 1; versuch <= 5; versuch++) {
			try {
				System.out.println("Connecting to " + serverName + " on port " + port);
				Socket clientSocket = new Socket();
				clientSocket.connect(new InetSocketAddress(serverName, port), 5000);  // Timer

				System.out.println("Just connected to " + clientSocket.getRemoteSocketAddress());
				OutputStream outToServer = clientSocket.getOutputStream();
				DataOutputStream out = new DataOutputStream(outToServer);

				out.writeUTF("Client says: Hello from " + clientSocket.getLocalSocketAddress());
				InputStream inFromServer = clientSocket.getInputStream();  // Antwort vom Server
				DataInputStream in = new DataInputStream(inFromServer);

				System.out.println(in.readUTF()); // eingehende Nachricht vom Server
				clientSocket.close();
				break;
			} catch (IOException e) {
				System.out.println("server nicht erreichtbar. " + versuch + ". versuch (timeout)");
			}
		}
	}
}

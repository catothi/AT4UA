package edu.thi.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/*
 * Basis: http://www.ejbtutorial.com/java-rmi/new-easy-tutorial-for-java-rmi-using-eclipse
 * https://www.tutorials.de/threads/java-1-5-0-rmi-ohne-stubs-und-skeletons-dank-dynamic-proxies.179039/
 * Man beachte: die typische Server-Endlosschleife fehlt in der Server-Implementierung
 */
public class AdditionServer implements AdditionInterface {
	public final static int STD_RMI_PORT = 1099;

	public AdditionServer() {
	}

	private void start() {
		try {
			AdditionInterface stub = (AdditionInterface) UnicastRemoteObject.exportObject(this, 0);
			Registry registry = LocateRegistry.createRegistry(STD_RMI_PORT);
			System.out.println("Registry: " + registry);
			registry.bind("ADDITIONSERVICE", stub);
			System.out.println("Addition Server is ready.");
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new AdditionServer().start();
	}

	public int add(int a, int b) throws RemoteException {
		System.out.println("Remote call received with values " + a + " and " + b);
		int result = a + b;
		return result;
	}

}

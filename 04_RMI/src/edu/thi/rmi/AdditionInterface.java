package edu.thi.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
 * Basis: http://www.ejbtutorial.com/java-rmi/new-easy-tutorial-for-java-rmi-using-eclipse
 * https://www.tutorials.de/threads/java-1-5-0-rmi-ohne-stubs-und-skeletons-dank-dynamic-proxies.179039/
 */
public interface AdditionInterface extends Remote {
	public int add(int a, int b) throws RemoteException;
}

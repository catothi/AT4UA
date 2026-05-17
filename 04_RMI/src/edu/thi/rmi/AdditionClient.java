package edu.thi.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/*
 * Basis: http://www.ejbtutorial.com/java-rmi/new-easy-tutorial-for-java-rmi-using-eclipse
 * https://www.tutorials.de/threads/java-1-5-0-rmi-ohne-stubs-und-skeletons-dank-dynamic-proxies.179039/
 */
public class AdditionClient {

    public final static int STD_RMI_PORT = 1099;
    
    public static void main(String[] args) {
        new AdditionClient().start();
    }
 
    private void start() {
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1",STD_RMI_PORT);
            AdditionInterface additionService = (AdditionInterface) registry.lookup("ADDITIONSERVICE");
            System.out.println("Addition von 7 und -9 ergibt: " + additionService.add(7,-9));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }
}

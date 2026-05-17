package edu.thi.rmi;

import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class SearchCustomerServer implements SearchCustomerInterface {
	public final static int STD_RMI_PORT = 1099;

	public SearchCustomerServer() {
	}

	private void start() {
		try {
			SearchCustomerInterface stub = (SearchCustomerInterface) UnicastRemoteObject.exportObject(this, 0);

			Registry registry = LocateRegistry.createRegistry(STD_RMI_PORT);

			System.out.println("Registry: " + registry);

			registry.bind("SEARCH_CUSTOMER_SERVICE", stub);
			
			System.out.println("Search Customer Server is ready.");

		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (AlreadyBoundException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		new SearchCustomerServer().start();
	}

	@Override
	public List<Customer> searchCustomers(String email) throws RemoteException {
		System.out.println("Searching for " + email);
		List<Customer> customerList = new ArrayList<>();
		Customer customer = new Customer();
		customer.setId(4711L);
		customer.setPassword("secret1");
		customer.setEmail("donald.duck@demo.org");
		customerList.add(customer);
		customer = new Customer();
		customer.setId(4712L);
		customer.setPassword("secret2");
		customer.setEmail("susan.summer@demo.org");
		customerList.add(customer);
		return customerList;
	}


}

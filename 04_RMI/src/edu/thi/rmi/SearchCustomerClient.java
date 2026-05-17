package edu.thi.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class SearchCustomerClient {

	public final static int STD_RMI_PORT = 1099;

	public static void main(String[] args) {
		new SearchCustomerClient().start();
	}

	private void start() {
		try {
			Registry registry = LocateRegistry.getRegistry("127.0.0.1", STD_RMI_PORT);
			SearchCustomerInterface searchCustomerService = (SearchCustomerInterface) registry
					.lookup("SEARCH_CUSTOMER_SERVICE");
			List<Customer> custList = searchCustomerService.searchCustomers("demo.org");
			for (Customer customer : custList) {
				System.out.println(customer.getId());
				System.out.println(customer.getEmail());
				System.out.println(customer.getPassword());
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (NotBoundException e) {
			e.printStackTrace();
		}
	}
}

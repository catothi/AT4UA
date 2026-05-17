package edu.thi.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface SearchCustomerInterface extends Remote {
	public List<Customer> searchCustomers(String email) throws RemoteException;
}

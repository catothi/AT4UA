package edu.thi.at4ua.ejb;

import java.util.List;
import jakarta.ejb.Remote;
import edu.thi.at4ua.bean.Customer;


@Remote
public interface SearchCustomerBeanRemote {
	public abstract List<Customer> searchCustomers(String email);
}

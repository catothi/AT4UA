import java.util.List;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import edu.thi.at4ua.bean.Customer;
import edu.thi.at4ua.ejb.DiscountBeanRemote;
import edu.thi.at4ua.ejb.SearchCustomerBeanRemote;

public class Main {
	public static void main(String[] args) throws NamingException {
		Properties props = new Properties();
		props.setProperty(Context.INITIAL_CONTEXT_FACTORY,
				"org.wildfly.naming.client.WildFlyInitialContextFactory");
		props.setProperty(Context.PROVIDER_URL,
				"remote+http://localhost:8080");
		props.setProperty(Context.SECURITY_PRINCIPAL, "demo");
		props.setProperty(Context.SECURITY_CREDENTIALS, "demo123!");

		System.out.print("Initializing Context...");
		Context context = new InitialContext(props);
		System.out.println("done!");

		String modul = "ejbdemo";

		System.out.print("Finding discountBean...");
		DiscountBeanRemote discountBean = (DiscountBeanRemote)context.lookup(
											modul
											+ "/"
											+ "DiscountBean"
											+ "!"
											+ "edu.thi.at4ua.ejb.DiscountBeanRemote");
		System.out.println("done!");
		System.out.println("Discount für 101.000€: " + discountBean.calculateDiscount("4711", new Float(101000)));

		System.out.print("Finding searchCustomerBean...");
		SearchCustomerBeanRemote searchCustomerBean = (SearchCustomerBeanRemote)context.lookup(
											modul
											+ "/"
											+ "SearchCustomerBean"
											+ "!"
											+ "edu.thi.at4ua.ejb.SearchCustomerBeanRemote");
		System.out.println("done!");
		List<Customer> custList = searchCustomerBean.searchCustomers("demo.org");
		for (Customer customer : custList) {
			System.out.println(customer.getId());
			System.out.println(customer.getEmail());
			System.out.println(customer.getPassword());
		}
	}

	public Main() {
		super();
	}
}

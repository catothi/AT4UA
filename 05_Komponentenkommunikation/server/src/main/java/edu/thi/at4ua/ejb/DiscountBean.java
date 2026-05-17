package edu.thi.at4ua.ejb;

import jakarta.ejb.Stateless;

/**
 * Session Bean implementation class DiscountBean
 */
@Stateless
public class DiscountBean implements DiscountBeanRemote {

    /**
     * Default constructor. 
     */
    public DiscountBean() {
        // TODO Auto-generated constructor stub
    }

	@Override
	public Float calculateDiscount(String customerId, Float totalAmount) {
		if (totalAmount > 100000)
			return new Float(10.5);
		else
			return new Float(4.3);
	}

}

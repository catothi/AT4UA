package edu.thi.rmi;

import java.io.Serializable;

public class Customer implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String email;
	private String password;
	
	public Customer() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return "Id: " + id + "; Email: " + email + "; Password: " + password;
	}
}

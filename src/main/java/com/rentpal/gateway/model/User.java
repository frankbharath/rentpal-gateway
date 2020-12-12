package com.rentpal.gateway.model;

/**
 * Domain model for user.
 * @author bharath
 * @version 1.0
 * Creation time: Jul 9, 2020 9:58:03 PM
 */

public class User {

	private Long id;

	private String email;

	private String password;

	private Long creationTime;

	private boolean verified;

	public Long getId() { return id; }

	public void setId(Long id) { this.id = id; }

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

	public Long getCreationTime() { return creationTime; }

	public void setCreationTime(Long creationTime) { this.creationTime = creationTime; }

	public boolean isVerified() {
		return verified;
	}
	
	public void setVerified(boolean verified) {
		this.verified = verified;
	}

}

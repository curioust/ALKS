package com.coxautoinc.cia.alks.model.rest;


public class RequestDetails extends RestModel{

	private String password;
	private String adGroup;
	
	public String getAdGroup() {
		return adGroup;
	}
	public void setAdGroup(String adGroup) {
		this.adGroup = adGroup;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	
}

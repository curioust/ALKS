package com.coxautoinc.cia.alks.model.rest;

public class RestModel {
	
	private String userid;
	private String role;
	private Integer sessionTime;
	private String account;
	
	public Integer getSessionTime() {
		return sessionTime;
	}
	public void setSessionTime(Integer sessionTime) {
		this.sessionTime = sessionTime;
	}
	public String getAccount() {
		return account;
	}
	public void setAccount(String account) {
		this.account = account;
	}
	public String getRole() {
		return role;
	}
	public void setRole(String role) {
		this.role = role;
	}

	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}

	
}

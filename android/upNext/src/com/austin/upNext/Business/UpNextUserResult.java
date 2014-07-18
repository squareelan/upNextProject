package com.austin.upNext.Business;

public class UpNextUserResult {
	
	private String Message, Status;
	private User User;
	
	public String getMessage() {
		return Message;
	}
	
	public void setMessage(String message) {
		Message = message;
	}
	
	public String getStatus() {
		return Status;
	}
	
	public void setStatus(String status) {
		Status = status;
	}
	
	public User getUser() {
		return User;
	}
	
	public void setUser(User user) {
		User = user;
	}

}

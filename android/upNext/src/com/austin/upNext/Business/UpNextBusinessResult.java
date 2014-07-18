package com.austin.upNext.Business;

import java.util.List;

public class UpNextBusinessResult {
	
	private String Status, Message;
	private List<Business> Businesses;
	private List<Double> Distances;
	private int size;

	public String getStatus() {
		return Status;
	}

	public void setStatus(String status) {
		Status = status;
	}

	public String getMessage() {
		return Message;
	}

	public void setMessage(String message) {
		Message = message;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<Business> getBusinesses() {
		return Businesses;
	}

	public void setBusinesses(List<Business> businesses) {
		Businesses = businesses;
	}

	public List<Double> getDistances() {
		return Distances;
	}

	public void setDistances(List<Double> distances) {
		Distances = distances;
	}
	
}

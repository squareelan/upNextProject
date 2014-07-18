package com.austin.upNext.Business;

public class User {
	
	private String first, last, _id;
	private int point;
	private String[] history;
	
	public String getFirst() {
		return first;
	}
	
	public void setFirst(String first) {
		this.first = first;
	}
	
	public String getLast() {
		return last;
	}
	
	public void setLast(String last) {
		this.last = last;
	}
	
	public String get_id() {
		return _id;
	}
	
	public void set_id(String _id) {
		this._id = _id;
	}
	
	public int getPoint() {
		return point;
	}
	
	public void setPoint(int point) {
		this.point = point;
	}
	
	public String[] getHistory() {
		return history;
	}
	
	public void setHistory(String[] history) {
		this.history = history;
	}

}

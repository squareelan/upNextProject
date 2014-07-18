package com.austin.upNext.Business;

import java.util.List;

public class Business {

	private String _id, name, category, location, lastReported, shortAddress, reportedBy, busyness, wait, crowd;
	private int hearts;
	private List<String> geocode;
	
	public String get_id() {
		return _id;
	}
	
	public void set_id(String _id) {
		this._id = _id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCategory() {
		return category;
	}
	
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
	}

	public String getLastReported() {
		return lastReported;
	}

	public void setLastReported(String lastReported) {
		this.lastReported = lastReported;
	}

	public List<String> getGeocode() {
		return geocode;
	}

	public void setGeocode(List<String> geocode) {
		this.geocode = geocode;
	}

	public String getShortAddress() {
		return shortAddress;
	}

	public void setShortAddress(String shortAddress) {
		this.shortAddress = shortAddress;
	}

	public String getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(String reportedBy) {
		this.reportedBy = reportedBy;
	}

	public String getBusyness() {
		return busyness;
	}

	public void setBusyness(String busyness) {
		this.busyness = busyness;
	}

	public String getWait() {
		return wait;
	}

	public void setWait(String wait) {
		this.wait = wait;
	}

	public int getHearts() {
		return hearts;
	}

	public void setHearts(int hearts) {
		this.hearts = hearts;
	}

	public String getCrowd() {
		return crowd;
	}

	public void setCrowd(String crowd) {
		this.crowd = crowd;
	}
		
}

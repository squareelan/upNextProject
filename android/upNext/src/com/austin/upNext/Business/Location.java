package com.austin.upNext.Business;

import java.util.List;

public class Location {
	private Coordinate coordinate;
	private List<String> address;
	private List<String> display_address;
	private String city;
	private String stateCode;
	private String postal_code;
	private String countryCode;
	private String crossStreets;
	private List<String> neighborhoods;
	private int geoAccuracy;

	public Coordinate getCoordinate() {
		return coordinate;
	}

	public void setCoordinate(Coordinate coordinate) {
		this.coordinate = coordinate;
	}

	public List<String> getAddress() {
		return address;
	}

	public void setAddress(List<String> address) {
		this.address = address;
	}

	public List<String> getDisplayAddress() {
		return display_address;
	}

	public void setDisplayAddress(List<String> displayAddress) {
		this.display_address = displayAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getStateCode() {
		return stateCode;
	}

	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	public String getPostalCode() {
		return postal_code;
	}

	public void setPostalCode(String postalCode) {
		this.postal_code = postalCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public String getCrossStreets() {
		return crossStreets;
	}

	public void setCrossStreets(String crossStreets) {
		this.crossStreets = crossStreets;
	}

	public List<String> getNeighborhoods() {
		return neighborhoods;
	}

	public void setNeighborhoods(List<String> neighborhoods) {
		this.neighborhoods = neighborhoods;
	}

	public int getGeoAccuracy() {
		return geoAccuracy;
	}

	public void setGeoAccuracy(int geoAccuracy) {
		this.geoAccuracy = geoAccuracy;
	}
}


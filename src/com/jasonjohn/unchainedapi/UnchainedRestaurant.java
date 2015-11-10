package com.jasonjohn.unchainedapi;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * UnchainedRestaurant, the heart of this API
 * @author Jason John
 *
 */
public class UnchainedRestaurant {

	/** Various venue information */
	private String name, address, website, telephone;
	private Double rating;
	private ArrayList<String> picUrls;
	private double[] latlng;
	private int pricePoint;

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public double[] getLatlng() {
		return latlng;
	}

	public void setLatlng(double[] latlng) {
		this.latlng = latlng;
	}

	public int getPricePoint() {
		return pricePoint;
	}

	public void setPricePoint(int pricePoint) {
		this.pricePoint = pricePoint;
	}

	/**
	 * Constructor that fills out all venue data
	 * @param name Name of Venue
	 * @param address Geographic address of venue
	 * @param website Venue website (on review site, not actual company website)
	 * @param rating Rating out of 5 stars
	 */
	public UnchainedRestaurant(String name, String address, String website, Double rating, ArrayList<String> pics, 
			String telephone, double[] ll, int pricePoint) {
		setName(name);
		setAddress(address);
		setWebsite(website);
		setRating(rating);
		setPicUrls(pics);
		setTelephone(telephone);
		setLatlng(ll);
		setPricePoint(pricePoint);
	}

	@Override
	public String toString() {
		String s = "Restaurant Info for: " + getName() + "\n";

		s += (getAddress() == null) ? "No address data for this venue\n" : getAddress() + "\n";
		s += getWebsite() + "\n";

		if(getRating() == -1) 
			s+= "No rating data for this venue";
		else
			s += getRating() + "/5\n";

		if(getPicUrls() != null && !getPicUrls().isEmpty())
			s += "Pic: " + getPicUrls().get(0) + "\n";
		return s;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		if(website != null) 
			this.website = website;
		else
			if(this.address != null) {
				this.website = String.format("http://www.google.com/search?q=%s+%s",
						getName().replace(' ', '+'), getAddress().replace(' ', '+'));
			} else {
				this.website = String.format("http://www.google.com/search?q=%s",
						getName().replace(' ', '+'));
			}
	}


	@Override
	public int hashCode() {
		return Util.normalizeVenueName(this.getName()).hashCode();
	}

	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}

	@Override
	public boolean equals(Object o) {
		UnchainedRestaurant r = (UnchainedRestaurant) o;
		String name1 = this.getName();
		String name2 = r.getName();

		name1 = Util.normalizeVenueName(name1);
		name2 = Util.normalizeVenueName(name2);

		if(name1.contains(name2) || name2.contains(name1)) {
			return true;
		} else return false;
	}


	public ArrayList<String> getPicUrls() {
		return picUrls;
	}

	public void setPicUrls(ArrayList<String> picUrls) {
		this.picUrls = picUrls;
	}
}

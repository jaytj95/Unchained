package com.jasonjohn.unchainedapi;

import java.util.ArrayList;

/**
 * Unchained Restaurant - Foursquare
 * @author Jason John
 *
 */
public class Unchained4SQRestaurant extends UnchainedRestaurant {
	
	/**
	 * Manual Constructor
	 * @param name Name of Venue
	 * @param address Geographical address of venue
	 * @param website Wesbite URL
	 * @param rating rating out of 5
	 */
	public Unchained4SQRestaurant(String name, String address, String website, Double rating, ArrayList<String> pics, String telephone, double[] ll, int pricePoint) {
		super(name, address, website, rating, pics, telephone, ll, pricePoint);
	}

}

package com.jasonjohn.unchainedapi;

import java.util.ArrayList;

/**
 * Unchained Restaurant - Yelp
 * @author Jason John
 *
 */
public class UnchainedYelpRestaurant extends UnchainedRestaurant {

	/**
	 * Manual Constructor
	 * @param name Name of Venue
	 * @param address Geographical address of venue
	 * @param website Wesbite URL
	 * @param rating rating out of 5
	 */
	public UnchainedYelpRestaurant(String name, String address, String website, double rating, ArrayList<String> pics) {
		super(name, address, website, rating, pics);
		// TODO Auto-generated constructor stub
	}


}

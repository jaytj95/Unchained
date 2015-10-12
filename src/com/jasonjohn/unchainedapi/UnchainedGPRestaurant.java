package com.jasonjohn.unchainedapi;

import java.util.ArrayList;

/**
 * Unchained Restaurant - Google Places
 * @author Jason John
 *
 */
public class UnchainedGPRestaurant extends UnchainedRestaurant{
	private String refId;
	/**
	 * Manual Constructor
	 * @param name Name of Venue
	 * @param address Geographical address of venue
	 * @param website Wesbite URL
	 * @param rating rating out of 5
	 */
	public UnchainedGPRestaurant(String name, String address, String website, Double rating, ArrayList<String> pics) {
		super(name, address, website, rating, pics);
		// TODO Auto-generated constructor stub
	}

}

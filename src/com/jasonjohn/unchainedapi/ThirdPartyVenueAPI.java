package com.jasonjohn.unchainedapi;
import java.util.ArrayList;

/**
 * Abstract class that all Third Party Venue APIs (4SQ, Yelp, etc.) must inherit
 * @author Jason John
 *
 */
public abstract class ThirdPartyVenueAPI {
	/**
	 * Required method to get venues, implemented by child class
	 * @param ll lat,lng
	 * @return ArrayList of restaurants from that specific endpoint
	 * @throws UnchainedAPIException 
	 */
	public abstract ArrayList<UnchainedRestaurant> getVenues(String query, String ll) throws UnchainedAPIException;
}

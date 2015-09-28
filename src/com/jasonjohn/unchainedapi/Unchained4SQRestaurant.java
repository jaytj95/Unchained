package com.jasonjohn.unchainedapi;
import fi.foyt.foursquare.api.entities.CompactVenue;

/**
 * Unchained Restaurant - Foursquare
 * @author Jason John
 *
 */
public class Unchained4SQRestaurant extends UnchainedRestaurant {

	/**
	 * Constructor using Foursquare API Dependency
	 * @param venue CompactVenue from API
	 */
	public Unchained4SQRestaurant(CompactVenue venue) {
		super(venue.getName(), venue.getLocation().getAddress(), venue.getCanonicalUrl(), venue.getRating());

		String addr = venue.getLocation().getAddress() + ", " + venue.getLocation().getCity() + ", " 
				+ venue.getLocation().getState();
		this.setAddress(addr);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Manual Constructor
	 * @param name Name of Venue
	 * @param address Geographical address of venue
	 * @param website Wesbite URL
	 * @param rating rating out of 5
	 */
	public Unchained4SQRestaurant(String name, String address, String website, Double rating) {
		super(name, address, website, rating);
	}

}

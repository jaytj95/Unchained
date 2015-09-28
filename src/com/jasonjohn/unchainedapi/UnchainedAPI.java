package com.jasonjohn.unchainedapi;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fi.foyt.foursquare.api.FoursquareApiException;

/**
 * The UnchainedAPI class is a one-stop class to perform the application's core function
 * @author Jason John
 *
 */
public class UnchainedAPI {
	/** 
	 * API Keys for various endpoints
	 */
	private String YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET; //yelp keys
	private String FOURSQUARE_KEY, FOURSQUARE_SECRET; //foursquare keys 
	private String GOOGLE_API_KEY; //geocoding + places
	
	/**
	 * Constructor for the UnchainedAPI - takes in API keys for various endpoints
	 * @param yelpKey - Yelp Consumer Key
	 * @param yelpSecret - Yelp Consumer Secret
	 * @param yelpToken - Yelp Token
	 * @param yelpTokenSecret - Yelp Token Secret
	 * @param fsKey - Foursquare V2 Key
	 * @param fsSecret - Foursquare V2 Secret
	 * @param googleKey - Google Places + Google Geocoding API Key (both must be activated in Developer Console)
	 */
	public UnchainedAPI(String yelpKey, String yelpSecret, String yelpToken, String yelpTokenSecret, 
			String fsKey, String fsSecret, String googleKey) {
		YELP_KEY = yelpKey;
		YELP_SECRET = yelpSecret;
		YELP_TOKEN = yelpToken;
		YELP_TOKEN_SECRET = yelpTokenSecret;
		FOURSQUARE_KEY = fsKey;
		FOURSQUARE_SECRET = fsSecret;
		GOOGLE_API_KEY = googleKey;
		
	}
	
	/**
	 * Core functionality: Load list of chain restaurants, get venues around me, curate the list to remove chains
	 * @param ll lat,lng of current/specified location
	 * @return list of non-chain restaurants
	 * @throws FoursquareApiException - something up with the OAuth?
	 * @throws IOException - something up with the file containing the list of chains? (chains.txt)
	 */
	public ArrayList<UnchainedRestaurant> getUnchainedRestaurants(String ll) throws FoursquareApiException, IOException {
		//get list of chains from file
		ArrayList<String> chains = loadChainRestaurantsList();
		//get list of restaurants around me
		ArrayList<UnchainedRestaurant> restaurantsAroundMe = getVenuesNearby(ll);
		//return curated list
		return curateRestaurants(restaurantsAroundMe, chains);
	}
	/**
	 * Takes in specified or geocoded lat,lng and returns all restaurants nearby
	 * @param ll lat,lng of current/specified location
	 * @return list of all restaurants (chains & non-chains) around specified location
	 * @throws FoursquareApiException - something up with Foursquare OAuth?
	 */
	private ArrayList<UnchainedRestaurant> getVenuesNearby(String ll) throws FoursquareApiException {
		//get Foursquare results for venues
		ArrayList<UnchainedRestaurant> fsResults = get4SQResults(ll);
		//get Yelp results for venues
		ArrayList<UnchainedRestaurant> yelpResults = getYelpResults(ll);
		//get Google Places results for venues
		ArrayList<UnchainedRestaurant> googleResults = getGooglePlacesResults(ll);
		
		//aggregate all results into one arraylist
		ArrayList<UnchainedRestaurant> combined = new ArrayList<>();
		combined.addAll(fsResults);
		combined.addAll(yelpResults);
		combined.addAll(googleResults);

		
		//remove any duplicates
		combined = Util.removeDuplicates(combined);
		combined.trimToSize();

		return combined;
	}

	/**
	 * Load chain restaurants from file as an ArrayList
	 * @return ArrayList of chain restaurants
	 * @throws IOException something wrong with the chains.txt?
	 */
	private ArrayList<String> loadChainRestaurantsList() throws IOException {
		ArrayList<String> chains = new ArrayList<String>();
		InputStream in=ClassLoader.getSystemResourceAsStream("chains.txt");
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String name;
		while((name = reader.readLine()) != null) {
			chains.add(name);
		}
		return chains;
	}
	
	/**
	 * Remove all chains from the aggregated list of venues from Foursquare, Yelp, Google, etc.
	 * @param allVenues aggregate list of venues
	 * @param chain arraylist of chain restaurants
	 * @return list of venues without the chains (non-chains)
	 */
	private ArrayList<UnchainedRestaurant> curateRestaurants(ArrayList<UnchainedRestaurant> allVenues, ArrayList<String> chain) {
		ArrayList<UnchainedRestaurant> nonChains = new ArrayList<>();
		boolean isChain;
		/*
		 * Assume a venue is NOT a chain. If we find it to not be a chain, 
		 * then add it to the list of non-chains. If it is a chain, then flag
		 * it and do nothing with it
		 */
		for(UnchainedRestaurant venue : allVenues) {
			String venueLC = Util.normalizeVenueName(venue.getName());
			isChain = false;
			for(String chainRestaurant : chain) {
				chainRestaurant = Util.normalizeVenueName(chainRestaurant);
				if(venueLC.contains(chainRestaurant)) {
					isChain = true;
					break;
				}
				
			}
			if(!isChain) nonChains.add(venue);
		}
		return nonChains;
	}
	
	/**
	 * Get venue results from Foursquare V2
	 * @param ll lat,lng
	 * @return list of venues from Foursquare
	 */
	private ArrayList<UnchainedRestaurant> get4SQResults(String ll) {
		FoursquareAPI2 fsApi2 = new FoursquareAPI2(FOURSQUARE_KEY, FOURSQUARE_SECRET);
		return fsApi2.getVenues(ll);
	}
	

	/**
	 * Get venue results from Yelp
	 * @param ll lat,lng
	 * @return list of venues from Yelp
	 */
	private ArrayList<UnchainedRestaurant> getYelpResults(String ll) {
		YelpAPI yelpApi = new YelpAPI(YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET);
		return yelpApi.getVenues(ll);
	}


	/**
	 * Get venue results from Google Places
	 * @param ll lat,lng
	 * @return list of venues from Google Places
	 */
	private ArrayList<UnchainedRestaurant> getGooglePlacesResults(String ll) {
		GooglePlacesAPI gpApi = new GooglePlacesAPI(GOOGLE_API_KEY);
		return gpApi.getVenues(ll);
	}
}

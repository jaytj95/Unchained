package com.jasonjohn.unchainedapi;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import fi.foyt.foursquare.api.FoursquareApiException;

public class UnchainedAPI {
	

	
	private String YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET; //yelp keys
	private String FOURSQUARE_KEY, FOURSQUARE_SECRET; //foursquare keys 
	private String GOOGLE_API; //geocoding + places
	
	public UnchainedAPI(String yelpKey, String yelpSecret, String yelpToken, String yelpTokenSecret, 
			String fsKey, String fsSecret, String googleKey) {
		YELP_KEY = yelpKey;
		YELP_SECRET = yelpSecret;
		YELP_TOKEN = yelpToken;
		YELP_TOKEN_SECRET = yelpTokenSecret;
		FOURSQUARE_KEY = fsKey;
		FOURSQUARE_SECRET = fsSecret;
		GOOGLE_API = googleKey;
		
	}
	
	public ArrayList<UnchainedRestaurant> getUnchainedRestaurants(String ll) throws FoursquareApiException, IOException {
		ArrayList<String> chains = loadChainRestaurantsList();
		ArrayList<UnchainedRestaurant> restaurantsAroundMe = getVenuesNearby(ll);
		
		return curateRestaurants(restaurantsAroundMe, chains);
	}
	
	private ArrayList<UnchainedRestaurant> getVenuesNearby(String ll) throws FoursquareApiException {
		ArrayList<UnchainedRestaurant> fsResults = get4SQResults(ll);
		ArrayList<UnchainedRestaurant> yelpResults = getYelpResults(ll);
		ArrayList<UnchainedRestaurant> googleResults = getGooglePlacesResults(ll);
		
		ArrayList<UnchainedRestaurant> combined = new ArrayList<>();
		combined.addAll(fsResults);
		combined.addAll(yelpResults);
		combined.addAll(googleResults);

		
		//remove any duplicates
		combined = Util.removeDuplicates(combined);
		combined.trimToSize();

		return combined;
	}

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
	
	private ArrayList<UnchainedRestaurant> curateRestaurants(ArrayList<UnchainedRestaurant> foursquare, ArrayList<String> chain) {
		ArrayList<UnchainedRestaurant> nonChains = new ArrayList<>();
		boolean isChain;
		for(UnchainedRestaurant venue : foursquare) {
			String venueLC = Util.normalizeVenueName(venue.getName());
			isChain = false;
			for(String chainRestaurant : chain) {
				chainRestaurant = Util.normalizeVenueName(chainRestaurant);
				if(venueLC.contains(chainRestaurant)) {
					isChain = true;
				}
				
			}
			if(!isChain) nonChains.add(venue);
		}
		return nonChains;
	}
	
	private ArrayList<UnchainedRestaurant> get4SQResults(String ll) {
		FoursquareAPI2 fsApi2 = new FoursquareAPI2(FOURSQUARE_KEY, FOURSQUARE_SECRET);
		return fsApi2.getVenues(ll);
	}
	
	private ArrayList<UnchainedRestaurant> getYelpResults(String ll) {
		YelpAPI yelpApi = new YelpAPI(YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET);
		return yelpApi.getVenues(ll);
	}

	private ArrayList<UnchainedRestaurant> getGooglePlacesResults(String ll) {
		GooglePlacesAPI gpApi = new GooglePlacesAPI(GOOGLE_API);
		return gpApi.getVenues(ll);
	}
}

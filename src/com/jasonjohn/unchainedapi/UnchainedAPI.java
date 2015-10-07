package com.jasonjohn.unchainedapi;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;

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
	
	private boolean use4sq, useYelp, useGp;


	public UnchainedAPI() {
		
	}
	
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
		
		use4sq = useGp = useYelp = true;
		
		if(yelpKey == null || yelpSecret == null || yelpToken == null || yelpTokenSecret == null) {
			useYelp = false;
		}
		
		if(fsKey == null || fsSecret == null) {
			use4sq = false;
		}
		
		useGp = (googleKey == null) ? false : true;
		
	}
	
	/**
	 * Core functionality: Load list of chain restaurants, get venues around me, curate the list to remove chains
	 * @param ll lat,lng of current/specified location
	 * @return list of non-chain restaurants
	 * @throws UnchainedAPIException 
	 * @throws IOException - something up with the file containing the list of chains? (chains.txt)
	 */
	public ArrayList<UnchainedRestaurant> getUnchainedRestaurants(String query, String ll) throws UnchainedAPIException {
		query = query.replaceAll(" ", "+");
		//get list of chains from file
		ArrayList<String> chains = loadChainRestaurantsList(new File("chains"));
		//get list of restaurants around me
		ArrayList<UnchainedRestaurant> restaurantsAroundMe = getVenuesNearby(query, ll);
		//return curat	ed list
		return curateRestaurants(restaurantsAroundMe, chains);
	}
	/**
	 * Takes in specified or geocoded lat,lng and returns all restaurants nearby
	 * @param ll lat,lng of current/specified location
	 * @return list of all restaurants (chains & non-chains) around specified location
	 * @throws UnchainedAPIException 
	 */
	private ArrayList<UnchainedRestaurant> getVenuesNearby(String query, String ll) {
		query = Normalizer.normalize(query, Form.NFD);
		query = query.replaceAll("[^A-Za-z0-9]", "");
		
		//get Foursquare results for venues
		ArrayList<UnchainedRestaurant> fsResults = new ArrayList<>();
		//get Yelp results for venues
		ArrayList<UnchainedRestaurant> yelpResults = new ArrayList<>();
		//get Google Places results for venues
		ArrayList<UnchainedRestaurant> googleResults = new ArrayList<>();
		
		//aggregate all results into one arraylist
		ArrayList<UnchainedRestaurant> combined = new ArrayList<>();
		
		if(use4sq) {
			try {
				fsResults = get4SQResults(query, ll);
			} catch (UnchainedAPIException e) {
				//do nothing, leave arraylist empty
			}
			combined.addAll(fsResults);
		}
		
		if(useYelp) {
			try {
				yelpResults = getYelpResults(query, ll);
			} catch (UnchainedAPIException e) {
				//do nothing, leave arraylist empty
			}
			combined.addAll(yelpResults);
		}
		
		if(useGp) {
			try {
				googleResults = getGooglePlacesResults(query, ll);
			} catch (UnchainedAPIException e) {
				//do nothing, leave arraylist empty
			}
			combined.addAll(googleResults);
		}
		
		//remove any duplicates
		combined = Util.removeDuplicatesManually(combined);
		combined.trimToSize();

		return combined;
	}

	/**
	 * Load chain restaurants from file as an ArrayList
	 * @return ArrayList of chain restaurants
	 * @throws IOException something wrong with the chains.txt?
	 */
	private ArrayList<String> loadChainRestaurantsList(File chainsFile) throws UnchainedAPIException {
		ArrayList<String> chains = new ArrayList<String>();
		
		InputStream in= UnchainedAPI.class.getResourceAsStream(chainsFile.getAbsolutePath());
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		String name;
		try {
			while((name = reader.readLine()) != null) {
				chains.add(name);
			}
		} catch (IOException e) {
			throw new UnchainedAPIException("Can't read list of chains");
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
	 * @throws UnchainedAPIException 
	 */
	private ArrayList<UnchainedRestaurant> get4SQResults(String query, String ll) throws UnchainedAPIException {
		FoursquareAPI2 fsApi2 = new FoursquareAPI2(FOURSQUARE_KEY, FOURSQUARE_SECRET);
		return fsApi2.getVenues(query, ll);
	}
	

	/**
	 * Get venue results from Yelp
	 * @param ll lat,lng
	 * @return list of venues from Yelp
	 */
	private ArrayList<UnchainedRestaurant> getYelpResults(String query, String ll) throws UnchainedAPIException {
		YelpAPI yelpApi = new YelpAPI(YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET);
		return yelpApi.getVenues(query, ll);
	}


	/**
	 * Get venue results from Google Places
	 * @param ll lat,lng
	 * @return list of venues from Google Places
	 * @throws UnchainedAPIException 
	 */
	private ArrayList<UnchainedRestaurant> getGooglePlacesResults(String query, String ll) throws UnchainedAPIException {
		GooglePlacesAPI gpApi = new GooglePlacesAPI(GOOGLE_API_KEY);
		return gpApi.getVenues(query, ll);
	}
	

	/**
	 * Return if the API is using Foursquare to get data
	 * @return if API is using Foursquare data
	 */
	public boolean isUsing4sq() {
		return use4sq;
	}

	/**
	 * Return if the API is using Yelp to get data
	 * @return if API is using Yelp data
	 */
	public boolean isUsingYelp() {
		return useYelp;
	}

	/**
	 * Return if the API is using Google Places to get data
	 * @return if API is using Google Places data
	 */
	public boolean isUsingGp() {
		return useGp;
	}
	
	/**
	 * Set use status for Foursquare
	 * @param use4sq
	 */
	public void setUse4sq(boolean use4sq) {

		if(FOURSQUARE_KEY == null || FOURSQUARE_SECRET == null) {
			this.use4sq = false;
		}		
		
		this.use4sq = use4sq;
	}
	
	/**
	 * Set use status for Yelp
	 * @param useYelp
	 */
	public void setUseYelp(boolean useYelp) {

		if(YELP_KEY == null || YELP_SECRET == null || YELP_TOKEN == null || YELP_TOKEN_SECRET == null) {
			this.useYelp = false;
		}		
		
		this.useYelp = useYelp;
	}

	/**
	 * set use status for GP
	 * @param useGp
	 */
	public void setUseGp(boolean useGp) {
		this.useGp = (GOOGLE_API_KEY == null) ? false : useGp;
	}
	
	/**
	 * Set keys for Foursquare
	 * @param key
	 * @param secret
	 */
	public void setFoursquareKeys(String key, String secret) {
		this.FOURSQUARE_KEY = key;
		this.FOURSQUARE_SECRET = secret;
		setUse4sq(true);
	}
	
	/**
	 * Set keys for Yelp
	 * @param key
	 * @param secret
	 * @param tok
	 * @param tokSec
	 */
	public void setYelpKeys(String key, String secret, String tok, String tokSec) {
		YELP_KEY = key;
		YELP_SECRET = secret;
		YELP_TOKEN = tok;
		YELP_TOKEN_SECRET = tokSec;
		setUseYelp(true);
	}
	
	/**
	 * Set key for GP
	 * @param key
	 */
	public void setGPKey(String key) {
		GOOGLE_API_KEY = key;
		setUseGp(true);
	}
}

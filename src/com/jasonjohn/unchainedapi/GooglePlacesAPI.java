package com.jasonjohn.unchainedapi;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Google Places API class to manually query venues
 * @author Jason John
 *
 */
public class GooglePlacesAPI extends ThirdPartyVenueAPI {
	/**
	 * Google Places search endpoint
	 */
	public static final String GOOGLE_PLACES_ENDPOINT = 
			"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=1000%s&types=food&key=%s";
	
	/**
	 * Google Key
	 */
	private String GOOGLE_KEY;
	
	/**
	 * Constructor that takes in the API key for Google Places
	 * @param key API Key
	 */
	public GooglePlacesAPI(String key) {
		GOOGLE_KEY = key;
	}

	/**
	 * Required method to get venues from GP
	 * @param ll lat,lng
	 * @return ArrayList of restaurants from GP
	 */
	@Override
	public ArrayList<UnchainedRestaurant> getVenues(String query, String ll) throws UnchainedAPIException {
		//check to make sure user entered a query
		query = (query == null || query.equals("")) ? "" : "&keyword="+query;
		ArrayList<UnchainedRestaurant> venues = new ArrayList<>();
		//format endpoint for key, secret, and lat/lng
		String url = String.format(GOOGLE_PLACES_ENDPOINT, ll, query, GOOGLE_KEY);
		JSONObject googleResponse = Util.getJsonFromUrl(url);
		try {
			//if meta code returns successful
			if(googleResponse.getString("status").equals("OK")) {
				//bunch of JSON sifting to get what we want
				JSONArray array = googleResponse.getJSONArray("results");
				for(int i = 0; i < array.length(); i++) {
					JSONObject gVenue = array.getJSONObject(i);
					String name = gVenue.getString("name");
					String address = (gVenue.has("vicinity")) ? gVenue.getString("vicinity") : null;
					String website = null; //no website data...let superclass handle google search url
					double rating = (gVenue.has("rating")) ? gVenue.getDouble("rating") : -1;
					venues.add(new UnchainedRestaurant(name, address, website, rating));
				}
			} else {
				throw new UnchainedAPIException("Error getting GP venues");
			}
		} catch (JSONException e) {
			throw new UnchainedAPIException("Error getting GP venues");
		}
		
		
		return venues;
		
	}

}

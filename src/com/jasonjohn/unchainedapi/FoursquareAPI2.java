package com.jasonjohn.unchainedapi;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * FoursquareAPI2 class to manually query venues (dependency is only good for OAuth)
 * @author Jason John
 *
 */
public class FoursquareAPI2 extends ThirdPartyVenueAPI {
	/**
	 * Foursquare V2 search endpoint
	 */
	public static final String FS_SEARCH = "https://api.foursquare.com/v2/venues/explore?"
			+ "client_id=%s"
			+ "&client_secret=%s"
			+ "&v=20150922"
			+ "&ll=%s"
			+ "&query=restaurant";

	/**
	 * Foursquare Key and Secret
	 */
	private String FS_KEY, FS_SECRET;

	/**
	 * Constructor that takes in a key and secret
	 * @param key Foursquare V2 API Key
	 * @param secret Foursquare V2 API Secret
	 */
	public FoursquareAPI2(String key, String secret) {
		FS_KEY = key;
		FS_SECRET = secret;
	}

	/**
	 * Required method to get venues from Foursquare
	 * @param ll lat,lng
	 * @return ArrayList of restaurants from Foursquare
	 */
	@Override
	public ArrayList<UnchainedRestaurant> getVenues(String ll) {
		ArrayList<UnchainedRestaurant> venues = new ArrayList<>();
		//format endpoint for key, secret, and lat/lng
		String url = String.format(FS_SEARCH, FS_KEY, FS_SECRET, ll);
		JSONObject fsResponse = Util.getJsonFromUrl(url);
		try {
			//if meta code returns successful
			if(fsResponse.getJSONObject("meta").getInt("code") == 200) {
				//bunch of JSON sifting to get what we want
				JSONObject response = fsResponse.getJSONObject("response");
				JSONArray groups = response.getJSONArray("groups");
				JSONObject temp = groups.getJSONObject(0);
				JSONArray items = temp.getJSONArray("items");

				for(int i = 0; i < items.length(); i++) {
					JSONObject venue = items.getJSONObject(i).getJSONObject("venue");
					String name = venue.getString("name");
					String address, website;
					double rating;
					try {
						//address building
						address = venue.getJSONObject("location").getString("address") 
								+ ", " + venue.getJSONObject("location").getString("city")
								+ ", " + venue.getJSONObject("location").getString("state");
					} catch(JSONException e) {
						address = null;
					}

					try{
						//website data in JSON actually brings you to COMPANY website, we want to navigate to Foursquare website
						website = String.format("https://foursquare.com/v/%s/%s", name.replaceAll("[^a-zA-Z0-9]", "-"), 
								venue.getString("id"));
					} catch(JSONException e) {
						website = null;
					}

					try {
						//we want ratings out of 5, not out of 10
						rating = venue.getDouble("rating")/2;
					} catch(JSONException e) {
						rating = -1;
					}
					//create an UnchainedRestaurant out of this and add it to the list of venues
					Unchained4SQRestaurant fsRestaurant = new Unchained4SQRestaurant(name, address, website, rating);
					venues.add(fsRestaurant);
				}

			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return venues;
	}


}

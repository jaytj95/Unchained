package com.jasonjohn.unchainedapi;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GooglePlacesAPI extends ThirdPartyVenueAPI {

	public static final String GOOGLE_PLACES_ENDPOINT = 
			"https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=%s&radius=500&types=food&key=%s";
	
	private String GOOGLE_KEY;
	
	public GooglePlacesAPI(String key) {
		GOOGLE_KEY = key;
	}

	@Override
	public ArrayList<UnchainedRestaurant> getVenues(String ll) {
		ArrayList<UnchainedRestaurant> venues = new ArrayList<>();
		String url = String.format(GOOGLE_PLACES_ENDPOINT, ll, GOOGLE_KEY);
		JSONObject googleResponse = Util.getJsonFromUrl(url);
		try {
			if(googleResponse.getString("status").equals("OK")) {
				JSONArray array = googleResponse.getJSONArray("results");
				for(int i = 0; i < array.length(); i++) {
					JSONObject gVenue = array.getJSONObject(i);
					String name = gVenue.getString("name");
					String address = (gVenue.has("vicinity")) ? gVenue.getString("vicinity") : null;
					String website = null; //no website data...let superclass handle google search url
					double rating = (gVenue.has("rating")) ? gVenue.getDouble("rating") : -1;
					venues.add(new UnchainedRestaurant(name, address, website, rating));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return venues;
		
	}

}

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FoursquareAPI2 {
	public static final String FS_SEARCH = "https://api.foursquare.com/v2/venues/explore?"
			+ "client_id=%s"
			+ "&client_secret=%s"
			+ "&v=20150922"
			+ "&ll=%s"
			+ "&query=sushi";

	public static final String FOURSQUARE_ID = "NVH2HBDEWL00GLGRYWZMDSFK2FUZR00ICNDW0OOGXL13NUFY";
	public static final String FOURSQUARE_SECRET = "TV04OXE1WM32JEHQLJTETFOE35KDHCEPNRHY35YCV5OOAH04";
	public static final String FOURSQUARE_CATEGORY_RESTAURANTS = "4d4b7105d754a06374d81259";


	private String oAuth;
	public FoursquareAPI2(String oAuth) {
		this.oAuth = oAuth;
	}

	public static ArrayList<UnchainedRestaurant> get4SQVenues(String ll) {
		ArrayList<UnchainedRestaurant> venues = new ArrayList<>();
		String url = String.format(FS_SEARCH, FOURSQUARE_ID, FOURSQUARE_SECRET, ll);
		JSONObject fsResponse = Util.getJsonFromUrl(url);
		try {
			if(fsResponse.getJSONObject("meta").getInt("code") == 200) {
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
						address = venue.getJSONObject("location").getString("address") 
								+ ", " + venue.getJSONObject("location").getString("city")
								+ ", " + venue.getJSONObject("location").getString("state");
					} catch(JSONException e) {
						address = null;
					}
					
					try{
						website = venue.getString("url");
					} catch(JSONException e) {
						website = null;
					}
					
					try {
						rating = venue.getDouble("rating")/2;
					} catch(JSONException e) {
						rating = -1;
					}

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

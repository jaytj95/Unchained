package com.jasonjohn.unchainedapi;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Code sample for accessing the Yelp API V2.
 * 
 * This program demonstrates the capability of the Yelp API version 2.0 by using the Search API to
 * query for businesses by a search term and location, and the Business API to query additional
 * information about the top result from the search query.
 * 
 * <p>
 * See <a href="http://www.yelp.com/developers/documentation">Yelp Documentation</a> for more info.
 * 
 */
public class YelpAPI extends ThirdPartyVenueAPI {

	private static final String API_HOST = "api.yelp.com";
	private static final int SEARCH_LIMIT = 20; //MAX
	private static final String SEARCH_PATH = "/v2/search";
	private static final String BUSINESS_PATH = "/v2/business";
	private static final String CATEGORY_FILTER = "food";

	OAuthService service;
	Token accessToken;

	/**
	 * Setup the Yelp API OAuth credentials.
	 * 
	 * @param consumerKey Consumer key
	 * @param consumerSecret Consumer secret
	 * @param token Token
	 * @param tokenSecret Token secret
	 */
	public YelpAPI(String key, String secret, String token, String tokenSecret) {
		this.service =
				new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(key)
				.apiSecret(secret).build();
		this.accessToken = new Token(token, tokenSecret);
	}

	/**
	 * Creates and sends a request to the Search API by term and location.
	 * <p>
	 * See <a href="http://www.yelp.com/developers/documentation/v2/search_api">Yelp Search API V2</a>
	 * for more info.
	 * 
	 * @param term <tt>String</tt> of the search term to be queried
	 * @param location <tt>String</tt> of the location
	 * @return <tt>String</tt> JSON Response
	 */
	private String searchForBusinessesByLocation(String term, String location) {
		OAuthRequest request = createOAuthRequest(SEARCH_PATH);
		request.addQuerystringParameter("term", term);
		request.addQuerystringParameter("location", location);
		request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
		request.addQuerystringParameter("category_filter", CATEGORY_FILTER);
		return sendRequestAndGetResponse(request);
	}
	private String searchForBusinessesByLL(String term, String ll) {
		OAuthRequest request = createOAuthRequest(SEARCH_PATH);
		request.addQuerystringParameter("term", term);
		request.addQuerystringParameter("ll", ll);
		request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
		request.addQuerystringParameter("category_filter", CATEGORY_FILTER);
		return sendRequestAndGetResponse(request);
	}

	/**
	 * Creates and sends a request to the Business API by business ID.
	 * <p>
	 * See <a href="http://www.yelp.com/developers/documentation/v2/business">Yelp Business API V2</a>
	 * for more info.
	 * 
	 * @param businessID <tt>String</tt> business ID of the requested business
	 * @return <tt>String</tt> JSON Response
	 */
	private String searchByBusinessId(String businessID) {
		OAuthRequest request = createOAuthRequest(BUSINESS_PATH + "/" + businessID);
		return sendRequestAndGetResponse(request);
	}

	/**
	 * Creates and returns an {@link OAuthRequest} based on the API endpoint specified.
	 * 
	 * @param path API endpoint to be queried
	 * @return <tt>OAuthRequest</tt>
	 */
	private OAuthRequest createOAuthRequest(String path) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + path);
		return request;
	}

	/**
	 * Sends an {@link OAuthRequest} and returns the {@link Response} body.
	 * 
	 * @param request {@link OAuthRequest} corresponding to the API request
	 * @return <tt>String</tt> body of API response
	 */
	private String sendRequestAndGetResponse(OAuthRequest request) {
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	/**
	 * Queries the Search API based on the command line arguments and takes the first result to query
	 * the Business API.
	 * 
	 */
	@Override
	public ArrayList<UnchainedRestaurant> getVenues(String query, String ll) throws UnchainedAPIException {
		//check to make sure user entered a query
		query = (query == null || query.equals("")) ? "restaurant" : query;
		ArrayList<UnchainedRestaurant> list = new ArrayList<>();
		String searchResponseJSON = searchForBusinessesByLL(query, ll);
		JSONParser parser = new JSONParser();
		JSONObject response = null;
		try {
			response = (JSONObject) parser.parse(searchResponseJSON);
		} catch (ParseException pe) {
			System.out.println("Error: could not parse JSON response:");
			System.exit(1);
		}

		JSONArray businesses = (JSONArray) response.get("businesses");
		try {
			for(int i = 0; i < businesses.size(); i++) {
				JSONObject restaurant = (JSONObject) businesses.get(i);
				String name = (String) restaurant.get("name");

				JSONObject location = (JSONObject) restaurant.get("location");
				String address = null;
				double[] latlng = new double[]{0,0};
				if(location != null) {
					JSONArray addrArray = (JSONArray) location.get("address");
					address = (String) addrArray.get(0);
					if(address != null) {
						address += ", " + (String) location.get("city");
						address += ", " + (String) location.get("state_code");
						address += ", " + (String) location.get("postal_code");
					}
					
					JSONObject coord = (JSONObject) location.get("coordinate");
					latlng = new double[]{(Double)coord.get("latitude"), (Double)coord.get("longitude")};
				}


				String website = (String) restaurant.get("mobile_url");
				double rating = (Double) restaurant.get("rating");

				ArrayList<String> picUrls = new ArrayList<>();
				String pic = (String) restaurant.get("image_url");
				if(pic != null) {
					pic = pic.replace("ms.jpg", "o.jpg");
					picUrls.add(pic);
				}
				String phone = null;
				phone = (String) restaurant.get("display_phone");
				UnchainedYelpRestaurant yelpRestaurant = new UnchainedYelpRestaurant(name, address, website, rating, picUrls, phone, latlng, 2);
				list.add(yelpRestaurant);
			}
		} catch (Exception e) {
			throw new UnchainedAPIException("Error getting Yelp venues");
		}

		return list;
	}

}

package com.jasonjohn.unchainedapi;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pablo127.almonds.GetCallback;
import pablo127.almonds.Parse;
import pablo127.almonds.ParseException;
import pablo127.almonds.ParseObject;
import pablo127.almonds.ParseQuery;

/**
 * Utility class for various helpful functions
 * @author Jason John
 *
 */
public class Util {

	/**
	 * Google Geocoding key and secret
	 */
	public static final String GOOGLE_GEOCODING_KEY = "AIzaSyBNxtP1FnsCQoBz6pOozC-WVRo_2ZoCmzQ";
	public static final String GOOGLE_GEOCODING_ENDPOINT = 
			"https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=" + GOOGLE_GEOCODING_KEY;
	public static final String PARSE_APP_ID = "vI3NcItKiNXqihGNhwRTByuLEldT0z3Xys0hMPe2";
	public static final String PARSE_REST_ID = "PO0MmneJArScl7MJAvkzPjov2l3M6CdCSlP9oMzX"; 
	


	/**
	 * Remove duplicate venue names
	 * Throw them into a Set which doesn't allow for duplicates, then throw the set back into an arraylist
	 * @param arraylist list of venues with duplicates
	 * @return arraylist without duplicate venues
	 */
	public static ArrayList<UnchainedRestaurant> removeDuplicates(ArrayList<UnchainedRestaurant> arraylist) {
		//remove any duplicates
		ArrayList<UnchainedRestaurant> noDuplicates = new ArrayList<>();
		Set<UnchainedRestaurant> setItems = new LinkedHashSet<UnchainedRestaurant>(arraylist);
		noDuplicates.addAll(setItems);
		return noDuplicates;
	}

	public static ArrayList<UnchainedRestaurant> removeDuplicatesManually(ArrayList<UnchainedRestaurant> chains) {
		int size = chains.size();

		// not using a method in the check also speeds up the execution
		// also i must be less that size-1 so that j doesn't
		// throw IndexOutOfBoundsException
		for (int i = 0; i < size - 1; i++) {
			// start from the next item after strings[i]
			// since the ones before are checked
			for (int j = i + 1; j < size; j++) {
				// no need for if ( i == j ) here
				if (!chains.get(j).equals(chains.get(i)))
					continue;
				chains.remove(j);
				// decrease j because the array got re-indexed
				j--;
				// decrease the size of the array
				size--;
			} // for j
		} // for i

		return chains;

	}

	/**
	 * Get lat,lng from a Google Maps query (ie "Mall of GA")
	 * @param query maps query (ie "Mall of GA" or "Buford Hwy")
	 * @return lat,lng of specified location
	 * @throws UnchainedAPIException 
	 */
	public static String getLatLngFromMapsQuery(String query) throws UnchainedAPIException {
		System.out.println("Geocoding query: " + query);
		query = query.replace(' ', '+');
		query = Normalizer.normalize(query, Form.NFD);
		query = query.replaceAll("[^A-Za-z0-9]", "");
		query = String.format(GOOGLE_GEOCODING_ENDPOINT, query);
		JSONObject responseJson = getJsonFromUrl(query);
		try {
			if(responseJson.getString("status").equals("OK")) {
				JSONArray resultsArray = responseJson.getJSONArray("results");
				JSONObject geometryObject = resultsArray.getJSONObject(0).getJSONObject("geometry");
				JSONObject location = geometryObject.getJSONObject("location");
				String ll = location.getString("lat") + "," + location.getString("lng");

				System.out.println("Geocoding result: ll =" + ll);
				return ll;
			} else {
				throw new UnchainedAPIException("GOOGLE GEOCODE ERROR: STATUS = " + responseJson.getString("status"));
			}

		} catch (JSONException e) {
			throw new UnchainedAPIException("GOOGLE GEOCODE ERROR: BAD JSON");
		}
	}

	/**
	 * Function to query a URL and retrieve a JSON Response
	 * @param url
	 * @return
	 */
	public static JSONObject getJsonFromUrl(String url) {
		JSONObject jObj = null;
		HttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Content-type", "application/json");
		InputStream inputStream = null;
		String result = null;
		try {
			HttpResponse response = httpclient.execute(httpGet);           
			HttpEntity entity = response.getEntity();

			inputStream = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 64);
			StringBuilder sb = new StringBuilder();

			String line = null;
			while ((line = reader.readLine()) != null)
			{
				sb.append(line + "\n");
			}
			result = sb.toString();
		} catch (IllegalStateException e) { 
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try{if(inputStream != null)inputStream.close();}catch(Exception e){}
		}
		try {
			jObj = new JSONObject(result);
		} catch (JSONException e) {
			e.getMessage();
		}
		return jObj;
	}


	/**
	 * Normalize venue names (used for comparisons) so that "McDonald's == Mcdonalds")
	 * @param name
	 * @return
	 */
	public static String normalizeVenueName(String name) {
		String normalized = name;
		normalized = normalized.toLowerCase();
		normalized = normalized.replaceAll("[^A-Za-z0-9]", "");
		return normalized;
	}
	
	/**
	 * Update specified chains file if it's out of date (24 hrs)
	 * @param path the path to the chains file
	 */
	public static void updateChainsFile(File file) {
		long fileTime = file.lastModified();
		long currentTime = System.currentTimeMillis();
		if((currentTime - fileTime) < 8.64e7) {
			System.out.println("Need new file from Parse...");
			//time to do parse stuff
			Parse.initialize(PARSE_APP_ID, PARSE_REST_ID);
			ParseQuery query = new ParseQuery("Chains");
			query.getInBackground("03plzIDDms", new GetCallback() {
				@Override
				public void done(ParseObject obj) {
					if (obj != null) {
						System.out.println("SUCCESS");
						
					} else {
						System.out.println("FAIL");
					}
				}
			});
		}
	}
}

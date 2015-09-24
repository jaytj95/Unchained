package com.jasonjohn.unchainedapi;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
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

public class Util {

//	public static final String GOOGLE_GEOCODING_API = 
//			"http://maps.googleapis.com/maps/api/geocode/json?address=%s&sensor=true";
	
	public static final String GOOGLE_GEOCODING_KEY = "AIzaSyBNxtP1FnsCQoBz6pOozC-WVRo_2ZoCmzQ";
	public static final String GOOGLE_GEOCODING_ENDPOINT = 
			"https://maps.googleapis.com/maps/api/geocode/json?address=%s&key=" + GOOGLE_GEOCODING_KEY;


	public static ArrayList<UnchainedRestaurant> removeDuplicates(ArrayList<UnchainedRestaurant> arraylist) {
		//remove any duplicates
		ArrayList<UnchainedRestaurant> noDuplicates = new ArrayList<>();
		Set<UnchainedRestaurant> setItems = new LinkedHashSet<UnchainedRestaurant>(arraylist);
		noDuplicates.addAll(setItems);
		return noDuplicates;
	}
	
	public static String getLatLngFromMapsQuery(String query) {
		System.out.println("Geocoding query: " + query);
		query = query.replace(' ', '+');
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
				System.err.println("GOOGLE GEOCODE ERROR: STATUS = " + responseJson.getString("status"));
				return null;
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
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
	

	public static String normalizeVenueName(String name) {
		String normalized = name;
		normalized = normalized.toLowerCase();
		normalized = normalized.replaceAll("[^A-Za-z0-9]", "");
		return normalized;
	}
}

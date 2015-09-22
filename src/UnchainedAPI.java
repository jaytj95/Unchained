import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;

import fi.foyt.foursquare.api.FoursquareApi;
import fi.foyt.foursquare.api.FoursquareApiException;
import fi.foyt.foursquare.api.Result;
import fi.foyt.foursquare.api.entities.CompactVenue;
import fi.foyt.foursquare.api.entities.VenuesSearchResult;

public class UnchainedAPI {
	
	public static final String YELP_KEY = "1y6Y9ZQBDOctIKrq5NO7XQ";
	public static final String YELP_SECRET = "852Nfvhn9yd7GnXoOyNsygmT2Ks";
	public static final String YELP_TOKEN = "OHVBilTnx0nmS8_fVQxMJ6s41fcLoZA9";
	public static final String YELP_TOKEN_SECRET = "3bjEG5GcVc-3vJ6UQIt1bgrGD2o";
	
	
	public static ArrayList<UnchainedRestaurant> getUnchainedRestaurants(String ll) throws FileNotFoundException, FoursquareApiException {
		ArrayList<String> chains = loadChainRestaurantsList();
		ArrayList<UnchainedRestaurant> restaurantsAroundMe = getVenuesNearby(ll);
		
		return curateRestaurants(restaurantsAroundMe, chains);
	}
	
	private static ArrayList<UnchainedRestaurant> getVenuesNearby(String ll) throws FoursquareApiException {
		ArrayList<UnchainedRestaurant> fsResults = FoursquareAPI2.get4SQVenues(ll);
		ArrayList<UnchainedRestaurant> yelpResults = getYelpResults(ll);
		
		ArrayList<UnchainedRestaurant> combined = new ArrayList<>();
		combined.addAll(fsResults);
		combined.addAll(yelpResults);

		//remove any duplicates
		combined = Util.removeDuplicates(combined);
		return combined;
	}
	
	private static ArrayList<String> loadChainRestaurantsList() throws FileNotFoundException {
		Scanner s = new Scanner(new File("files/chains.txt"));
		ArrayList<String> chains = new ArrayList<String>();
		while (s.hasNextLine()){
		    chains.add(s.nextLine());
		}
		s.close();
		return chains;
	}
	
	private static ArrayList<UnchainedRestaurant> curateRestaurants(ArrayList<UnchainedRestaurant> foursquare, ArrayList<String> chain) {
		ArrayList<UnchainedRestaurant> nonChains = new ArrayList<>();
		boolean isChain;
		for(UnchainedRestaurant venue : foursquare) {
			String venueLC = venue.getName().toLowerCase();
			isChain = false;
			for(String chainRestaurant : chain) {
				chainRestaurant = chainRestaurant.toLowerCase();
				if(venueLC.contains(chainRestaurant)) {
					isChain = true;
				}
				
			}
			if(!isChain) nonChains.add(venue);
		}
		return nonChains;
	}
	
	
	
	/* VENUES */
//	private static ArrayList<UnchainedRestaurant> get4SQVenues(String ll) throws FoursquareApiException {
//		ArrayList<UnchainedRestaurant> result = new ArrayList<>();
//		FoursquareApi foursquareApi = new FoursquareApi(FOURSQUARE_ID, FOURDQUARE_SECRET, "www.google.com");
//		foursquareApi.setVersion("20150917");
//		
//		Result<VenuesSearchResult> fsResult = foursquareApi.venuesSearch(ll, null, null, null, null, 100, null, FOURSQUARE_CATEGORY_RESTAURANTS, null, null, null, 24141, null);
//		
//		if (fsResult.getMeta().getCode() == 200) {
//			for(CompactVenue v: fsResult.getResult().getVenues()) {
//				Unchained4SQRestaurant fsRestaurant = new Unchained4SQRestaurant(v);
//				result.add(fsRestaurant);
//			}
//		} else {
//			// TODO: Proper error handling
//			System.out.println("Error occured: ");
//			System.out.println("  code: " + fsResult.getMeta().getCode());
//			System.out.println("  type: " + fsResult.getMeta().getErrorType());
//			System.out.println("  detail: " + fsResult.getMeta().getErrorDetail()); 
//		}
//		
//		return result;
//	}
	
	private static ArrayList<UnchainedRestaurant> getYelpResults(String ll) {
		YelpAPI yelpApi = new YelpAPI(YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET);
		return yelpApi.queryAPI(yelpApi, ll);
	}
}

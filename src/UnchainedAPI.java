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
	
	public UnchainedAPI() {
		
	}
	
	public ArrayList<UnchainedRestaurant> getUnchainedRestaurants(String ll) throws FileNotFoundException, FoursquareApiException {
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
		return combined;
	}

	private ArrayList<String> loadChainRestaurantsList() throws FileNotFoundException {
		Scanner s = new Scanner(new File("files/chains.txt"));
		ArrayList<String> chains = new ArrayList<String>();
		while (s.hasNextLine()){
		    chains.add(s.nextLine());
		}
		s.close();
		return chains;
	}
	
	private ArrayList<UnchainedRestaurant> curateRestaurants(ArrayList<UnchainedRestaurant> foursquare, ArrayList<String> chain) {
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
	
	private ArrayList<UnchainedRestaurant> get4SQResults(String ll) {
		FoursquareAPI2 fsApi2 = new FoursquareAPI2();
		return fsApi2.getVenues(ll);
	}
	
	private ArrayList<UnchainedRestaurant> getYelpResults(String ll) {
		YelpAPI yelpApi = new YelpAPI();
		return yelpApi.getVenues(ll);
	}

	private ArrayList<UnchainedRestaurant> getGooglePlacesResults(String ll) {
		GooglePlacesAPI gpApi = new GooglePlacesAPI();
		return gpApi.getVenues(ll);
	}
}

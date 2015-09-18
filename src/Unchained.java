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

public class Unchained {
	public static final String FOURSQUARE_ID = "NVH2HBDEWL00GLGRYWZMDSFK2FUZR00ICNDW0OOGXL13NUFY";
	public static final String FOURDQUARE_SECRET = "TV04OXE1WM32JEHQLJTETFOE35KDHCEPNRHY35YCV5OOAH04";
	public static final String FOURSQUARE_CATEGORY_RESTAURANTS = "4d4b7105d754a06374d81259";
	
	public static final String YELP_KEY = "1y6Y9ZQBDOctIKrq5NO7XQ";
	public static final String YELP_SECRET = "852Nfvhn9yd7GnXoOyNsygmT2Ks";
	public static final String YELP_TOKEN = "OHVBilTnx0nmS8_fVQxMJ6s41fcLoZA9";
	public static final String YELP_TOKEN_SECRET = "3bjEG5GcVc-3vJ6UQIt1bgrGD2o";
	
	
	public static final String LL = "33.890917,-84.030136";
	
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter lat,lng");
		String ll = keyboard.next();
		keyboard.close();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ArrayList<String> chains = loadChainRestaurants();
					ArrayList<String> restaurantsAroundMe = getVenues(LL);
					ArrayList<String> nonChains = curateRestaurants(restaurantsAroundMe, chains);
					for(String v : nonChains) {
						System.out.println(v);
					}
					System.out.println("\nWe found " + nonChains.size() + " non-chains");
					System.out.println("\n\nBrought to you by Foursquare and Yelp!");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FoursquareApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}


	public static ArrayList<String> getVenues(String ll) throws FoursquareApiException {
		ArrayList<String> fsResults = get4SQVenues(ll);
		ArrayList<String> yelpResults = getYelpResults(ll);
		
		ArrayList<String> combined = new ArrayList<>();
		combined.addAll(fsResults);
		combined.addAll(yelpResults);
		return combined;
	}
	
	public static ArrayList<String> loadChainRestaurants() throws FileNotFoundException {
		Scanner s = new Scanner(new File("files/chains.txt"));
		ArrayList<String> chains = new ArrayList<String>();
		while (s.hasNextLine()){
		    chains.add(s.nextLine());
		}
		s.close();
		return chains;
	}
	
	public static ArrayList<String> curateRestaurants(ArrayList<String> foursquare, ArrayList<String> chain) {
		ArrayList<String> nonChains = new ArrayList<>();
		boolean isChain;
		for(String venue : foursquare) {
			String venueLC = venue.toLowerCase();
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
	public static ArrayList<String> get4SQVenues(String ll) throws FoursquareApiException {
		ArrayList<String> result = new ArrayList<>();
		FoursquareApi foursquareApi = new FoursquareApi(FOURSQUARE_ID, FOURDQUARE_SECRET, "www.google.com");
		foursquareApi.setVersion("20150917");
		// After client has been initialized we can make queries.
		Result<VenuesSearchResult> fsResult = foursquareApi.venuesSearch(ll, null, null, null, null, 100, null, FOURSQUARE_CATEGORY_RESTAURANTS, null, null, null, 24141, null);

		if (fsResult.getMeta().getCode() == 200) {
			for(CompactVenue v: fsResult.getResult().getVenues()) {
				result.add(v.getName());
			}
		} else {
			// TODO: Proper error handling
			System.out.println("Error occured: ");
			System.out.println("  code: " + fsResult.getMeta().getCode());
			System.out.println("  type: " + fsResult.getMeta().getErrorType());
			System.out.println("  detail: " + fsResult.getMeta().getErrorDetail()); 
		}
		Set setItems = new LinkedHashSet(result);
		result.clear();
		result.addAll(setItems);
		return result;
	}
	
	public static ArrayList<String> getYelpResults(String ll) {
		YelpAPI yelpApi = new YelpAPI(YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET);
		return yelpApi.queryAPI(yelpApi);
	}
}

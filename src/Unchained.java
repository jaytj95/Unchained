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
	public static final String CLIENT_ID = "NVH2HBDEWL00GLGRYWZMDSFK2FUZR00ICNDW0OOGXL13NUFY";
	public static final String CLIENT_SECRET = "TV04OXE1WM32JEHQLJTETFOE35KDHCEPNRHY35YCV5OOAH04";
	public static final String CATEGORY_RESTAURANTS = "4d4b7105d754a06374d81259";
	public static void main(String[] args) {
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter lat,lng");
		String ll = keyboard.next();
		keyboard.close();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ArrayList<String> venues = 
							curateRestaurants(getVenues(ll), loadChainRestaurants());
					for(String v : venues) {
						System.out.println(v);
					}
				} catch (FoursquareApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}


	public static ArrayList<String> getVenues(String ll) throws FoursquareApiException {
		ArrayList<String> result = new ArrayList<>();
		FoursquareApi foursquareApi = new FoursquareApi(CLIENT_ID, CLIENT_SECRET, "www.google.com");
		foursquareApi.setVersion("20150917");
		// After client has been initialized we can make queries.
		Result<VenuesSearchResult> fsResult = foursquareApi.venuesSearch(ll, null, null, null, null, 50, null, CATEGORY_RESTAURANTS, null, null, null, 20000, null);

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
		for(String venue : foursquare) {
			for(String chainRestaurant : chain) {
				if(!venue.contains(chainRestaurant)) {
					nonChains.add(venue);
				}
			}
		}
		return nonChains;
	}
}

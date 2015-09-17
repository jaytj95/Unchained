import java.util.Scanner;

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
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					getVenues(ll);
				} catch (FoursquareApiException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
		}).start();
	}


	public static void getVenues(String ll) throws FoursquareApiException {
		FoursquareApi foursquareApi = new FoursquareApi(CLIENT_ID, CLIENT_SECRET, "www.google.com");
		foursquareApi.setVersion("20150917");
		// After client has been initialized we can make queries.
		Result<VenuesSearchResult> result = foursquareApi.venuesSearch(ll, null, null, null, null, 50, null, CATEGORY_RESTAURANTS, null, null, null, 20000, null);

		if (result.getMeta().getCode() == 200) {
			// if query was ok we can finally we do something with the data
			for (CompactVenue venue : result.getResult().getVenues()) {
				// TODO: Do something we the data
				System.out.println(venue.getName());
			}
		} else {
			// TODO: Proper error handling
			System.out.println("Error occured: ");
			System.out.println("  code: " + result.getMeta().getCode());
			System.out.println("  type: " + result.getMeta().getErrorType());
			System.out.println("  detail: " + result.getMeta().getErrorDetail()); 
		}
	}
}

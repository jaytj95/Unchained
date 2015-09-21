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

public class UnchainedTester {
	public static final String DEAFULT_LL = "34.061982,-83.983360";
	
	public static void main(String[] args) {
		
		//JAVA SAMPLE 
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter lat,lng");
		String ll = keyboard.next();
		keyboard.close();
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					ArrayList<String> nonChains = Unchained.getUnchainedRestaurants(ll);
					for(String v : nonChains) {
						System.out.println(v);
					}
					System.out.println("\nWe found " + nonChains.size() + " non-chains");
					System.out.println("Brought to you by Foursquare and Yelp!");
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
}

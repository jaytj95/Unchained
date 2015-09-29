package com.jasonjohn.unchainedapi;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import fi.foyt.foursquare.api.FoursquareApiException;

public class UnchainedTester {
	public static final String GOOGLE_PLACES_KEY = "AIzaSyBNxtP1FnsCQoBz6pOozC-WVRo_2ZoCmzQ";
	public static final String YELP_KEY = "1y6Y9ZQBDOctIKrq5NO7XQ";
	public static final String YELP_SECRET = "852Nfvhn9yd7GnXoOyNsygmT2Ks";
	public static final String YELP_TOKEN = "OHVBilTnx0nmS8_fVQxMJ6s41fcLoZA9";
	public static final String YELP_TOKEN_SECRET = "3bjEG5GcVc-3vJ6UQIt1bgrGD2o";
	public static final String FOURSQUARE_ID = "NVH2HBDEWL00GLGRYWZMDSFK2FUZR00ICNDW0OOGXL13NUFY";
	public static final String FOURSQUARE_SECRET = "TV04OXE1WM32JEHQLJTETFOE35KDHCEPNRHY35YCV5OOAH04";
	public static final String DEAFULT_LL = "34.0619825,-83.9833599";

	public static void main(String[] args) {		
		//JAVA SAMPLE 
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter lat,lng or maps query");
		final String ll = keyboard.nextLine();

		Thread thread = new Thread(new Runnable() {
			UnchainedAPI unchainedApi = new UnchainedAPI(YELP_KEY, YELP_SECRET, YELP_TOKEN, YELP_TOKEN_SECRET, 
					FOURSQUARE_ID, FOURSQUARE_SECRET, GOOGLE_PLACES_KEY);
			String query = ll;
			@Override
			public void run() {
				System.out.printf("API STATUS:\nFoursquare: %b\tYelp: %b\tGoogle: %b\n\n", 
						unchainedApi.isUsing4sq(), unchainedApi.isUsingYelp(), unchainedApi.isUsingGp());
				if(!query.matches("-?[0-9.]*,-?[0-9.]*")) { 
					query = Util.getLatLngFromMapsQuery(query);
				}
				try {
					System.out.println("Finding venues...");
					ArrayList<UnchainedRestaurant> nonChains = unchainedApi.getUnchainedRestaurants(query);
					
					for(UnchainedRestaurant v : nonChains) {
						System.out.println(nonChains.indexOf(v) + 1 + ". " + v.getName());
					}
					System.out.println("\nWe found " + nonChains.size() + " non-chains\n");

					System.out.println("Enter the number of a non-chain you want to check out...-1 to exit");

					int i;
					while((i = keyboard.nextInt()) != -1) {
						if(i >= 1 && i <= nonChains.size()) {
							UnchainedRestaurant ucr = nonChains.get(i-1);
							System.out.println(ucr.toString());
						} else {
							System.out.println("Please try again");
						}
					}


					System.out.println("Thanks for stopping by");
					System.out.println("Brought to you by Foursquare, Yelp, and Google Places!");
					System.out.println("Stay Unchained ;) - Jason");
				} catch (FileNotFoundException e) {
					System.err.println("File Not Found Exception");
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		});
		thread.start();
		try {
			thread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

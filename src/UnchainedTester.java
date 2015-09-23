import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import fi.foyt.foursquare.api.FoursquareApiException;

public class UnchainedTester {
	public static final String DEAFULT_LL = "34.0619825,-83.9833599";

	public static void main(String[] args) {

		//JAVA SAMPLE 
		Scanner keyboard = new Scanner(System.in);
		System.out.println("Enter lat,lng or maps query");
		final String ll = keyboard.nextLine();
		//		keyboard.close();

		Thread thread = new Thread(new Runnable() {
			UnchainedAPI unchainedApi = new UnchainedAPI();
			String query = ll;
			@Override
			public void run() {
				if(!query.matches("%f,%f")) { 
					query = Util.getLatLngFromMapsQuery(query);
				}
				try {
					ArrayList<UnchainedRestaurant> nonChains = unchainedApi.getUnchainedRestaurants(query);
					for(UnchainedRestaurant v : nonChains) {
						System.out.println(nonChains.indexOf(v) + 1 + ". " + v.getName());
					}
					System.out.println("\nWe found " + nonChains.size() + " non-chains\n");

					System.out.println("Enter the number of a non-chain you want to check out...-1 to exit");

					int i;
					while((i = keyboard.nextInt()) != -1) {
						if(i > 1 && i <= nonChains.size()) {
							UnchainedRestaurant ucr = nonChains.get(i-1);
							System.out.println(ucr.toString());
						} else {
							System.out.println("Please try again");
						}
					}



					System.out.println("Brought to you by Foursquare and Yelp!");
					System.out.println("Stay Unchained ;) - Jason");
				} catch (FileNotFoundException e) {
					System.err.println("File Not Found Exception");
					e.printStackTrace();
				} catch (FoursquareApiException e) {
					System.err.println("Foursquare API Exception");
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

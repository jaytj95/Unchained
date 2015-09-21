import fi.foyt.foursquare.api.entities.CompactVenue;

public class Unchained4SQRestaurant extends UnchainedRestaurant {

	public Unchained4SQRestaurant(CompactVenue venue) {
		super(venue.getName(), venue.getLocation().getAddress(), venue.getCanonicalUrl(), venue.getRating());

		String addr = venue.getLocation().getAddress() + ", " + venue.getLocation().getCity() + ", " 
				+ venue.getLocation().getState();
		this.setAddress(addr);
		// TODO Auto-generated constructor stub
	}

}

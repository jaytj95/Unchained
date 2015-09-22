public class UnchainedRestaurant {
	String name, address, website;
	Double rating;

	public UnchainedRestaurant(String name, String address, String website, Double rating) {
		setName(name);
		setAddress(address);
		setWebsite(website);
		setRating(rating);
	}

	@Override
	public String toString() {
		String s = "Restaurant Info for: " + getName() + "\n";

		s += getAddress() + "\n";
		s += getWebsite() + "\n";
		
		if(getRating() == -1) 
			s+= "No rating data for this venue";
		else
			s += getRating() + "/5\n";
		return s;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		if(address != null)
			this.address = address;
		else this.address = "No address data for this venue";
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		if(website != null) 
			this.website = website;
		else
			this.website = "Google Search: " + String.format("http://www.google.com/search?q=%s+%s",
					getName().replace(' ', '+'), getAddress().replace(' ', '+'));
	}


	public Double getRating() {
		return rating;
	}

	public void setRating(Double rating) {
		this.rating = rating;
	}


	public boolean equals(UnchainedRestaurant r) {
		if(this.getName().equals(r.getName())) {
			return true;
		} else return false;
	}




}

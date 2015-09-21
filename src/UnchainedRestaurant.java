public class UnchainedRestaurant {
	String name, address, website;
	Double rating;

	public UnchainedRestaurant(String name, String address, String website, Double rating) {
		this.name = name;
		this.address = address;
		this.website = website;
		this.rating = rating;
	}
	
	@Override
	public String toString() {
		String s = "Restaurant Info for: " + getName() + "\n";
		
		if(getAddress() != null) {
			s += getAddress() + "\n";
		} else {
			s += "No Address data for this venue...\n";
		}
		
		if(getWebsite() != null) {
			s += getWebsite() + "\n";
		} else {
			s += "Google Search: " + String.format("http://www.google.com/search?q=%s\n", getName().replace(' ', '+')) ;
		}
		
		if(getRating() != null) {
			s += getRating() + "/5\n";
		} else {
			s += "No rating info for this venue\n";
		}
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
		this.address = address;
	}

	public String getWebsite() {
		return website;
	}

	public void setWebsite(String website) {
		this.website = website;
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

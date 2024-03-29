package net.andli.foodbot.modules;

import net.andli.foodbot.FoodTime;
import net.andli.foodbot.FoodUser;

public class Departure {
	private FoodTime time;
	private String place;
	private FoodUser creator;
	
	public Departure(FoodTime time, String place, FoodUser creator) {
		this.time = time;
		this.place = place.trim();
		this.creator = creator; 
	}
	
	public String toString() {
		return " <" + this.place + " " + this.time.toString() + ">";
	}

	public FoodUser getCreator() {
		return creator;
	}
	
	@Override
	public boolean equals(Object compSuggestion) {
		boolean retVal = false;
		Departure d = (Departure)compSuggestion;
		if (time.equals(d.time) && 
				place.equals(d.place)) {
			retVal = true;
		}
		return retVal;
	}
}

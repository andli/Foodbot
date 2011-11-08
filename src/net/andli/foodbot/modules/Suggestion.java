package net.andli.foodbot.modules;

import java.util.ArrayList;
import java.util.Date;

import net.andli.foodbot.FoodTime;
import net.andli.foodbot.FoodUser;


public class Suggestion {
	private FoodTime lunchTime;
	private Date createdTime;
	private String foodPlace;
	private ArrayList<FoodUser> voters;
	private ArrayList<FoodUser> perferrers;
	private ArrayList<Departure> departures;
	private String creator;
	
	public ArrayList<Departure> getDepartures() {
		return departures;
	}

	public Suggestion(FoodUser voter, FoodTime time, String placeToEat) {
		lunchTime = time;
		departures = new ArrayList<Departure>();
		voters = new ArrayList<FoodUser>();
		perferrers = new ArrayList<FoodUser>();
		voters.add(voter);
		createdTime = new Date();
		foodPlace = placeToEat.trim();
		this.creator = voter.getNick();
	}
	
	public FoodTime getLunchTime() {
		return lunchTime;
	}
	
	public String getFoodPlace() {
		return foodPlace;
	}

	public Date getCreatedTime() {
		return createdTime;
	}

	public ArrayList<FoodUser> getVoters() {
		return voters;
	}

	public ArrayList<FoodUser> getPerferrers() {
		return perferrers;
	}

	public void addVoter(FoodUser name) {
		voters.add(name);
	}
	
	public void removeVoter(FoodUser name) {
		voters.remove(name);
	}
	
	public void addPreferrer(FoodUser name) {
		perferrers.add(name);
	}
	
	public void removePreferrer(FoodUser name) {
		perferrers.remove(name);
	}
	
	public void addDeparture(Departure dep) {
		this.departures.add(dep);
	}

	@Override
	public boolean equals(Object compSuggestion) {
		boolean retVal = false;
		Suggestion s = (Suggestion)compSuggestion;
		if (lunchTime.equals(s.lunchTime) && 
				foodPlace.equals(s.foodPlace)) {
			retVal = true;
		}
		return retVal;
	}

	public String getCreator() {
		return creator;
	}
	
	@Override
	public String toString() {
		String retString = String.format("%s @ %s - %s votes; %s",
				this.getLunchTime().toString(),
				this.getFoodPlace(),
				this.getVoters().size(),
				LunchCoord.printNameList(this.getVoters(), this.getPerferrers()));
		for (Departure d : departures) {
			retString = retString.concat(d.toString());
		}
		return retString; 
	}
	
//	private String getDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        Date date = new Date();
//        return dateFormat.format(date);
//	}
}

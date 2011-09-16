package net.andli.foodbot.modules;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Vector;

import net.andli.foodbot.FoodBot;
import net.andli.foodbot.FoodTime;
import net.andli.foodbot.FoodUser;
import net.andli.foodbot.IChannelHandler;


public class LunchCoord implements ActionListener {

	private ArrayList<Suggestion> suggestions;
	private ArrayList<FoodUser> nolunchers;
	private IChannelHandler broadcaster;
	private ArrayList<ActionListener> actionListeners;

	public LunchCoord(IChannelHandler bc) {
		actionListeners = new ArrayList<ActionListener>();
		suggestions = new ArrayList<Suggestion>();
		nolunchers = new ArrayList<FoodUser>();
		broadcaster = bc;
	}

	public void printSuggestions(String lunchChannel) {
		if (nolunchers.size() > 0) {
			broadcaster.printToChannel(lunchChannel, "Busy today: " + printNameList(nolunchers, null));
		}
		if (suggestions.size() > 0) {
			for (Suggestion s : suggestions) {
				if (s.getVoters().size() > 0) {
					broadcaster.printToChannel(lunchChannel, printSuggestion(s));
				}
			}
		}
		else {
			broadcaster.printToChannel(lunchChannel, "No suggestions yet. I !suggest you make one...");
		}
	}

	private String printSuggestion(Suggestion s) {
		return suggestions.indexOf(s) + ") " + s.toString();
	}

	/**
	 * For easy printing of FoodUser arrays
	 * @param users FoodUser arrayList
	 * @param prefs FoodUser arrayList with people who prefer this suggestion
	 * @return formatted string
	 */
	public static String printNameList(ArrayList<FoodUser> users, ArrayList<FoodUser> prefs) {
		String retString = "[";

		for (FoodUser fu : users) {
			retString = retString.concat(fu.getNick());
			if (fu.getNote() != null) {
				retString = retString.concat(" (" + fu.getNote() + ")");
			}
			if (prefs != null && prefs.contains(fu)) {
				retString = retString.concat("*");
			}
			if (users.indexOf(fu) < users.size() - 1) {
				retString = retString.concat(", ");
			}
		}

		return retString + "]";
	}

	public void addSuggestion(String channel, FoodUser user, String[] args) {
		Suggestion newSuggestion;
		try {
			String foodPlace = "";
			FoodTime time = null;

			if (FoodTime.isFoodTime(args[0])) {
				time = new FoodTime(args[0]);
				for (int i = 1; i < args.length; i++) {
					foodPlace = foodPlace.concat(args[i] + " ");
				}
			}
			else if (FoodTime.isFoodTime(args[args.length - 1])) {
				time = new FoodTime(args[args.length - 1]);
				for (int i = 0; i < args.length - 1; i++) {
					foodPlace = foodPlace.concat(args[i] + " ");
				}
			}

			newSuggestion = new Suggestion(user, time, foodPlace);

			// Don't create duplicate suggestions
			if (!suggestions.contains(newSuggestion)) {
				if (nolunchers.contains(user)) {
					nolunchers.remove(user);
					user.setNote(null);
				}
				suggestions.add(newSuggestion);
				broadcaster.printToChannel(channel, 
						user.getNick() + " suggested " + 
						printSuggestion(suggestions.get(suggestions.size() - 1)));
			}
			else {
				int where = suggestions.indexOf(newSuggestion);
				broadcaster.errorToChannel(channel, "Suggestion exists (number " + where + ").");
			}

		} catch (Exception e) {
			broadcaster.errorToChannel(channel, user.getNick() + ": " + e.getMessage());
		}

	}

	public void addDeparture(String channel, FoodUser user, String[] args) {
		try {
			int suggNo = Integer.parseInt(args[0]);
			verifySuggestion(suggNo);
			// Parse suggestion
			Suggestion s = suggestions.get(suggNo);
			FoodTime t = null;
			String p = "";

			if (FoodTime.isFoodTime(args[1])) {
				// Parse time
				t = new FoodTime(args[1]);
				// Parse place
				for (int i = 2; i < args.length; i++) {
					p = p.concat(args[i] + " ");
				}
			}
			else if (FoodTime.isFoodTime(args[args.length - 1])) {
				// Parse time
				t = new FoodTime(args[args.length - 1]);
				// Parse place
				for (int i = 1; i < args.length - 1; i++) {
					p = p.concat(args[i] + " ");
				}
			}

			if (t != null) {
				if (p.length() > 0) {
					// Add departure
					Departure d = new Departure(t, p, user);
					if (!s.getDepartures().contains(d)) {
						s.addDeparture(d);

						broadcaster.printToChannel(channel, printSuggestion(s));
					}
					else {
						broadcaster.errorToChannel(channel, "No duplicates.");
					}
				}
				else {
					broadcaster.errorToChannel(channel, "Depart must have text.");
				}
			}
			else {
				broadcaster.errorToChannel(channel, "Invalid time.");
			}

		} catch (Exception e) {
			broadcaster.errorToChannel(channel, e.getMessage());
		}
	}

	public void removeDepartures(String channel, FoodUser user, String[] args) {
		try {
			int suggNo = Integer.parseInt(args[0]);
			verifySuggestion(suggNo);

			Suggestion s = suggestions.get(suggNo);

			ArrayList<Departure> deps = s.getDepartures();
			Vector<Departure> remDeps = new Vector<Departure>();
			for (Departure d : deps) {
				if (d.getCreator().equals(user)) {
					remDeps.add(d);
				}
			}
			if (remDeps.size() > 0) {
				deps.removeAll(remDeps);
				broadcaster.printToChannel(channel, "Ok, " + user.getNick() + ". Your departs removed for suggestion " + suggNo + ".");
			}
			else {
				broadcaster.printToChannel(channel, "No departs for that suggestion and user.");
			}
		} catch (Exception e) {
			broadcaster.errorToChannel(channel, e.getMessage());
		}
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

	}

	public void addActionListener(FoodBot foodBot) {
		this.actionListeners.add(foodBot);
	}

	public void reset() {
		suggestions.clear();
		for (FoodUser fu : nolunchers) {
			fu.setNote(null);
		}
		nolunchers.clear();
	}

	public void vote(String channel, FoodUser user, int suggNo) {
		try {
			nolunchers.remove(user);
			user.setNote(null);
			if (suggestions.get(suggNo).getVoters().contains(user)) {
				broadcaster.printToChannel(channel, "Already voted for that.");
			}
			else {
				suggestions.get(suggNo).addVoter(user);
				broadcaster.printToChannel(channel, user.getNick() + " voted for " + 
						suggestions.get(suggNo).toString());			
			}
		} catch (Exception e) {
			broadcaster.errorToChannel(channel, e.getMessage());
		}
	}

	private void verifySuggestion(int suggNo) throws Exception {
		if (suggestions.get(suggNo).getVoters().size() == 0)
			throw new Exception("Not an active suggestion.");

	}

	public void unvote(String channel, FoodUser user, int suggNo) {
		try {
			verifySuggestion(suggNo);
			Suggestion s = suggestions.get(suggNo);
			s.removeVoter(user);
			// Also remove preferred if this was preferred
			s.removePreferrer(user);
			broadcaster.printToChannel(channel, "Ok, " + user.getNick());

			//			if (s.getVoters().size() == 0) {
			//				suggestions.remove(s);
			//			}
		} catch (Exception e) {
			broadcaster.errorToChannel(channel, e.getMessage());
		}
	}

	public void prefer(String channel, FoodUser user, int suggNo) {
		try {
			// If user has voted for suggestion
			boolean hasVoted = false;
			for (Suggestion s : suggestions) {
				if (s.getVoters().contains(user)) {
					hasVoted = true;
					if (s.getPerferrers().contains(user)) {
						s.removePreferrer(user);					
					}
				}
			}

			if (hasVoted) {
				// Set that user prefers suggestion
				suggestions.get(suggNo).addPreferrer(user);
				// Say <name> prefers <sugg no>
				broadcaster.printToChannel(channel, user.getNick() + " prefers " + suggNo + ").");
			}
			else {
				// Say no votes were cast
				vote(channel, user, suggNo);
				prefer(channel, user, suggNo);
				//broadcaster.errorToChannel(channel, "You have to vote for it to prefer it.");	
			}
		} catch (Exception e) {
			broadcaster.errorToChannel(channel, e.getMessage());
		}
	}

	public void unprefer(String channel, FoodUser user, String arguments) {
		for (Suggestion s : suggestions) {
			if (s.getPerferrers().contains(user)) {
				s.removePreferrer(user);
				broadcaster.printToChannel(channel, "Cleared preferred suggestion for " + user.getNick() + ".");
			}
		}
	}

	public void setBusy(String channel, FoodUser user, String reason) {
		// Remove user from all suggestions
		for (Suggestion s : suggestions) {
			if (s.getVoters().contains(user) && s.getVoters().size() - 1 == 0) {
				suggestions.remove(s);				
			}
			else if (s.getVoters().contains(user)) {
				s.removeVoter(user);
			}
		}

		// Set as busy
		user.setNote(reason);
		if (!nolunchers.contains(user)) {
			nolunchers.add(user);

			String appendReason = "";
			if (user.getNote() != null) {
				appendReason = " (" + user.getNote() + ")"; 
			}

			broadcaster.printToChannel(channel, user.getNick() + " is busy this lunch" + appendReason + ".");
		}
	}

	/**
	 * Switches out a voter, made for nick changes.
	 * @param oldUser
	 * @param newUser
	 */
	public void changeVoter(FoodUser oldUser, FoodUser newUser) {
		for (Suggestion s : suggestions) {
			if (s.getVoters().contains(oldUser)) {
				int switchIndex = s.getVoters().indexOf(oldUser);
				s.getVoters().set(switchIndex, newUser);
			}
		}
	}

}

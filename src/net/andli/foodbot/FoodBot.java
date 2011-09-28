package net.andli.foodbot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

import javax.swing.Timer;

import net.andli.common.Bugreport;
import net.andli.common.XmlStorageHandler;
import net.andli.foodbot.modules.LunchCoord;
import net.htmlparser.jericho.CharacterReference;
import net.htmlparser.jericho.Element;
import net.htmlparser.jericho.HTMLElementName;
import net.htmlparser.jericho.Source;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;
import org.jibble.pircbot.PircBot;


public class FoodBot
extends PircBot implements ActionListener, IChannelHandler
{
	private static final String FOODUSERS_FILENAME = "foodusers.xml";
	private static final String BUGREPORTS_FILENAME = "bugreports.xml";
	private static final String VERSION = "3.0.14";
	private Calendar lastReset;
	private ArrayList<FoodUser> userList;
	private LunchCoord lunchCoord;
	private String lunchChannel;
	private Timer spamTimer;
	private boolean debugMode;

	public FoodBot(String name, String lunchCh) 
	{
		this.setName(name);
		this.lastReset = Calendar.getInstance();
		spamTimer = new Timer(5000, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				spamTimer.stop();
			}});

		lunchCoord = new LunchCoord(this);
		lunchCoord.addActionListener(this);

		//		// Try to load saved players
		//		try {
		//			userList = (ArrayList<FoodUser>) XmlStorageHandler.readCollection(FOODUSERS_FILENAME);
		//			for (FoodUser fu : userList) {
		//				fu.addActionListener(this);
		//			}
		//		} catch (Exception e) {
		//			userList = new ArrayList<FoodUser>();
		//		}
		userList = new ArrayList<FoodUser>();

		lunchChannel = lunchCh;
	}

	@Override
	protected void onPrivateMessage(String sender, String login,
			String hostname, String message) {
		//Check if user is authed, then run any queued commands? Bad.
		//		// >> :servercentral.il.us.quakenet.org 330 andli1 andli1 andli :is authed as
		//		if (message.startsWith("330")) {
		//			String[] args = message.split(" ");
		//
		//			String authName = args[args.length - 1];
		//			if (authQueue.containsKey(authName)) {
		//				String nickName = authQueue.get(authName);
		//				sendMessage(lunchChannel, "FoodUser " + nickName + 
		//						" is now connected to auth " + authName + ".");
		//				//Merge users?
		//			}
		//		}

		super.onPrivateMessage(sender, login, hostname, message);
	}

	public void onMessage(String channel,
			String sender,
			String login,
			String hostname,
			String msg)
	{
		FoodUser user = getOrCreateFoodUser(sender);
		String command;
		String arguments;

		command = parseCommand(msg);
		// Allow commands without arguments
		if (command != null && msg.length() >= command.length() + 2) {
			arguments = msg.substring(command.length() + 2);
		}
		else {
			arguments = null;
		}

		if (command != null) {
			String[] args;
			CommandIndex cmd = CommandIndex.toCommand(command);

			switch (cmd) {
			case LUNCH:
				lunchCoord.printSuggestions(lunchChannel);

				break;
			case VOTE:
				args = verifySyntax(channel, arguments, 1);
				if (args != null) {
					lunchCoord.vote(channel, user, Integer.parseInt(args[0]));
				}

				break;
			case UNVOTE:
				args = verifySyntax(channel, arguments, 1);
				if (args != null) {
					lunchCoord.unvote(channel, user, Integer.parseInt(args[0]));
				}
				break;
			case PREFER:
				args = verifySyntax(channel, arguments, 1);
				if (args != null) {
					lunchCoord.prefer(channel, user, Integer.parseInt(args[0]));
				}
				break;
			case UNPREFER:
				lunchCoord.unprefer(channel, user, arguments);
				break;
			case NOLUNCH:
				lunchCoord.setBusy(channel, user, arguments);
				break;
			case BUSY:
				lunchCoord.setBusy(channel, user, arguments);
				break;
			case SUGGEST:
				if (!user.isSilenced()) {
					args = arguments.split(" ");
					lunchCoord.addSuggestion(channel, user, args);
				}
				break;
			case DEPART:
				if (!user.isSilenced()) {
					args = arguments.split(" ");
					lunchCoord.addDeparture(channel, user, args);
				}
				break;
			case CLEARDEPARTS:
				if (!user.isSilenced()) {
					args = arguments.split(" ");
					lunchCoord.removeDepartures(channel, user, args);
				}
				break;
			case MENULIST:
				if (!user.isSilenced()) {
					String urlString = "http://preston.se/dagens.html";
					if (arguments == null) {
						parseHTMLrestaurantIds(channel, arguments, urlString);
					}
					else {
						errorMessage(channel, "Wrong syntax.");
					}
				}
				break;
			case MENU:
				if (!user.isSilenced()) {
					String urlString = "http://preston.se/dagens.html";
					if (arguments != null) {
						parseHTMLmenu(channel, arguments, urlString);
					}
					else {
						sendMessage(channel, urlString);
					}
				}
				break;
			case RESET:
				if (user.isAdmin() && !user.isSilenced()) {
					reset();
					sendMessage(channel, sender + " did the big bad admin reset.");
				}
				else {
					sendMessage(channel, "You need to be an admin to do that.");
				}
				break;
			case PROMOTE:
				if (user.isAdmin() && !user.isSilenced()) {
					args = verifySyntax(channel, arguments, 1);
					if (args != null) {
						FoodUser target = getOrCreateFoodUser(args[0]);
						target.promote();
						sendMessage(channel, target.getNick() + " has been promoted to admin!");
					}
				}
				else {
					sendMessage(channel, "You need to be an admin to do that.");
				}
				break;
			case BUG:
				if (!user.isSilenced()) {
					reportBug(channel, sender, arguments);
				}
				break;
			case HELP:
				presentHelpText(channel); 
				break;
			default:
				// Not recognized
				break;
			}

		}
		else if (msg.contains("lunch?")) {
			if (channel.equals(lunchChannel)) {
				lunchCoord.printSuggestions(lunchChannel);
			}
		}
	}

	/**
	 * Prints a list of the id tags for all restaurants.
	 * @param channel
	 * @param arguments
	 * @param urlString
	 */
	private void parseHTMLrestaurantIds(String channel, String arguments, String urlString) {
		if (!spamTimer.isRunning()) {
			// parse specific menu item

			Source source;
			try {
				source = new Source(new URL(urlString));
				source.setLogger(null);

				ArrayList<Element> divTags = (ArrayList<Element>) source.getAllElements(HTMLElementName.DIV);
				ArrayList<Element> placeTags = new ArrayList<Element>();

				for (Element ed : divTags) {
					if (ed.getAttributeValue("id").startsWith("place")) { // ex. <div id="place3">
						placeTags.add(ed);
					}
				}

				System.out.println("Found " + placeTags.size() + " restaurants, searching them...");

				String restString = "";
				for (Element ed : placeTags) {
					Source subSource = new Source(ed);
					subSource.setLogger(null);

					ArrayList<Element> aTags = (ArrayList<Element>) subSource.getAllElements(HTMLElementName.A);
					for (Element ea : aTags) {
						if (ea.getAttributeValue("id") != null) { // Look at id tag
							restString = restString.concat(ea.getAttributeValue("id") + ", ");
						}
					}
				}
				if (restString.length() > 0) {
					restString = restString.substring(0, restString.length() - 2);
					sendMessage(channel, "List of restaurant id tags for the !menu command:");
					sendMessage(channel, CharacterReference.decode(restString));
				}
				else {
					sendMessage(channel, "No restaurants found.");
				}
			}
			catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace(); 
			}
		}
	}


	private void parseHTMLmenu(String channel, String arguments, String urlString) {
		if (!spamTimer.isRunning()) {
			// parse specific menu item

			Source source;
			try {
				source = new Source(new URL(urlString));
				source.setLogger(null);
				Element targetDiv = null;

				ArrayList<Element> divTags = (ArrayList<Element>) source.getAllElements(HTMLElementName.DIV);
				ArrayList<Element> placeTags = new ArrayList<Element>();

				for (Element ed : divTags) {
					if (ed.getAttributeValue("id").startsWith("place")) { // ex. <div id="place3">
						placeTags.add(ed);
					}
				}

				//System.out.println("Found " + placeTags.size() + " restaurants, searching them...");

				for (Element ed : placeTags) {
					Source subSource = new Source(ed);
					subSource.setLogger(null);

					ArrayList<Element> aTags = (ArrayList<Element>) subSource.getAllElements(HTMLElementName.A);
					for (Element ea : aTags) {
						if (ea.getAttributeValue("id") != null) { // Look at id tag

							//System.out.println("id matching: " + ea.getAttributeValue("id") + " == " + arguments);
							// <a id="cominn" accesskey="2" /> 
							if (ea.getAttributeValue("id").equalsIgnoreCase(arguments.trim())) { // New code, remove if buggy!
								//System.out.println("--> matched in id tag!");
								targetDiv = ed;
							}
						}
						else { // Look at content

							//System.out.println("name matching: " + ea.getContent().toString() + " == " + arguments);
							// <a href="http://www.cominn.nu/veckans-meny.html" accesskey="i" title="Källsidan i HTML">ComInn</a>
							if (ea.getContent().toString().equalsIgnoreCase(arguments.trim())) {
								//System.out.println("--> matched in text!");
								targetDiv = ed;
							}
						}
					}
				}

				if (targetDiv != null) {
					source = new Source(targetDiv);
					source.setLogger(null);

					ArrayList<Element> liTags = (ArrayList<Element>) source.getAllElements(HTMLElementName.LI);
					spamTimer.start();					
					//System.out.println("Printing " + liTags.size() + " menu items...");
					if (liTags.size() > 0) {
					for (Element el : liTags) {
						sendMessage(channel, CharacterReference.decode(el.getContent()));
					}
					}
					else {
						sendMessage(channel, "Restaurant has no menu items.");
					}
				}
				else {
					sendMessage(channel, "Could not find restaurant '" + arguments + "'.");
				}

			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			sendMessage(channel, "Wait a few seconds.");
		}
	}

	private String[] verifySyntax(String destChannel, String arguments, int numberOfArgs) {
		if (arguments != null) {
			String[] retArgs =  arguments.split(" ");
			if (retArgs.length != numberOfArgs) {
				errorMessage(destChannel, "Wrong syntax.");
				return null;
			}
			else {
				return retArgs;
			}
		}
		else {
			return null;
		}
	}

	private String parseCommand(String message) {
		if (message.startsWith("!")) {
			String[] retStringArray = message.split(" ");
			retStringArray[0] = retStringArray[0].substring(1);
			return retStringArray[0];
		}
		return null;
	}

	private void presentHelpText(String channel) {
		sendMessage(channel, "My commands and bugs (v " + FoodBot.VERSION + ") can be found at:");
		sendMessage(channel, "http://andli.net/foodbot");
	}

	/**
	 * Report a bug to the bug storage service.
	 * @param channel
	 * @param sender
	 * @param message
	 */
	@SuppressWarnings("unchecked")
	private void reportBug(String channel, String sender, String message)
	{
		Bugreport newBug = new Bugreport();
		newBug.setCreator(sender);
		newBug.setBugreport(message);

		ArrayList<Bugreport> bugsList = new ArrayList<Bugreport>();
		try {
			bugsList = (ArrayList<Bugreport>) XmlStorageHandler.readCollection(BUGREPORTS_FILENAME);
		} catch (Exception e) {
			errorMessage(channel, e.getMessage());
		}
		bugsList.add(newBug);

		try {
			XmlStorageHandler.writeCollection(bugsList, BUGREPORTS_FILENAME);
			sendMessage(channel, "Thanks!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void reset()
	{	
		lunchCoord.reset();
		this.lastReset = Calendar.getInstance();
		forceSaveDbData();
		printToDefaultChannel("Suggestions have been reset.");
	}

	@Override
	protected void onNickChange(String oldNick, String login, String hostname,
			String newNick) {
		FoodUser oldUser = getOrCreateFoodUser(oldNick);
		FoodUser newUser = getOrCreateFoodUser(newNick);
		lunchCoord.changeVoter(oldUser, newUser);
	}

	public void onKick(String channel,
			String kickerNick,
			String login,
			String hostname,
			String recipientNick,
			String reason)
	{
		if (recipientNick.equalsIgnoreCase(getNick())) {
			joinChannel(channel);
		}
	}

	public void onDisconnect()
	{
		while (!isConnected()) {
			try {
				reconnect();
			} catch (Exception e) {
				// Couldn't reconnect.
				// Pause for a short while before retrying?
				System.out.println(e.getMessage());
				try {
					Thread.sleep(15000);
					reconnect();
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				} catch (NickAlreadyInUseException ne) {
					e.printStackTrace();
				} catch (IOException ie) {
					e.printStackTrace();
				} catch (IrcException irce) {
					e.printStackTrace();
				}
			}
		}
	}

	public void onServerPing(String response) {
		super.onServerPing(response);

		Calendar thresholdDate = Calendar.getInstance();
		thresholdDate.set(Calendar.HOUR_OF_DAY, 0);
		thresholdDate.set(Calendar.MINUTE, 0);

		// If a reset has already been done today, then don't
		if (lastReset.before(thresholdDate)) {
			reset();
		}
	}

	private void errorMessage(String target, String message) {
		sendMessage(target, "ERROR: " + message);
	}

	@Override
	public void actionPerformed(ActionEvent e) {


	}

	@Override
	public void printToChannel(String targetChannel, String message) {
		sendMessage(targetChannel, message);
	}

	@Override
	public void errorToChannel(String targetChannel, String message) {
		errorMessage(targetChannel, message);
	}

	public void printToDefaultChannel(String message) {
		sendMessage(lunchChannel, message);
	}

	@Override
	public ArrayList<FoodUser> getUserList() {

		return userList;
	}

	public String whois(String target) {
		// A simple whois
		FoodUser fu = new FoodUser();
		fu.setNick(target);
		int index = userList.indexOf(fu);
		return userList.get(index).toString();
	}

	public boolean userNickExists(String target) {
		boolean isTheSame = false;
		FoodUser fu = new FoodUser();
		fu.setNick(target);
		if (userList.contains(fu)) {
			isTheSame = true;
		}

		return isTheSame;
	}

	public FoodUser getOrCreateFoodUser(String nick) {
		FoodUser fu = new FoodUser();
		fu.setNick(nick);
		if (userList.contains(fu)) {
			int index = userList.indexOf(fu);
			fu = userList.get(index);
		}
		else {
			fu = createUser(nick);
		}
		return fu;
	}

	private FoodUser createUser(String nick) {
		FoodUser user = new FoodUser();
		user.setNick(nick);
		user.addActionListener(this);
		userList.add(user);
		return user;
	}

	public void forceSaveDbData() {
		try {
			XmlStorageHandler.writeCollection(userList, FOODUSERS_FILENAME);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}

	public boolean isDebugMode() {
		return debugMode;
	}
}

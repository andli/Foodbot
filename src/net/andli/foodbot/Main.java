/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package net.andli.foodbot;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

/**
 *
 * @author elimand
 */
public class Main {

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		FoodBot bot;
		InputManager im;

		String botName;
		String lunchChannel;
		String encodingString;

		if (args.length == 3) {
			botName = args[0];
			lunchChannel = args[1];
			encodingString = args[2];
			
			bot = new FoodBot(botName, lunchChannel);
			bot.setAutoNickChange(true);
			bot.setMessageDelay(1100);
			
			// Enable debugging output.
			bot.setVerbose(false);

			try {
				bot.setEncoding(encodingString);
			} catch (UnsupportedEncodingException e) {
				consoleMsg("ERROR: Cannot set encoding." + e.getMessage());
			}
			try {
				//      consoleMsg("Setting proxy details...");
				//      System.setProperty("http.proxyHost", "www-proxy.ericsson.se");
				//      System.setProperty("http.proxyPort", "8080");
				consoleMsg("Encoding: " + bot.getEncoding());

				consoleMsg("Connecting to IRC server...");
				bot.connect("se.quakenet.org");
				consoleMsg("Connected.");

				consoleMsg("Joining channel " + lunchChannel + "...");
				bot.joinChannel(lunchChannel);
				consoleMsg("Joined.");

				im = new InputManager(bot);
				Thread t = new Thread(im);
				t.start();

			} catch (NickAlreadyInUseException e) {
				errorMsg(e);

			} catch (IOException e) {
				errorMsg(e);

			} catch (IrcException e) {
				errorMsg(e);

			}

		}
		else {
			System.out.println("Must start with <botname> <channel> <encoding>. " +
					"Example: sudo screen -S fb java -jar foodbot.jar foodbot \"#mjardevi\" \"utf-8\"");
		}

		
	}

	private static void consoleMsg(String text)
	{
		System.out.println("> " + text);
	}

	private static void errorMsg(Exception e)
	{
		System.out.println("ERROR: " + e.getMessage());
	}
}


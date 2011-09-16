package net.andli.foodbot;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jibble.pircbot.IrcException;
import org.jibble.pircbot.NickAlreadyInUseException;

public class InputManager implements Runnable {

	private FoodBot bot;
	private BufferedReader bin;

	public InputManager(FoodBot target) {
		bot = target;
	}

	@Override
	public void run() {

		bin = new BufferedReader(new InputStreamReader(System.in));
		String line;
		try {
			while ((line = bin.readLine()) != null) {
				String cmd = parseCommand(line);

				if (cmd != null) {
					if (cmd.equals("say")) {
						bot.printToDefaultChannel(parseMessage(line, cmd));
					}
					else if (cmd.equals("reset")) {
						consoleMsg("Reset...");
						bot.reset();
					}
					else if (cmd.equals("reconnect")) {
						consoleMsg("Disconnecting...");
						bot.disconnect();
						consoleMsg("Saving data...");
						bot.forceSaveDbData();
						consoleMsg("Reconnecting...");
						bot.reconnect();
					}
					else if (cmd.equals("quit")) {
						consoleMsg("Quitting...");
						bot.disconnect();
						bot.forceSaveDbData();
						System.exit(0);
					}
					else if (cmd.equals("debug")) {
						if (bot.isDebugMode()) {
							bot.setDebugMode(false);
							consoleMsg("Debug mode OFF");
						}
						else {
							bot.setDebugMode(true);
							consoleMsg("Debug mode ON");
						}
					}
					else {
						consoleMsg("Unknown command.");
					}
				}
				line = null; 
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NickAlreadyInUseException e) {
			e.printStackTrace();
		} catch (IrcException e) {
			e.printStackTrace();
		}
	}

	private String parseCommand(String message) {
		if (message.startsWith("/")) {
			String[] retStringArray = message.split(" ");
			retStringArray[0] = retStringArray[0].substring(1);
			return retStringArray[0];
		}
		return null;
	}

	private String parseMessage(String message, String command) {
		return message.substring(command.length() + 2); // +2 for +1 and " "
	}

	private static void consoleMsg(String text)
	{
		System.out.println("> " + text);
	}
}

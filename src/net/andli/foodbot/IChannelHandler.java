package net.andli.foodbot;

import java.util.ArrayList;

public interface IChannelHandler {
	public void printToChannel(String targetChannel, String message);
	public void errorToChannel(String targetChannel, String message);
	public ArrayList<FoodUser> getUserList();
}

package net.andli.foodbot;

import java.awt.event.ActionEvent;

public class FoodUserEvent extends ActionEvent {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2079164086239570474L;
	public static final int LEVELUP = FoodUserEvent.ACTION_FIRST + 1;
	public static final int GAINEDXP = FoodUserEvent.ACTION_FIRST + 2;
	public static final int EQUIPPEDITEM = FoodUserEvent.ACTION_FIRST + 3;
	public static final int DROPPEDITEM = FoodUserEvent.ACTION_FIRST + 4;
	public static final int PVPFIGHTWON = FoodUserEvent.ACTION_FIRST + 5;
	public static final int FIGHTCANCELED = FoodUserEvent.ACTION_FIRST + 6;
	public static final int PUNISHOVER = FoodUserEvent.ACTION_FIRST + 7;
	public static final int PICKEDUPITEM = FoodUserEvent.ACTION_FIRST + 8;
	public static final int FIGHTALLOWED = FoodUserEvent.ACTION_FIRST + 9;
	
	public FoodUserEvent(FoodUser source, int id, String command) {
		super(source, id, command);
	}

}


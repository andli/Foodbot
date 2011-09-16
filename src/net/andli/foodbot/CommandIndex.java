package net.andli.foodbot;

public enum CommandIndex {
	BUG,
	BUSY,
	CLEARDEPARTS,
	DEPART,
	HELP,
	LUNCH,
	MENU,
	MENULIST,
	NOLUNCH,
	NOVALUE, 
	PREFER,
	PROMOTE,
	RESET,
	SUGGEST,
	UNPREFER,
	UNVOTE,
	VOTE;
	
	public static CommandIndex toCommand(String str)
    {
        try {
            return CommandIndex.valueOf(str.toUpperCase());
        } 
        catch (Exception ex) {
            return NOVALUE;
        }
    }   
}

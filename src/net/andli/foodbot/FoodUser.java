package net.andli.foodbot;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class FoodUser implements ActionListener {

	private String nick;
	private String authName;
	private boolean isAdmin;
	private boolean isSilenced;
	private Vector<ActionListener> actionListeners;
	private String note;

	public FoodUser() {
		actionListeners = new Vector<ActionListener>();
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}
	
	@Override
	public boolean equals(Object o) {
		boolean isTheSame = false;
		if ( !(o instanceof FoodUser) ) return false;
		else {
			FoodUser fu = (FoodUser)o;
			if (this.nick.equals(fu.nick)) {
				isTheSame = true;
			}
		}
		return isTheSame;
	}

	@Override
	public String toString() {
		return this.nick;
	}

	public void addActionListener(FoodBot fb) {
		this.actionListeners.add(fb);
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setSilenced(boolean isSilenced) {
		this.isSilenced = isSilenced;
	}

	public boolean isSilenced() {
		return isSilenced;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
//		String cmd = arg0.getActionCommand();
//		if (cmd.equals("PUNISHOVER")) {
//			if (this.actionListeners.size() > 0) {
//				this.isSilenced = false;
//				FoodUserEvent fue = new FoodUserEvent(this, FoodUserEvent.PUNISHOVER,
//						this.getNick() + "'s punishment is over.");
//				for (ActionListener al : actionListeners) {
//					al.actionPerformed(fue);
//				}
//			}
//		}
//		if (cmd.equals("FIGHTALLOWEDRESET")) {
//			if (this.actionListeners.size() > 0) {
//				this.pvpCooldown = false;
//				FoodUserEvent fue = new FoodUserEvent(this, FoodUserEvent.FIGHTALLOWED,
//						this.getNick() + "'s pvp cooldown has expired.");
//				for (ActionListener al : actionListeners) {
//					al.actionPerformed(fue);
//				}
//			}
//		}
	}

	public void promote() {
		this.isAdmin = true;
	}

	public void demote() {
		this.isAdmin = false;
	}

	public void setAuthName(String authName) {
		this.authName = authName;
	}

	public String getAuthName() {
		return authName;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getNote() {
		return note;
	}

}

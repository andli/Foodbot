package net.andli.common;

import java.util.Date;

/*
 * Bugreport is a bean, ie it has a no-args constructor and public
 * getters and setters for all attributes
 */
public class Bugreport {

    private String creator;
    private long dateFiled;
    private String bugReport;

    public Bugreport() {
        Date d = new Date();
        this.dateFiled = d.getTime();
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public void setDateFiled(long dateFiled) {
		this.dateFiled = dateFiled;
	}

	public long getDateFiled() {
        return dateFiled;
    }

    public String getBugreport() {
        return bugReport;
    }

    public void setBugreport(String bugReport) {
        this.bugReport = bugReport;
    }
    
    @Override
    public String toString() {
    	Date d = new Date(this.dateFiled);
    	String retString = "(" + this.creator + ", " + d.toString() + ") " + this.bugReport;

		return retString;
    }
}


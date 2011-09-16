package net.andli.foodbot;

public class FoodTime {

	private int hour;
	private int minute;

	public FoodTime(int hour, int minute)  {
		this.hour = hour;
		this.minute = minute;
	}

	public FoodTime(String time) throws Exception {
		String[] args = time.split("[:.]", 2);
		int hours = Integer.parseInt(args[0]);
		int minutes = Integer.parseInt(args[1]);
		if (hours >= 0 && hours <= 23 && minutes >= 0 && minutes <= 59) {
			this.hour = hours;
			this.minute = minutes;
		}
		else {
			throw new Exception("Wrong time format");
		}
	}

	@Override
	public String toString() {

		String minuteString = Integer.toString(minute);
		String hourString = Integer.toString(hour);
		if (minute < 10) {
			minuteString = "0" + minuteString;
		}
		if (hour < 10) {
			hourString = "0" + hourString;
		}

		return hourString + ":" + minuteString; 
	}

	@Override
	public boolean equals(Object compTime) {
		boolean retVal = false;
		//TODO: Ugly with the casts!
		if (this.hour == ((FoodTime)compTime).hour && this.minute == ((FoodTime)compTime).minute) {
			retVal = true;
		}
		return retVal;

	}

	public static boolean isFoodTime(String string) {

		try {
			@SuppressWarnings("unused")
			FoodTime ft = new FoodTime(string);
			return true;
		} catch (Exception e) {
			return false;			
		}
	}
}

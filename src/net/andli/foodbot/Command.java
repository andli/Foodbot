package net.andli.foodbot;

public interface Command {
	/**
	 * The code is foreach noun in nouns, execute it
	 * @param nouns
	 */
    public void doThis(String[] nouns);
    /**
     * This clones the object and returns it
     * @return
     */
    public Command factory();
}
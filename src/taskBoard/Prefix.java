package taskBoard;

/**
 * An enum contains the prefix added in front of the option keyword. For example, "-create" or "-task".
 * It is always "-" in this work.
 */

public enum Prefix {
	
	DASH('-');

	private char c;
	Prefix(char c){
		this.c=c;
	}

	/**
	 * @return The prefix character
	 */
	char getPrefixName(){
		return c;
	}

}

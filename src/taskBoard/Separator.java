package taskBoard;

/**
 *  An enum contains the separator between arguments in an user input command line.
 *  It is always BLANK in this work.
 */

public enum Separator {
	BLANK(' ');

	private char c;
	Separator(char c){
		this.c=c;
	}

	/**
	 * @return The actual character of Separator
	 */
	char getSeparatorName(){
		return c;
	}
}

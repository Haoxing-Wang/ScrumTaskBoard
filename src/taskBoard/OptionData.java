package taskBoard;

import java.util.ArrayList;

/**
 * This class creates an option instance to hold all the information of a single option.
 */

public class OptionData {

	private String keyWord;
	private Prefix prefix;
	private Separator separator;
	private Multiplicity multiplicity;
	private boolean hasDataItem;
	private ArrayList<String> matchedDataItem;
	private java.util.regex.Pattern keyWordPattern;
	private int counter = 0;

	/**
	 * Constructor 
	 * @param prefix the prefix, set from Option class
	 * @param keyWord the key word of an option, set in the OptionSet class
	 * @param separator the character that separates each argument, set in the OptionSet class
	 * @param dataValue the boolean value indicating whether or not there is data item after key word, set in the OptionSet class
	 * @param multiplicity the multiplicity of an option, set in the OptionSet class
	 */
	public OptionData(Prefix prefix, String keyWord, Separator separator, boolean dataValue, Multiplicity multiplicity){
		this.prefix=prefix;
		this.keyWord=keyWord;
		this.separator=separator;
		this.hasDataItem=dataValue;
		this.multiplicity=multiplicity;

		if(prefix==null){
			throw new IllegalArgumentException(this.getClass().getName()+" missing prefix '-'");
		}
		if(keyWord==null){
			throw new IllegalArgumentException(this.getClass().getName()+" missing the keyword");
		}
		if(separator==null){
			throw new IllegalArgumentException(this.getClass().getName()+" the separator must be blank");
		}
		if(multiplicity==null){
			throw new IllegalArgumentException(this.getClass().getName()+" the multiplicity cannot be null");
		}

		if (dataValue) { // If there is data item after keyWord
			// Create a dataValues list to store each data item
			matchedDataItem = new ArrayList<String>();
			
			if (separator == Separator.BLANK) { // If the separator is BLANK
				keyWordPattern = java.util.regex.Pattern.compile(prefix.getPrefixName() + keyWord + "$");
			}
		}
		else {	// If data item does not exist
			keyWordPattern = java.util.regex.Pattern.compile(prefix.getPrefixName() + keyWord + "$");
		}
	}

	/**
	 * Get the prefix
	 * @return the retrieved prefix character 
	 */
	public Prefix getPrefix(){
		return prefix;
	}

	/**
	 * Get the multiplicity
	 * @return the retrieved multiplicity character
	 */
	public Multiplicity getMultiplicity(){
		return multiplicity;
	}

	/**
	 * Get the separator
	 * @return the retrieved separator character
	 */
	public Separator getSeparator(){
		return separator;
	}

	/**
	 * Get the key word
	 * @return the retrieved keyword
	 */
	public String getkeyWord(){
		return keyWord;
	}

	/**
	 * Get the boolean value
	 * @return the retrieved boolean value
	 */
	public boolean hasDataItem() {
		return hasDataItem;
	}

	/**
	 * Get the pattern of an option keyword
	 * @return the retrieved pattern
	 */
	public java.util.regex.Pattern getKeyWordPattern() {
		return keyWordPattern;
	}

	// 
	/**
	 * Add data item once it is found. It is called by check() method in the Options class
	 * @param dataItem The found data item
	 */
	public void addMatchedDataItem(String dataItem){
		if(hasDataItem){
			if (dataItem == null){
				throw new IllegalArgumentException(this.getClass().getName() + ": valueData may not be null");	
			}
			else{
				//System.out.println("OptionData: "+dataItem);
				matchedDataItem.add(dataItem);	
			}
		}
		counter++; // If a matched key has no data items
	}

	/**
	 * Get the numbers of matched key words, after comparing the predefined option against user input command,
	 * @return the number of times retrieved
	 */
	public int getMatchedKeysCount(){
		// Check the number of times a key matched,
		// based on the number of times a data item matched.
		if(hasDataItem){
			return matchedDataItem.size();
		}
		else
			return counter; // If a matched key has no data items
	}
}

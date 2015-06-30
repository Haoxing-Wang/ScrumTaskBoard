package taskBoard;

import taskBoard.OptionData;
import taskBoard.Separator;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class creates an option set instance to group multiple options
 */
public class OptionSet {

	private Prefix prefix;
	private Multiplicity multiplicity;
	private String setName;
	private String setDescription;
	private int minData=0, maxData=0;
	private ArrayList<String> data = new ArrayList<String>();
	private ArrayList<OptionData> options = new ArrayList<OptionData>();
	private HashMap<String, OptionData> keyWords = new HashMap<String, OptionData>();

	/**
	 * Constructor
	 * @param prefix The character in front of the option keyword, set in Options class
	 * @param multiplicity the number of times an option keyword could be occurred in predefined option set, set in Options class
	 * @param setName The given set name, set in Options class
	 * @param minData The minimum numbers of data items (without prefix) in predefined command line, set in Option class
	 * @param maxData The maximum numbers of data items (without prefix) in predefined command line, set in Option class
	 * @param setDescription Description of this set, set in Option class
	 */
	public OptionSet(Prefix prefix, Multiplicity multiplicity, String setName, int minData, int maxData, String setDescription){
		this.prefix=prefix;
		this.multiplicity=multiplicity;
		this.setName=setName;
		this.maxData=maxData;
		this.minData=minData;
		this.setDescription=setDescription;
	}

	/**
	 * Add a value option with the given keyWord, separator, and multiplicity, and data items, and the prefix
	 * @param keyWord The key word for an option
	 * @param separator The character separate each argument
	 * @param value Whether or not there is data item after key word
	 * @param multiplicity The number of times an option key word could be occurred in a single command
	 * @return The option set instance itself (to support invocation chaining for addOption() methods)
	 */
	public OptionSet addOption(String keyWord, Separator separator, boolean value, Multiplicity multiplicity){
		if (keyWord == null){
			throw new IllegalArgumentException(this.getClass().getName() +": key cannot be null");	
		}
		if (multiplicity == null){
			throw new IllegalArgumentException(this.getClass().getName() + ": multiplicity may not be null");	
		}
		if (separator == null){
			throw new IllegalArgumentException(this.getClass().getName() + ": separator may not be null");	
		}
		if (keyWords.containsKey(keyWord)){
			throw new IllegalArgumentException(this.getClass().getName() +" "+ keyWord + " has already been defined for this OptionSet");	
		}

		OptionData optionData = new OptionData(prefix, keyWord, separator, value, multiplicity);
		options.add(optionData);
		keyWords.put(keyWord, optionData);
		return this;
	}
	
	/**
	 * Add a value option with the given key word and multiplicity
	 * @param keyWord the key word for an option
	 * @return The option set instance itself (to support invocation chaining for addOption() methods)
	 */
	public OptionSet addOption(String keyWord) {
		return addOption(keyWord, Separator.BLANK, false, multiplicity);
	}

	/**
	 * Get all the options contained in a given option set
	 * @return the found Option Data
	 */
	public ArrayList<OptionData> getAllOptions(){
		return options;
	}

	/**
	 * Get related information based on an option name such as "create" in a predefined Option set
	 * @param optionName The name of the option that needs to be retrieved
	 * @return The retrieved information related "create" could be found in OptionData class
	 */
	public OptionData getAnOption(String optionName){
		if(optionName==null){
			throw new IllegalArgumentException(this.getClass().getName()+": the key cannot be null");
		}
		if (!keyWords.containsKey(optionName)){
			throw new IllegalArgumentException(this.getClass().getName() + ": unknown key: " + optionName);
		}
		return keyWords.get(optionName);		
	}

	/**
	 * Get the name of an option set
	 * @return The retrieved set name
	 */
	public String getSetName(){
		return setName;
	}

	/**
	 * Get the minimum data items of an option set
	 * @return The retrieved minimum number of data items
	 */
	public int getMinData(){
		return minData;
	}

	/**
	 * Get the maximum data items of an option set
	 * @return The retrieved maximum number of data items
	 */
	public int getMaxData(){
		return maxData;
	}

	/**
	 * Get the description of an option set
	 * @return The retrieved set description
	 */
	public String getSetDescription(){
		return setDescription;
	}

	/**
	 * Get any data items in the user input command line that not start with prefix,
	 * it works after calling check() method in Options class.
	 * @return the found data items after check() method 
	 */
	public ArrayList<String> getDataItems() {
		return data;
	}
}

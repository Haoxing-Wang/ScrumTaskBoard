package taskBoard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import taskBoard.OptionSet;

/**
 * The Options class is the core part for verifying user input command line,
 * by calling the method check()
 */

public class Option {

	private int minData, maxData;
	private Prefix prefix;
	private Multiplicity multiplicity;
	private String[] allArgumentsOfInput;
	private StringBuffer errorInformation;
	private HashMap<String, OptionSet> optionSets = new HashMap<String, OptionSet>();

	/**
	 * Constructor
	 * @param args                The user input command line
	 * @param prefix              The prefix character used in front of the option keyword, it will set for all the option keywords
	 * @param multiplicity 		  The multiplicity to use for an option
	 * @param minData             The minimum number of data items (items without prefix)
	 * @param maxData             The maximum number of data items (items without prefix)
	 * @throws IllegalArgumentException If either args, prefix, multiplicity, minData or maxData is invalid
	 */

	public Option(String args[], Prefix prefix, Multiplicity multiplicity, int minData, int maxData) {
		if (args == null){
			throw new IllegalArgumentException(this.getClass().getName()+": args cannot be null");	
		}
		if (prefix == null){
			throw new IllegalArgumentException(this.getClass().getName()+": prefix may not be null");	
		}
		if (multiplicity == null){
			throw new IllegalArgumentException(this.getClass().getName()+": multiplicity may not be null");	
		}
		if (minData < 0){
			throw new IllegalArgumentException(this.getClass().getName()+": minData must be greater than 0");	
		}
		if (maxData < minData){
			throw new IllegalArgumentException(this.getClass().getName()+": maxData must be greater than minData");	
		}

		allArgumentsOfInput = new String[args.length]; // Initialize the arguments array according to the length of the user inputs
		int i = 0;
		for (String value : args){
			allArgumentsOfInput[i++] = value;
		}

		this.prefix=prefix;
		this.multiplicity=multiplicity;
		this.minData=minData;
		this.maxData=maxData;
	}

	/**
	 * Constructor
	 * @param args                The user input command line
	 * @param minData             The minimum number of data items (items without prefix)
	 * @param maxData             The maximum number of data items (items without prefix)
	 * <p>
	 * @throws IllegalArgumentException If either args, minData or maxData is invalid
	 */
	public Option (String[] args, int minData, int maxData){
		this(args, Prefix.DASH, Multiplicity.ONCE, minData, maxData);
	}

	/**
	 * Add an option set
	 * @param setName the name for this set, and must be a unique identifier
	 * @param minDataValue the minimum number of data items (items without prefix) for this set
	 * @param maxDataValue the maximum number of data items (items without prefix) for this set
	 * @param setDescription the description of this option set
	 * @return The new OptionSet instance created. This is useful to allow chaining of addOption() calls right after this method
	 */
	public OptionSet addSet(String setName, int minDataValue, int maxDataValue, String setDescription){
		if (setName == null){
			throw new IllegalArgumentException(this.getClass().getName() + ": setName may not be null");	
		}
		if (optionSets.containsKey(setName)){
			throw new IllegalArgumentException(this.getClass().getName() + ": a set with the name " + setName + " has already been defined");	
		}
		OptionSet os = new OptionSet(prefix, multiplicity, setName, minData, maxData, setDescription);
		optionSets.put(setName, os);
		return os;
	}

	/**
	 * Add an option set
	 * @param setName The name for this set, and must be a unique identifier
	 * @param setDescription The description for this option set
	 * @return The new OptionSet instance created. This is useful to allow chaining of addOption() calls right after this method
	 */
	public OptionSet addSet(String setName, String setDescription){
		return addSet(setName, 0, 0, setDescription);
	}

	/**
	 *  Get an option set based on the given set name
	 * @param setName The name of the set needed
	 * @return the retrieved option set
	 */
	public OptionSet getSet(String setName) {
		return optionSets.get(setName);
	}

	/**
	 * Get the number of option sets added in an Options instance
	 * @return The retrieved number
	 */
	public int getNumberOfOptionSets(){
		return optionSets.size();
	}

	/**
	 * Get all the option set names in optionSet
	 * @return the retrieved setNames list
	 */
	public ArrayList<String> getSetName(){
		ArrayList<String> setNames = new ArrayList<String>();
		for(Entry<String, OptionSet> e:optionSets.entrySet()){
			setNames.add(e.getKey());
		}
		return setNames;
	}

	/**
	 * Get the error message after calling check() method
	 * @return the retrieved message
	 */
	public String getErrorInformation() {
		return errorInformation.toString();
	}

	/**
	 * Check the user input command line against a given set
	 * @param setName The name of the option set needs to be checked
	 * @return a boolean value indicating the checking result
	 */
	public boolean check(String setName){
		if (setName == null){
			throw new IllegalArgumentException(this.getClass().getName() + ": setName may not be null");	
		}
		if (optionSets.get(setName) == null){
			throw new IllegalArgumentException(this.getClass().getName() + ": Unknown OptionSet: " + setName);
		}

		errorInformation = new StringBuffer();
		errorInformation.append("Error information of "+setName+"\n");

		OptionSet set = optionSets.get(setName);
		ArrayList<OptionData> options = set.getAllOptions();	 // Should be the number of keywords in the predefined option set
		ArrayList<String> dataItems = set.getDataItems();	 	 // Should be an empty list

		if (options.size() == 0) { 								 // No option arguments were predefined
			if (allArgumentsOfInput.length == 0) { 			     // No user inputs were found as well
				return true;
			}
			else {
				System.out.println("No options have been defined\n");
				//errorInformation.append("No options have been defined, nothing to check\n");
				return false;
			}
		}
		else{													 // Options were predefined
			if (allArgumentsOfInput.length == 0) {				 // No user inputs were found
				errorInformation.append("Options have been defined, but no user inputs were given\n");
				return false;
			}
		}

		int count=0;
		String keyWord= null;
		String nextValue= null;
		boolean[] matched = new boolean[allArgumentsOfInput.length];
		for (int i = 0; i < matched.length; i++){
			matched[i] = false; 								// Initially, no match for each user input argument (option key and option data item)
		}

		while(true){ 											// Get each argument of the user input command
			String currentValue=null;
			boolean shouldAdd=true;

			for (OptionData optionData : options) { 			// This option information were from predefined option set
				Matcher match = optionData.getKeyWordPattern().matcher(allArgumentsOfInput[count]); // Check the list pattern against each field in user input command. Since user input command has 5 arguments, then it needs to check 5 times 

				if(match.lookingAt()){ // Checking pattern
					
					if (optionData.hasDataItem()) { // Checking if it has data items
						
						if (optionData.getSeparator() == Separator.BLANK) { // Checking separator
							
							if (count + 1 == allArgumentsOfInput.length) {
								
								shouldAdd = false; // If it is the last argument of the user input, no data should be followed
							}
							else{
								nextValue = allArgumentsOfInput[count + 1];
								if (nextValue.startsWith("-")) { // Checking whether or not the next one in the user input is an argument (starts with -)
									shouldAdd = false;
									if(!nextValue.matches("\\d+")) // Checking whether or not a data item is negative when it is a number
										throw new IllegalArgumentException("The number is invalid, it must be a positve integer");
								}
								else {
									currentValue = nextValue;
									matched [count++] = true; // Matched data item found in the user input
									matched[count]   = true;  // Matched option key found in the user input
									// either add the data item here or use if(shouldAddDataItem) statement.
									// If I add here, it will only add the data item after a matched keyword.
									// If there are more data items in the user input, they won't be added
									// The missed ones will be added in the following codes
									 dataItems.add(currentValue); 
								}
							}
						} // End of checking separator
					} //End of checking data items
					
					else{ // If no data item exits after an argument in the predefined option set
						matched[count] = true;
					}

					if (shouldAdd) // It indicates the numbers of matched key words
						 optionData.addMatchedDataItem(currentValue); 
					else
						break;

				} // End of checking key word pattern
				
			}// End of for loop
			
			count++; // Checking for the next argument in user command
			if (count >= allArgumentsOfInput.length)
				break; 
		}

		// Identify unmatched arguments and actual (non-option) data.
		// For example -create -story 1 'description', the following code retrieves the 'description',
		// which was missed above
		int first = -1;
		for (int i = 0; i < matched.length; i++) { // matched.length should equal the allArguments.length
			if (!matched[i]) { // If any argument from user input is not matched
				if (allArgumentsOfInput[i].startsWith("-")) { // This is an unmatched option
					errorInformation.append("Option "+allArgumentsOfInput[i]+" has not been defined\n");
				}
				else { // This is a data item
					if (first < 0) first = i;
					dataItems.add(allArgumentsOfInput[i]);
				}
			}
		}

		boolean isWrong = true;
		for (OptionData optionData : options) { // Checking number of times a key word occurs    
			keyWord= optionData.getkeyWord();
			isWrong = false;

			// It's different when use if statement to check multiplicity
			// compared with using switch statement
			if(optionData.getMultiplicity().equals(Multiplicity.ONCE)){ 
				if(optionData.getMatchedKeysCount()!=1)
					isWrong =true;
				else
					isWrong =false;
			}

			if (isWrong) {
				 errorInformation.append("The number of times option keyword -"+keyWord+" was wrong\n");
				return false;
			}
		}

		// Check numbers of data
		if (dataItems.size() < set.getMinData() || dataItems.size() > set.getMaxData()) {
			errorInformation.append("The data range should be between: "+set.getMinData()+" and "+set.getMaxData());
			return false;
		}
		return true;
	}
}

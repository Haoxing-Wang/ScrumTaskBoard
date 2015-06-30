package taskBoard;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.xml.sax.SAXException;

import taskBoard.Multiplicity;
import taskBoard.Separator;

/**
 * This is the main class that user interacts with,
 * it fist predefined the correct command lines that could be used to complete the work,
 * then it accepts the user input command lines,
 * and call check() method to compare user input with predefined command lines.
 * If user input command line is valid, then execute the command calling different methods.
 */
public class Main {

	private Option opt;
	private ArrayList<String> matched;

	public Main(String[] args){
		opt = new Option(args,0,3); // Create an option instance with user input, data items range from 0 to 3
		opt.addSet("createStory", "create a new user story with the given ID and description")
		.addOption("create", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("story", Separator.BLANK, true, Multiplicity.ONCE);

		opt.addSet("createTask", "create a new task with the given task ID and description")
		.addOption("create", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("task", Separator.BLANK, true, Multiplicity.ONCE);

		opt.addSet("listStories", "list all user stories that have been created including ID's")
		.addOption("list", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("stories", Separator.BLANK, false, Multiplicity.ONCE);

		opt.addSet("listTasks", "list all the tasks associated with the given story id")
		.addOption("list", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("tasks", Separator.BLANK, false, Multiplicity.ONCE);

		opt.addSet("deleteStoryTask", "delete the user story or task with given IDs")
		.addOption("delete", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("task", Separator.BLANK, true, Multiplicity.ONCE);

		opt.addSet("deleteStory", "delete the user story with the given id")
		.addOption("delete", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("story", Separator.BLANK, true, Multiplicity.ONCE);

		opt.addSet("moveTask", "move the task to the new column")
		.addOption("move", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("task", Separator.BLANK, true, Multiplicity.ONCE);

		opt.addSet("updateTask", "update/modify a task's description")
		.addOption("update", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("task", Separator.BLANK, true, Multiplicity.ONCE);

		opt.addSet("completeStory", "mark the story with the given ID as completed")
		.addOption("complete", Separator.BLANK, false, Multiplicity.ONCE)
		.addOption("story", Separator.BLANK, true, Multiplicity.ONCE);
		
		checkOption(opt);
	}

	public void checkOption(Option opt){
		int falseCount=0;
		boolean checkResult =true;
		matched = new ArrayList<String>();
		for(String element: opt.getSetName()){
			checkResult = opt.check(element);
			System.out.println("Checking with "+element+": "+checkResult);
			if(checkResult){
				matched.add(element); // Save the matched set name 
				System.out.println("True option found!\nStop Checking and Executing the command...");
				try {
					executeOption();
				} catch (IOException | SAXException |XPathExpressionException
						| ParserConfigurationException e) {
					e.printStackTrace();
				}
				break;
			}
			else
				falseCount++;
		}
		if (falseCount==opt.getNumberOfOptionSets())
			System.out.println("\nChecking complete, the option key word is invalid");
	}

	public void executeOption() throws IOException, SAXException, ParserConfigurationException, XPathExpressionException{
		
		String[] keyWords = new String[2]; 			// Create a new String array with length of 2
		ArrayList<String> allDataItems =null;		// Create a new String list to hold all data items from user input

		if(matched.size()==1){
			// Get all options saved in this option set
			ArrayList<OptionData> allOptions = opt.getSet(matched.get(0)).getAllOptions();
			for(int i=0;i<allOptions.size();i++){
				keyWords[i]=allOptions.get(i).getkeyWord(); // Get key word of each option, and saved to the keyWords array
			}
			allDataItems = opt.getSet(matched.get(0)).getDataItems();
		}

		ExecuteOperation eo = new ExecuteOperation();
		String keyWordCombination = keyWords[0]+" "+keyWords[1];
		switch(keyWordCombination){

		case "create story":{
			if(allDataItems.size()!=2)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.add(allDataItems.get(0), allDataItems.get(1)); 
			break;
		}
		case "list stories":{
			if(allDataItems.size()>0)
				throw new IllegalArgumentException("No data item should be attached after keyWord");
			eo.listStories();
			break;
		}
		case "delete story":{
			if (allDataItems.size()!=1)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect"); 
			eo.delete(allDataItems.get(0));
			break;
		}
		case "create task":{
			if (allDataItems.size()!=3)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.add(allDataItems.get(0), allDataItems.get(1), allDataItems.get(2));
			break;
		}
		case "list tasks":{
			if (allDataItems.size()!=1)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.listTasks(allDataItems.get(0));
			break;
		}
		case "delete task":{
			if (allDataItems.size()!=2)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.delete(allDataItems.get(0), allDataItems.get(1));
			break;
		}
		case "move task":{
			if (allDataItems.size()!=3)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.moveTask(allDataItems.get(0), allDataItems.get(1), allDataItems.get(2));
			break;
		}
		case "update task":{
			if (allDataItems.size()!=3)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.updateTask(allDataItems.get(0), allDataItems.get(1), allDataItems.get(2));
			break;
		}
		case "complete story":{
			if (allDataItems.size()!=1)
				throw new IllegalArgumentException("Number of data items after keyWord are incorrect");
			eo.completeStory(allDataItems.get(0));
			break;
		}
		}
	}

	public static void main(String[] args){
		
		new Main(args);
	}
}

package testCases;

import java.util.ArrayList;

import taskBoard.Main;

/**
 * This class tests complete a story
 *1. create a new story with id 100
 *2. add three new tasks with id 1 and 2 to story 100
 *3. list stories to see the original status of story 100
 *4. change status of task 1 and 2 to inprocess, toverify
 *5. try to run the complete story method
 *6. change status of two tasks to done
 *7. try to run the complete story method
 */
public class TestFour {

	public static void main(String[] args) {
		String[] commandOne = {"-create", "-story", "100", "this is the 100th story"};
		String[] commandTwo = {"-create", "-task", "100", "1", "this is the 1st task of 100th story"};
		String[] commandThree = {"-create", "-task", "100", "2", "this is the 2nd task of 100th story"};
		String[] commandFour ={"-list", "-stories"};
		String[] commandFive ={"-move", "-task", "100", "1", "INPROCESS"};
		String[] commanSix ={"-move", "-task", "100", "2", "TOVERIFY"};
		String[] commandSeven = {"-complete", "-story", "100"};
		String[] commandEight ={"-move", "-task", "100", "1", "DONE"};
		String[] commandNigh ={"-move", "-task", "100", "2", "DONE"};

		ArrayList<String[]> list =new ArrayList<String[]>();
		list.add(commandOne);
		list.add(commandTwo);
		list.add(commandThree);
		list.add(commandFour);
		list.add(commandFive);
		list.add(commanSix);
		list.add(commandSeven);
		list.add(commandEight);
		list.add(commandNigh);
		list.add(commandSeven);
		list.add(commandFour);

		int count =1;
		for(String[] e: list){
			System.out.println("\n=============");
			System.out.println("Executing the command "+count+"...");
			new Main(e);
			count++;
		}
	}
}

package testCases;

import java.util.ArrayList;

import taskBoard.Main;

/**
 * This class testing update a task description, and move a task to a new status
 * 1. create a new story with id 10
 * 2. add task id 2 and task id 3 to story 10
 * 3. list tasks to see its original status
 * 4. move task id 2 under story 10 to in process
 * 5. move task id 3 under story 10 to done
 * 6. list tasks to verify the updated status
 */

public class TestTwo {

	public static void main(String[] args) {
		
		String[] commandOne = {"-create", "-story", "10", "this is the 10th story"};
		String[] commandTwo = {"-create", "-task", "10", "2", "this is the 1st task of 10th story"};
		String[] commandThree = {"-create", "-task", "10", "3", "this is the 2n task of 10th story"};
		String[] commandFour = {"-move", "-task", "10", "2", "INPROCESS"};
		String[] commandFive = {"-move", "-task", "10", "3", "DONE"};
		String[] commanSix = {"-list", "-tasks", "10"};
		
		ArrayList<String[]> list =new ArrayList<String[]>();
		list.add(commandOne);
		list.add(commandTwo);
		list.add(commandThree);
		list.add(commanSix);
		list.add(commandFour);
		list.add(commandFive);
		list.add(commanSix);
		
		int count =1;
		for(String[] e: list){
			System.out.println("\n=============");
			System.out.println("Executing the command "+count+"...");
			new Main(e);
			count++;
		}
	}
}

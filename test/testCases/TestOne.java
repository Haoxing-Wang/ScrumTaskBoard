package testCases;

import java.util.ArrayList;

import taskBoard.Main;

/**
 * This class testing create a new story or a new task, and list stories and tasks.
 * 1. create a new story with id 50
 * 2. create new task id 1 and task id 2 to story 50
 * 3. create a new story with id 70
 * 4. list stories
 * 5. list tasks
 */
public class TestOne {
	
	public static void main(String[] args){
		
		String[] commandOne = {"-create", "-story", "50", "this is the 50th story"};
		String[] commandTwo = {"-create", "-task", "50", "1", "this is the 1st task of 50th story"};
		String[] commandThree = {"-create", "-task", "50", "2", "this is the 2n task of 50th story"};
		String[] commandFour = {"-create", "-story", "70", "this is the 70th story"};
		String[] commandFive = {"-list", "-stories"};
		String[] commanSix = {"-list", "-tasks", "50"};
		
		ArrayList<String[]> list =new ArrayList<String[]>();
		list.add(commandOne);
		list.add(commandTwo);
		list.add(commandThree);
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

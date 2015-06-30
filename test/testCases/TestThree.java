package testCases;

import java.util.ArrayList;

import taskBoard.Main;

/**
 * This class tests the delete task and story
 * 1. create story with id 11
 * 2. add task id 1 and id 2 to story 11
 * 3. create story with id 22
 * 4. add task id 3 to story 22
 * 5. delete task 2 with story id 11
 * 6. delete story 22
 */

public class TestThree {
	
	public static void main(String[] args){
		String[] commandOne = {"-create", "-story", "11", "this is the 11th story"};
		String[] commandTwo = {"-create", "-task", "11", "1", "this is the 1st task of 11th story"};
		String[] commandThree = {"-create", "-task", "11", "2", "this is the 2nd task of 11th story"};
		String[] commandFour = {"-create", "-story", "22", "this is the 22th story"};
		String[] commandFive = {"-create", "-task", "22", "3", "this is the 3rd task of 22th story"};
		String[] commanSix = {"-delete", "-task", "11", "2"};
		String[] commandSeven = {"-delete", "-story", "22"};
		
		ArrayList<String[]> list =new ArrayList<String[]>();
		list.add(commandOne);
		list.add(commandTwo);
		list.add(commandThree);
		list.add(commandFour);
		list.add(commandFive);
		list.add(commanSix);
		list.add(commandSeven);
		
		int count =1;
		for(String[] e: list){
			System.out.println("\n=============");
			System.out.println("Executing the command "+count+"...");
			new Main(e);
			count++;
		}
	}
}

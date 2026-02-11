/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		9/8/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *****************************************************************************************/

package week4.menu;

import java.util.ArrayList;
import week4.newStructure.ScannerInputFilter;

public class BasicMenu implements MenuSpace {
	
	// Object fields
	private String menuMessage;
	private ArrayList<Menu> components;
	private ScannerInputFilter input;
	
	/**
	 * No-args constructor.
	 */
	public BasicMenu(){
		components = new ArrayList<>();
		input = new ScannerInputFilter();
	}
	
	/**
	 * This constructor takes the menu message as a parameter.
	 * @param message
	 */
	public BasicMenu(String message){
		setDisplayMessage(message);
		components = new ArrayList<>();
		input = new ScannerInputFilter();
	}
	
	/**
	 * This method executes the menu object.
	 */
	public void executeMenu(){
		int selection;
		BasicMenuItem currentItem;
		
		do{
			displayMenuMessage();
			printMessage("");
			displayMenuItems();
			printMessage("");
			selection = input.getIntegerWithinBounds(1, components.size(), "Enter a menu selection: ");
			printMessage("");
			currentItem = (BasicMenuItem)components.get(selection - 1);
			currentItem.performAction();
		} while(selection != components.size());
	}
	
	/**
	 *  This methods allows menu items to be added to the menu
	 */
	public void addComponent(Menu component){
		
		if (component instanceof BasicMenuItem){
			components.add(component);
		}
	}
	
	/**
	 * This method sets the menus display message.
	 */
	public void setDisplayMessage(String message){
		
		menuMessage = message;
	}
	
	// This method prints the menus message
	private void displayMenuMessage(){
		
		printMessage(menuMessage);
	}
	
	// This method prints all menu item messages
	private void displayMenuItems(){
		
		for (int i = 0; i < components.size(); i++){
			BasicMenuItem currentComponent = (BasicMenuItem)components.get(i);
			printMessage("\t" + (i + 1) + ") " + currentComponent.getDisplayMessage());
		}
	}
	
	private void printMessage(String message){
		
		System.out.println(message);
	}
}

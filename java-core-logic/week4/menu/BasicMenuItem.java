/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		9/8/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *****************************************************************************************/

package week4.menu;

public class BasicMenuItem implements MenuItem {
	
	// Object fields
	private String menuItemMessage;
	private MenuAction component;
	
	/**
	 * No-args constructor.
	 */
	public BasicMenuItem(){
		
	}
	
	/**
	 * Constructor for the menu item class that set the menu item message.
	 * @param message
	 */
	public BasicMenuItem(String message){
		
		setDisplayMessage(message);
	}
	
	/**
	 * This method stores an object of the Menu interface. This implementation stores menu actions.
	 */
	public void addComponent(Menu component){
		
		if (MenuAction.class == component.getClass()){
			this.component = (MenuAction)component;
		}
	}
	
	/**
	 * This method specifically stores menu action objects.
	 * @param component
	 */
	public void addComponent(MenuAction component){
		
			this.component = component;
	}
	
	/**
	 * This method set the menu item message for this object.
	 */
	public void setDisplayMessage(String message){
		
		menuItemMessage = message;
	}
	
	/**
	 * This method prints the menu item message. This is used by the menu to create selection lists.
	 */
	public String getDisplayMessage(){
		
		return menuItemMessage;
	}
	
	/**
	 * This method perform the action of a stored menu action object
	 */
	public void performAction(){
		
		if (component != null){
			component.executeAction();
		}
	}
}

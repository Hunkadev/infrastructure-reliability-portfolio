/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		9/8/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *****************************************************************************************/

package week4.menu;

public interface MenuComponent extends Menu {
	
	public void addComponent(Menu component);
	public void setDisplayMessage(String message);
}
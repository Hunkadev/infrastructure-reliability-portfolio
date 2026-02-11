/* *****************************************************************************************
 * Author: Christian Hunkus
 * Date: 8/30/2015
 * Class: UoP PRG 420
 * Instructor:	David Frank
 * 
 * Summary of this module:
 * 		This is the new beginning to the Sales person program. Classes are becoming more
 * 		well defined and encapsulated resulting in this being a small and concise class.
 * 
 * 		This class simply creates a Salesperson list operator and passes it as an argument
 * 		to an instance of the menu builder class. Finally the launch menu method is called
 * 		to start the program.
 ******************************************************************************************/

package week4.newStructure;

public class ProgramEntry {
	
	public static void main(String[] args){
		SalespersonOperations listOperator = new SalespersonOperations();
		SalespersonMenuBuilder builder = new SalespersonMenuBuilder(listOperator);
		
		builder.launchMenus(); // Launches the menu system
	}

}

/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		9/7/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *****************************************************************************************/

package week4.newStructure;  // Just my sorting mechanism, can be deleted.

import java.util.ArrayList;

public class SalespersonList {
	
	// Constant fields
	private static final int MAX_LIST_SIZE = 15;		// Limit the maximum size of the list
	
	// Primitive fields
	private int conflictRectificationIndex;				// Store the index of the item a user wants to modify
	
	// Object fields
	private ArrayList<Salesperson> employees;			// Store employees
	// Stores the indexes of elements from the employee list in a parallel structure with the conflict list
	// This is used for deleting elements from the employees list
	private ArrayList<Integer> employeeDeletionIndicies;
	private ArrayList<Salesperson> conflictList;		// Store employees with the same name during a search
	private ScannerInputFilter input;					// Store a basic input object
	
	/**
	 * No-args constructor to initialize the list for use
	 */
	public SalespersonList(){
		
		conflictRectificationIndex = -1;				// Set to clearly identifiable default value
		
		employees = new ArrayList<Salesperson>();		// Create an array list to hold employees
		employeeDeletionIndicies = new ArrayList<Integer>();	// Create an array list to hold parallel deletion indicies
		conflictList = new ArrayList<Salesperson>();	// Create an array list to hold employee search conflicts
		input = new ScannerInputFilter();				// Create a basic input object
	}
	
	/**
	 * Method to add a new salesperson to the list.
	 * @param employee
	 */
	public void addSalesperson(Salesperson employee){
		String message = "List is currently full.";
		
		if (!hasReachedMaxListSize()){
			employees.add(employee);
		} else{
			printMessage(message);
		}
	}
	
	/**
	 * Method to remove a salesperson from the list.
	 * @param name
	 */
	public void removeSalesperson(String name){
		String message = "List is currently empty.";
		
		if (!isEmptyList(employees)){							// Check if the list is empty
			scanListForConflicts(name);							// Scan the list for duplicate matches
			if (rectifyConflicts()){							// Get the correct list entry
				removeEmployeeFromList();						// Remove employee
			}
			clearList(conflictList);							// Clear the duplicates list
		} else{
			printMessage(message);
		}
	}
	
	/**
	 * Method to retrieve a salesperson from the current list. A dummy object
	 * is returned if the salesperson is not found.
	 * @param name
	 * @return
	 */
	public Salesperson retrieveSalesperson(String name){
		String message = "List is currently empty.";
		
		if (!isEmptyList(employees)){			// Check if the list is empty
			scanListForConflicts(name);			// Scan the list for duplicate matches
			if (rectifyConflicts()){			// Get the correct list entry
				Salesperson salespersonHolder = conflictList.get(conflictRectificationIndex);
				clearList(conflictList);		// Clear the duplicates list
				
				return salespersonHolder;		// Return the correct match
			}
		} else{
			printMessage(message);
		}
		
		return new Salesperson("junk", "junk");	// Return a dummy object to prevent a null pass
	}
	
	
	/**
	 * This method prints the name of the salesperson at the specified index.
	 * @param index
	 */
	public void printSalespersonAtIndex(int index){
			String name = employees.get(index).getFullName();
			
			printMessage("\t" + name);
	}
	
	/**
	 * This method returns the current size of the list of employees.
	 * @return
	 */
	public int size(){
		
		return listSize(employees);
	}
	
	// This is a helper method for list traversing. If more than one object is found in the list
	// that matches the search criteria, each matching object is added to a queue for further
	// processing
	private void scanListForConflicts(String name){
		Salesperson employee;
		
		for (int i = 0; i < listSize(employees); i++){		// Iterate through all sales people
			employee = employees.get(i);
			if (isCurrentEmployeesName(employee, name)){	// Check for a name match
				conflictList.add(employee);					// Add to the conflicts list when matches are found
				employeeDeletionIndicies.add(new Integer(i));	// Add the conflicts index to the deletion list
			}
		}
	}
	
	// This is a helper method to check if two name strings are equal
	private boolean isCurrentEmployeesName(Salesperson employee, String name){
		
		if (employee.getFullName().equals(name)){
			return true;
		}
		
		return false;
	}
	
	// This method examines the list of potential conflicts and makes sure it is not empty
	private boolean rectifyConflicts(){
		String message = "No matches were found.";
		
		if (!isEmptyList(conflictList)){
			selectCorrectSalesperson();
			return true;
		} else{
			printMessage(message);
			return false;
		}
	}
	
	// This method determines if any conflicts were found
	private void selectCorrectSalesperson(){
		
		if (listSize(conflictList) > 1){	// More than one entry matched
			displayConflicts();				// Print the list of conflicts for the user
		} else{
			conflictRectificationIndex = 0;	// No conflicts were found set the index to the first entry
		}
	}
	
	// This method outputs a list of all found conflicts for the user. The correct entry
	// can then be selected from the list.
	private void displayConflicts(){
		int selection;
		
		// Display the menu text and all conflict items
		printMessage("Select the employee to modify. (Enter the menu value)");
		for (int i = 0; i < listSize(conflictList); i++){
			Salesperson currentEmployee = conflictList.get(i);
			printMessage("\t" + (i + 1) + ") " + currentEmployee.getFullName() 
			+ " Salary: " +currentEmployee.getSalary() 
			+ " Total sales: " + currentEmployee.getCumulativeSales());
		}
		
		// Get the user to choose the correct list item and store the list index
		selection = input.getIntegerWithinBounds(1, listSize(conflictList), "\nEnter a menu item: ");
		conflictRectificationIndex = selection - 1; // Convert from the menu index to the list index
	}
	
	// This method check to see if the employee list has reached its maximum allowed size
	private boolean hasReachedMaxListSize(){
		
		if (employees.size() >= MAX_LIST_SIZE){
			return true;
		}
		
		return false;
	}
	
	// This method deletes the correct employee entry from the employees list using
	// a parallel index storage array list with the conflicts list
	private void removeEmployeeFromList(){
		int employeeIndex = employeeDeletionIndicies.get(conflictRectificationIndex);
		
		employeeDeletionIndicies.clear();		// Clear the deletion array for next use
		employees.remove(employeeIndex);		// Remove the employee at the specified index
	}
	
	// This method gets the current size of an ArrayList of type Salesperson
	private int listSize(ArrayList<Salesperson> list){
		
		return list.size();
	}
	
	// This method checks if the current ArrayList of type Salesperson is empty
	private boolean isEmptyList(ArrayList<Salesperson> list){
		 
		if (list.isEmpty()){
			return true;
		}
		
		return false;
	}
	
	// This method is used to clear the conflict list after each use
	private void clearList(ArrayList<Salesperson> list){
		
		list.clear();
	}
	
	// This is a general screen printing method
	private void printMessage(String message){
		System.out.println(message);
	}

}

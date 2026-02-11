/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		9/7/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *****************************************************************************************
 * UPDATE NOTES FOR: 9/9/2015 BY: Christian Hunkus
 * 
 * Add a method to remove employees from the comparison list.
 *
 *****************************************************************************************
 * UPDATE NOTES FOR: 9/8/2015 BY: Christian Hunkus
 * 
 * Add comments to this class.
 * 
 * Add duplication check to the add to working list method. (COMPLETE)
 * 
 * Clean the compare working list method. (COMPLETE)
 * 
 * Add a method to print all employees in the list. (COMPLETE)
 * 
 *****************************************************************************************/

package week4.newStructure;  // Just my sorting mechanism, can be deleted.

import java.util.ArrayList;

public class SalespersonOperations {
	
	// Object fields
	private Salesperson currentEmployee;			// Contains one employee at a time for alterations
	private ArrayList<Salesperson> workingList;		// This is a list of Salesperson object for comparison
	private SalespersonList employeeList;			// This is the main list for storing all Salesperson objects
	private ScannerInputFilter input;				// Stored the input object this class uses
	
	/**
	 * No-args constructor to initialize the SalespersonOperations class.
	 */
	public SalespersonOperations(){
		workingList = new ArrayList<Salesperson>();
		employeeList = new SalespersonList();
		input = new ScannerInputFilter();
	}
	
	/**
	 * This method creates and initializes a new Salesperson and add it to the employeeList.
	 */
	public void createNewEmployee(){
		
		currentEmployee = new Salesperson();
		setCurrentEmployeeFirstName();
		setCurrentEmployeeLastName();
		setCurrentEmployeeSalary();
		setCurrentEmployeeCommissionPercentage();
		currentEmployee.setTablePrinter(new DefaultTablePrinter());
		employeeList.addSalesperson(currentEmployee);
	}
	
	/**
	 * This method adds the currently stored Salesperson object to this classes working list
	 * if is it not already in the list. The list is used to compare Salesperson objects.
	 */
	public void addEmployeeToWorkingList(){
		
		if (isNotNull(currentEmployee) && roomInList()){
			String message = "This employee is already in the list.";
			if (!isDuplicateEntry()){
				workingList.add(currentEmployee);
			} else{
				printMessage(message);
			}
		} else{
			printMessage("Either there is no current salesperson to add or the list is full.");
		}
	}
	
	/**
	 * This method removes employees from comparison list.
	 */
	public void removeEmployeeFromWorkingList(){
		
		if (isNotNull(currentEmployee) && workingList.size() > 0){
			removeEmployeeFromList();
		} else{
			printMessage("Either there is no current salesperson to remove or the list is empty.");
		}
	}
	
	/**
	 * This method performs comparisons on sales people stored in the comparison menu.
	 */
	public void compareWorkingList(){
		
		if (workingList.size() > 1) {		// Check that there is more than one salesperson to compare
			double highestCompensation;		// Store the highest compensation
			
			highestCompensation = getHighestCompensation();	// Retrieve the highest compensation value
			for (Salesperson employee : workingList){
				// Check if the current sales person has a compensation lower than the highest
				if (employee.totalAnnualCompensation() != highestCompensation){
					printMessage(employee.getFullName() + " needs to increase total sales volume by:");
					printFormattedMessage("$%,.2f", employee.salesNeededForCompensationTarget(highestCompensation));
					printFormattedMessage(" to reach a compensation value of $%,.2f.\n\n", highestCompensation);
				} else{
					// Print this message when the salesperson with the highest compensation is found
					printMessage(employee.getFullName() + " is even with the highest compensation value of:");
					printFormattedMessage("$%,.2f.\n\n", highestCompensation);
				}
			} 
		} else{
			printMessage("Not enough employees in the list.");
		}
	}
	
	/**
	 * This method prints all sales people in the employee master list.
	 */
	public void displayAllEmployeesInEmployeeList(){
		
		if (employeeList.size() != 0){
			printMessage("All Employees currently in the employee list are:\n");
			for (int i = 0; i < employeeList.size(); i++){
				employeeList.printSalespersonAtIndex(i);
			}
		} else{
			printMessage("The list is currently empty.");
		}
		printMessage("");
	}
	
	/**
	 * This method prints all the sales people currently in the comparison list.
	 */
	public void displayAllEmployeesInComparisonList(){
		
		if (workingList.size() != 0){
			printMessage("All Employees currently in the comparison list are:\n");
			for (Salesperson employee : workingList){
				String name = employee.getFullName();
				
				printMessage("\t" + name);
			}
		} else{
			printMessage("The list is currently empty.");
		}
		printMessage("");
	}
	
	/**
	 * This method sets the salary value for the current employee.
	 */
	public void setCurrentEmployeeSalary(){
		
		if (isNotNull(currentEmployee)){
			double salary;
			String message = "Enter a salary value for " + 
			currentEmployee.getFullName() + " between 0 and 500,000: ";
			
			salary = input.getDoubleWithinBounds(0, 500000, message);
			currentEmployee.setSalary(salary);
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method sets the commission percentage value for the current employee.
	 */
	public void setCurrentEmployeeCommissionPercentage(){
		
		if (isNotNull(currentEmployee)){
			double commissionPercentage;
			String message = "Enter a commission percentage value for " + 
			currentEmployee.getFullName() + " between 0 and 200: ";
			
			commissionPercentage = input.getDoubleWithinBounds(0, 200, message);
			currentEmployee.setCommissionPercentage(commissionPercentage);
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method sets the total sales value for the current employee.
	 */
	public void setCurrentEmployeeCumulativeSales(){
		
		if (isNotNull(currentEmployee)){
			double cumulativeSales;
			String message = "Enter a cumulative sales value for " + 
			currentEmployee.getFullName() + " between 0 and 1,000,000: ";
			
			cumulativeSales = input.getDoubleWithinBounds(0,  1000000,  message);
			currentEmployee.setCumulativeSales(cumulativeSales);
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method sets the first name for the current employee.
	 */
	public void setCurrentEmployeeFirstName(){

		if (isNotNull(currentEmployee)){
			currentEmployee.setFirstName(inputFirstName());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method sets the last name for the current employee.
	 */
	public void setCurrentEmployeeLastName(){

		if (isNotNull(currentEmployee)){
			currentEmployee.setLastName(inputLastName());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method retrieves the salesperson with matching name and set it to the current employee.
	 */
	public void getCurrentEmployee(){
			String name;
			Salesperson dummyCheck;
			
			name = inputFullName();
			dummyCheck = employeeList.retrieveSalesperson(name);	// Check if the employee did not exist
			if (!isDummySalesperson(dummyCheck)){
				currentEmployee = dummyCheck;						// If the employee exists, set to current
			}
	}
	
	/**
	 * This method displays all data about the current employee and the potential compensation table.
	 */
	public void displayAllEmployeeDataAndTable(){
		
		displayAllEmployeeData();
		System.out.println();
		printTable();
	}
	
	/**
	 * This method displays all data about the current employee.
	 */
	public void displayAllEmployeeData(){
		
		getCurrentEmployeeFullName();
		getCurrentEmployeeSalary();
		getCurrentEmployeeCommissionPercentage();
		getCurrentEmployeeCumulativeSales();
		getCurrentEmployeeTargetSales();
		getCurrentEmployeeAccelerationFactor();
		getCurrentEmployeeAnnualCompensation();
	}
	
	/**
	 * This method displays salary data for the current employee.
	 */
	public void getCurrentEmployeeSalary(){

		if (isNotNull(currentEmployee)){
			String message = currentEmployee.getFullName() + "'s salary is $%,.2f.\n";
			
			printFormattedMessage(message, currentEmployee.getSalary());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays commission percentage data for the current employee.
	 */
	public void getCurrentEmployeeCommissionPercentage(){

		if (isNotNull(currentEmployee)){
			String message = currentEmployee.getFullName() + "'s commission percentage is %%%,.2f.\n";
			
			printFormattedMessage(message, currentEmployee.getCommissionPercentage() * 100);
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays total sales data for the current employee.
	 */
	public void getCurrentEmployeeCumulativeSales(){

		if (isNotNull(currentEmployee)){
			String message = currentEmployee.getFullName() + "'s cumulative sales is $%,.2f.\n";
			
			printFormattedMessage(message, currentEmployee.getCumulativeSales());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays target sales data for the current employee.
	 */
	public void getCurrentEmployeeTargetSales(){

		if (isNotNull(currentEmployee)){
			String message = currentEmployee.getFullName() + "'s target sale goal is $%,.2f.\n";
			
			printFormattedMessage(message, currentEmployee.getTargetSales());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays the acceleration factor for the current employee.
	 */
	public void getCurrentEmployeeAccelerationFactor(){

		if (isNotNull(currentEmployee)){
			String message = currentEmployee.getFullName() + "'s acceleration factor is %,.2f.\n";
			
			printFormattedMessage(message, currentEmployee.getAccelerationFactor());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays total compensation data for the current employee.
	 */
	public void getCurrentEmployeeAnnualCompensation(){

		if (isNotNull(currentEmployee)){
			String message = currentEmployee.getFullName() + "'s total annual compensation is $%,.2f.\n";
			
			printFormattedMessage(message, currentEmployee.totalAnnualCompensation());
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays the full name for the current employee.
	 */
	public void getCurrentEmployeeFullName(){

		if (isNotNull(currentEmployee)){
			String message = "The current employees name is " + currentEmployee.getFullName();
			
			printMessage(message);
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method displays the potential compensation table for the current employee.
	 */
	public void printTable(){
		
		if (isNotNull(currentEmployee)){
			currentEmployee.printTable();
		} else{
			printNoEmployeeExists();
		}
	}
	
	/**
	 * This method removes the current employee from the employee list.
	 */
	public void removeEmployee(){
		String name;
		
		name = inputFullName();
		employeeList.removeSalesperson(name);
		
		if (isNotNull(currentEmployee)){
			if (currentEmployee.getFullName().equals(name)){
				currentEmployee = null;
			}
		}
	}
	
	// This method gets the full name of a person
	private String inputFullName(){
		
		return inputFirstName() + " " + inputLastName();
	}
	
	// This method gets the first name of a person
	private String inputFirstName(){
		
		return input.getEnglishName("Enter the employees first name: ");
	}
	
	// This method gets the last name of a person
	private String inputLastName(){
		
		return input.getEnglishName("Enter the employees last name: ");
	}
	
	// Method to notify that the current employee is empty
	private void printNoEmployeeExists(){
		
		printMessage("There is currently no employee set to modify.");
	}
	
	// Easy print method
	private void printMessage(String message){
		
		System.out.println(message);
	}
	
	// Easy formatted message printing
	private void printFormattedMessage(String message, double value){
		
		System.out.printf(message, value);
	}
	
	// This method removes an employee from the comparison list
	private void removeEmployeeFromList(){
		
		for (int i = 0; i < workingList.size(); i++){
			Salesperson employee = workingList.get(i);
			if (currentEmployee.equals(employee)){
				workingList.remove(i);
			}
		}
	}
	
	// This method determines the highest compensation value among a list of sales people
	private double getHighestCompensation(){
		double highestCompensation = 0;
		
		for (Salesperson employee : workingList){
			if (employee.totalAnnualCompensation() > highestCompensation){
				highestCompensation = employee.totalAnnualCompensation();
			}
		}
		
		return highestCompensation;
	}
	
	// This method identifies duplicate entries in the comparision list
	private boolean isDuplicateEntry(){
		
		for (Salesperson employee : workingList){
			if (employee.equals(currentEmployee)){
				return true;
			}
		}
		
		return false;
	}
	
	// This method identifies dummy return object from trying to retrieve sales person objects
	private boolean isDummySalesperson(Salesperson employee){
		String dummyName = "junk junk";
		
		if (employee.getFullName().equals(dummyName)){
			return true;
		}
		
		return false;
	}
	
	// This method check for room to add sales people into the comparison list
	private boolean roomInList(){
		
		if (workingList.size() < employeeList.size()){
			return true;
		}
		
		return false;
	}
	
	// This method checks if an object is null and adds readability
	private boolean isNotNull(Object object){
		
		if (object != null){
			return true;
		}
		
		return false;
	}

}

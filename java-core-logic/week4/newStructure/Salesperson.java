/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		8/24/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *****************************************************************************************
 * UPDATE NOTES FOR: 9/8/2015 BY: Christian Hunkus
 * 
 * Added Object class method overloads for: toString, equals, hashCode, and clone. (COMPLETE)
 * 
 * Migrate the responsibility of calculating the remaining sales to meet a compensation
 * target to the Salesperson class. (COMPLETE)
 * 
 *****************************************************************************************
 * UPDATE NOTES FOR: 9/7/2015 BY: Christian Hunkus
 * 
 * Update the test constructor to include user initialization of targetSales. (COMPLETE)
 * 
 * Add firstName field as well as set and get methods. (COMPLETE)
 * 
 * Add lastName field as well as set and get methods. (COMPLETE
 * 
 * Add name validation for the name fields. (COMPLETE)
 * 
 *****************************************************************************************
 * UPDATE NOTES FOR: 8/31/2015 BY: Christian Hunkus
 * 
 * Add get and set for targetSales. (COMPLETE)
 * 
 * Update the logic for the 80% target sales condition for commission. (COMPLETE)
 * 
 * Create an acceleration algorithm, do not prompt user. (COMPLETE)
 * 
 * Implement the additional logic for final compensation calculation. (COMPLETE)
 * 
 * Add a set method for a TablePrinter. (COMPLETE)
 * 
 * Create a TablePrinter field and strategy access method so different
 * TablePrinters can be passed to this object for table output. (COMPLETE)
 * 
 ******************************************************************************************
 * Additional Identified Opportunities:
 * 
 * Factor out validation. (COMPLETE)
 * 
 * This class is getting large. Consider revising for next week.
 * 		UPDATE:		Despite the size of this class, all methods seem to present relevant
 * 					responsibilities to ensuring the integrity of Salesperson objects.
 */

package week4.newStructure; // Just my sorting mechanism, can be deleted.

import java.lang.Math;

public class Salesperson implements Cloneable {
	
	// Constant fields
	static final double COMMISSION_THRESHOLD = 0.8;
	static final int MINIMUM_NAME_LENGTH = 3;
	static final int ACCEPTABLE_NAME_LENGTH = 20;
	
	// Primitive fields
	private double salary;					// Salary for the Salesperson
	private double commissionPercentage;	// Commission% for the Salesperson
	private double cumulativeSales;			// Total sales for the Salesperson
	private static double targetSales;	 	// Target sales for the Salesperson and is the same for all instances

	
	// Object fields
	private String firstName;				// Stores a first name for the employee
	private String lastName;				// Stores a last name for the employee
	private TablePrinter table;				// Table printing algorithm.
	
	/**
	 * No-args constructor for the sales person class.
	 */
	public Salesperson(){
		setFirstName("Empty");
		setLastName("Empty");
		setSalary(0.0);
		setCommissionPercentage(0.0);
		setCumulativeSales(0.0);
		
		if (targetSales == 0){
			setTargetSales(120000);
		}
	}
	
	/**
	 * Basic constructor for the sales person class. Takes two String
	 * and two doubles: first name, last name, salary, and commission 
	 * percentage.
	 * @param firstName
	 * @param lastName
	 * @param salary
	 * @param commissionPercentage
	 */
	public Salesperson(String firstName, String lastName, double salary, double commissionPercentage){
		setFirstName(firstName);
		setLastName(lastName);
		setSalary(salary);
		setCommissionPercentage(commissionPercentage);
		setCumulativeSales(0.0);
		
		if (targetSales == 0){
			setTargetSales(120000);
		}
	}
	
	/**
	 * Constructor designed to return junk objects instead of throwing null
	 * not for general use, only place holding for null return values.
	 * @param firstName
	 * @param lastName
	 */
	public Salesperson(String firstName, String lastName){
		setFirstName(firstName);
		setLastName(lastName);
	}
		
	/**
	 * Full constructor for the sales person class. Takes two Strings
	 * and four doubles: salesperson salary, commission percentage, 
	 * cumulative sales, and target sales. This constructor is mainly 
	 * for testing purposes and not intended for general use.
	 * @param firstName
	 * @param lastName
	 * @param salary
	 * @param commissionPercentage
	 * @param cumulativeSales
	 * @param targetSales
	 */
	public Salesperson(String firstName, String lastName, 
			double salary, double commissionPercentage, 
			double cumulativeSales, double targetSales){
		
		setFirstName(firstName);
		setLastName(lastName);
		setSalary(salary);
		setCommissionPercentage(commissionPercentage);
		setCumulativeSales(cumulativeSales);
		
		if (targetSales == 0){
			setTargetSales(120000);
		}
	}
	
	// This method overloads the Object class toString method and prints an
	// individualized identifier message for this class.
	public String toString(){
		
		return getClass().getName()
				+ "[Salary=" + salary
				+ ", Commission%=" + commissionPercentage
				+ ", Sales=" + cumulativeSales
				+ "]";
	}
	
	// This method overloads the Object class equals methods and defines equality
	// for Salesperson objects.
	public boolean equals(Object object){
		
		// Good default test block
		if (this == object) return true;					// Checks for reference to the same object
		if (object == null) return false;					// Checks for non-existent object
		if (getClass() != object.getClass()) return false;	// Checks if object is the same class
		
		// Test equality of Name, Salary, Commission%, and CumulativeSales
		// These methods are at the very end of the class
		boolean nameIsEqual = equalName(this, (Salesperson)object);
		boolean salaryIsEqual = equalSalary((Salesperson)object);
		boolean commissionPercentageIsEqual = equalCommissionPercentage((Salesperson)object);
		boolean cumulativeSalesAreEqual = equalCumulativeSales((Salesperson)object);
		
		// Return true only if all are true
		return nameIsEqual && salaryIsEqual && commissionPercentageIsEqual && cumulativeSalesAreEqual;
	}
	
	// This method overloads the Object class hashCode method and generates an
	// individualized hashCode based on the object contents.
	public int hashCode(){
		int nameHash = 3 * (firstName.hashCode() + lastName.hashCode());
		int salaryHash = 5 * new Double(salary).hashCode();
		int commissionPercentageHash = 71 * new Double(commissionPercentage).hashCode();
		int cumulativeSalesHash = 17 * new Double(cumulativeSales).hashCode();
		
		return nameHash + salaryHash + commissionPercentageHash + cumulativeSalesHash;
	}
	
	
	// This method overloads the Object class clone method and defines what a
	// copy of a Salesperson object looks like.
	public Salesperson clone(){
		
		try{
			
			// To be honest I am still a bit confused on how exactly this works. 
			// I didn't use a new keyword and have to assume that the Object class
			// returns a malleable object.
			Salesperson clone = (Salesperson)super.clone();
			clone.firstName = firstName;
			clone.lastName = lastName;
			clone.salary = salary;
			clone.commissionPercentage = commissionPercentage;
			clone.cumulativeSales = cumulativeSales;
			
			return clone;
		} catch (CloneNotSupportedException e){
			return null; // I am leary about this, but it is not supposed to occur
		}
	}
	
	/**
	 * Method to set the salary value for a salesperson.
	 * @param salary
	 */
	public void setSalary(double salary){
		double lowerBound = 0;
		double upperBound = 500000;
		String message = "Salary value out of range. Enter a value between 0 and 500,000.";
		
		if (validInput(salary, lowerBound, upperBound, message)){
			this.salary = salary;
		}
	}
	
	/**
	 * Method to set the commission percentage for a salesperson.
	 * @param commissionPercentage
	 */
	public void setCommissionPercentage(double commissionPercentage){
		double lowerBound = 0;
		double upperBound = 200;
		String message = "Commission percentage value out of range. "
				+ "Enter a value between 0 and 200.";
		
		if (validInput(commissionPercentage, lowerBound, upperBound, message)){
			this.commissionPercentage = commissionPercentage / 100;
		}
	}
	
	/**
	 * Method to set the total current sales value of a salesperson.
	 * @param cumulativeSales
	 */
	public void setCumulativeSales(double sales){
		double lowerBound = 0;
		double upperBound = 1000000;
		String message = "Cumulative sales value out of range. Enter a value between 0 and 1,000,000.";
		
		if (validInput(sales, lowerBound, upperBound, message)){
			cumulativeSales = sales;
		}
	}
	
	/**
	 * Method to set the target sales value of a salesperson.
	 * @param cumulativeSales
	 */
	public void setTargetSales(double sales){
		double lowerBound = 0;
		double upperBound = 1000000;
		String message = "Target sales value out of range. Enter a value between 0 and 1,000,000.";
		
		if (validInput(sales, lowerBound, upperBound, message)){
			targetSales = sales;
		}
	}
	
	/**
	 * Method to set the first name of a salesperson.
	 * @param name
	 */
	public void setFirstName(String name){
		
		if (isValidName(name)){
			firstName = name;
		}
	}
	
	/**
	 * Method to set the last name of a salesperson.
	 * @param name
	 */
	public void setLastName(String name){
		
		if (isValidName(name)){
			lastName = name;
		}
	}
	
	/**
	 * Method to set the TablePrinter object for the salesperson class.
	 * @param table
	 */
	public void setTablePrinter(TablePrinter table){
		
			this.table = table;
	}
	
	/**
	 * Method to return the current salary value for a salesperson.
	 * @return
	 */
	public double getSalary(){
		return salary;
	}
	
	/**
	 * Method to return the current commission percentage for a salesperson.
	 * @return
	 */
	public double getCommissionPercentage(){
		return commissionPercentage;
	}
	
	/**
	 * Method to return the total current sales value for a salesperson.
	 * @return
	 */
	public double getCumulativeSales(){
		return cumulativeSales;
	}
	
	/**
	 * Method to return the target sales number for a salesperson.
	 * @return
	 */
	public double getTargetSales(){
		return targetSales;
	}
	
	/**
	 * Method to return the full name for a salesperson.
	 * @return
	 */
	public String getFullName(){
		return getFirstName() + " " + getLastName();
	}
	
	/**
	 * Method to return the first name only for a salesperson.
	 * @return
	 */
	public String getFirstName(){
		return firstName;
	}
	
	/**
	 * Method to return the last name only for a salesperson.
	 * @return
	 */
	public String getLastName(){
		return lastName;
	}
	
	/**
	 * Method to return the acceleration factor.
	 * @return
	 */
	public double getAccelerationFactor(){
		return currentAccelerationFactorState();
	}
	
	/**
	 * Method to return the total compensation value for a salesperson.
	 * @return
	 */
	public double totalAnnualCompensation(){
		return calculateAnnualCompensation();
	}
	
	public double salesNeededForCompensationTarget(double compensationTarget){
		
		return getSalesNeeded(compensationTarget);
	}
	
	// Used to output a table of sales data to the console.
	public void printTable(){
		
		// Makes sure the TablePrinter exists before trying to use it.
		if (isNotNull(table)){
			cumulativeSales = table.displayTable(this); // Displays the table and return the original sales.
		} else{
			System.out.println("No valid TablePrinter detected.");
		}
		
	}
	
	// This method takes a target compensation value and calculates the required
	// additional sales needed to meet the target compensation goal.
	//
	// The equation to describe this returned value is determined from a manipulation
	// of the annual compensation equation:
	//
	// >>AnnualCompensation = Salary + AnnualSales * Commission% * AccelerationFactor
	//
	// The equation for the calculation of sales to meet a target is:
	//
	// >>TargetAnnualSales = (CompensationTarget - Salary) / (Commission% * AccelerationFactor)
	//
	// From this, the remaining sales needed reach the target compensation is:
	//
	// >>TargetAnnualSales - CurrentAnnualSales
	//
	// However, depending on if the final sales required to meet the compensation goal
	// is below the target sales factor, the acceleration factor will not be part of the
	// calculation. So two equation states are required.
	private double getSalesNeeded(double compensationTarget){
		
		// Calculate the amount an employee would be compensated when their sales are
		// exactly the target sales amount. Then use the proper equation to calculate
		// remaining sales to meet the compensation target.
		double compensationAtTargetSales = salary 
				+ targetSales * (1 - COMMISSION_THRESHOLD) * commissionPercentage;
		double targetCompensationMinusSalary = compensationTarget - salary;
		double applicableCompensationMultipliers;
		double salesNeededForCompensationTarget;
		
		System.out.println("Compensation target: " + compensationTarget
				+ "\nCompensation when at target sales amount: " + compensationAtTargetSales
				+ "\nCompensation target minus salary (" + salary + "): " + targetCompensationMinusSalary);
		
		
		if (compensationAtTargetSales > compensationTarget){
			// Execute this statement if the compensation goal requires a sales value
			// below the target sales value
			applicableCompensationMultipliers = commissionPercentage;
			salesNeededForCompensationTarget = 
					targetCompensationMinusSalary / applicableCompensationMultipliers;
			
			System.out.println("Commission% only: " + applicableCompensationMultipliers
					+ "\nSales needed to meet the compensation target: " + salesNeededForCompensationTarget
					+ "\nCumulative sales: " + cumulativeSales);
			
			return salesNeededForCompensationTarget;
		} else{
			// Execute this statement if the compensation goal requires a sales value
			// above or equal to the target sales value
			applicableCompensationMultipliers = commissionPercentage * currentAccelerationFactorState();
			salesNeededForCompensationTarget = 
					targetCompensationMinusSalary / applicableCompensationMultipliers;
			
			System.out.println("Commission% and accelFactor: " + applicableCompensationMultipliers
					+ "\nSales needed to meet the compensation target: " + salesNeededForCompensationTarget
					+ "\nCumulative sales: " + cumulativeSales);
			
			return salesNeededForCompensationTarget;
		}
	}
	
	// Calculates the total compensation of a Salesperson.
	private double calculateAnnualCompensation(){
		// Checks if a Salesperson is eligible to receive commission.
		double commissionPercentage = currentCommissionState();
		// Checks if the Salesperson is eligible to receive an acceleration factor.
		double accelerationFactor = currentAccelerationFactorState();
		
		// Calculates a Salesperson's total compensation.
		return salary + getCommissionAmount(commissionPercentage) 
			+ getAcceleratedCommissionAmount(commissionPercentage, accelerationFactor);
	}
	
	// This method calculates accelerated commission only on sales over the sales target 
	private double getAcceleratedCommissionAmount(double commissionPercentage, double accelerationFactor){
		double salesAmountOverTargetSales = cumulativeSales - targetSales;
		
		return salesAmountOverTargetSales * commissionPercentage * accelerationFactor;
	}
	
	// This method calculates standard commission only for sales between
	// 80% and 100% (80% <= sales <= 100%) of the sales target
	private double getCommissionAmount(double commissionPercentage){
		double standardCommissionEarned;
		double salesAmountOverThreshold = cumulativeSales - targetSales * COMMISSION_THRESHOLD;
		double maximumStandardCommission = targetSales * (1 - COMMISSION_THRESHOLD);
		
		if (salesAmountOverThreshold < targetSales){
			standardCommissionEarned = salesAmountOverThreshold * commissionPercentage;
		} else{
			standardCommissionEarned = maximumStandardCommission * commissionPercentage;
		}
		
		return standardCommissionEarned;
	}
	
	// Returns the commission percentage if the Salesperson is eligible.
	private double currentCommissionState(){
		
		if (hasEarnedCommission()){
			return commissionPercentage;
		}
		
		return 0; // Causes no commission to be added to the salesperson's salary.
	}
	
	// Checks if the current sales for a salesperson meets or exceeds
	// the threshold to earn commission.
	private boolean hasEarnedCommission(){
		
		return hasMetThreshold(cumulativeSales, targetSales * COMMISSION_THRESHOLD);
	}
	
	// Returns an acceleration factor if a Salesperson is eligible.
	private double currentAccelerationFactorState(){
		
		if (hasEarnedAccelerationFactor()){
			// The equation is (1/2) * e^(x/y) where
			// x = total sales
			// y = target sales
			return 0.5 * Math.exp(cumulativeSales/targetSales);
		} else {
			return 1; // Causes no change to calculation of commission.
		}
	}
	
	// Checks if the current sales for a salesperson meets or exceeds
	// the threshold to earn an acceleration factor.
	private boolean hasEarnedAccelerationFactor(){
		
		return hasMetThreshold(cumulativeSales, targetSales);
	}
	
	// Method that checks if a value exceeds a threshold.
	private boolean hasMetThreshold(double value, double threshold){
		
		if (value >= threshold){
			return true;
		}
		
		return false;
	}
	
	// This is a generic validation method that prompts the user with a message, checks 
	// an input value against defined bounds, and returns whether or not the input is
	// within the bounds.
	private boolean validInput(double input, double lowerBound, double upperBound, String message){
		
		if (input < lowerBound || input > upperBound){
			System.out.println(message);
			return false;
		}
		
		return true;
	}
	
	// This method ensures that string values passed as names contain valid characters
	// and are not ridiculously long. 
	private boolean isValidName(String name){
		
		// A name must be within a certain length and contain appropriate characters
		// to return a true result. If a name is too long or contains invalid 
		// characters then this fails. 
		if (isAcceptableLength(name) && containsValidChars(name)){
			return true;
		} else{
			return false;
		}
	}
	
	// This method makes sure that strings are of an acceptable length.
	private boolean isAcceptableLength(String name){
		
		if (name.length() >= MINIMUM_NAME_LENGTH && name.length() <= ACCEPTABLE_NAME_LENGTH){
			return true;
		} else{
			return false;
		}
	}
	
	// This method makes sure that names contain acceptable characters.
	private boolean containsValidChars(String name){
		char currentChar; // Temporary storage for comparison values
		
		// Loop through each character in a string and check for validity
		for (int i = 0; i < name.length(); i++){
			currentChar = name.charAt(i); // Set the temporary char
			// Compare the temporary char against known upper and lower case
			// values of the English language. (Or and language using this
			// character set)
			if (!isUppercaseAlphabetic(currentChar) && !isLowercaseAlphabetic(currentChar)){
				return false;
			}
		}
		
		return true;
	}
	
	// This method checks if a character is uppercase in ASCII
	private boolean isUppercaseAlphabetic(char character){
		
		// Compare against the range of uppercase values
		if (character < 65 || character > 90){
			return false;
		} else{
			return true;
		}
	}
	
	// This method checks if a character is lowercase in ASCII
	private boolean isLowercaseAlphabetic(char character){
		
		// Compare against the range of lowercase values
		if (character < 97 || character > 122){
			return false;
		} else{
			return true;
		}
	}
	
	// Checks if an object is null.
	private boolean isNotNull(Object object){
		
		if (object == null){
			return false;
		}
		
		return true;
	}
	
	// The following four methods are used in the confirmation of equality process
	// handled in the overridden equals(Object object) method
	
	// This method check for identical names in the this object and the comparison object
	private boolean equalName(Salesperson currentObject, Salesperson otherObject){
		String currentObjectName = currentObject.getFullName();
		String otherObjectName = otherObject.getFullName();
		
		return currentObjectName.equals(otherObjectName);
	}
	
	// This method check for identical salary in the this object and the comparison object
	private boolean equalSalary(Salesperson otherObject){
		
		return salary == otherObject.getSalary();
	}
	
	// This method check for identical commission% in the this object and the comparison object
	private boolean equalCommissionPercentage(Salesperson otherObject){
		
		return commissionPercentage == otherObject.getCommissionPercentage();
	}
	
	// This method check for identical total sales in the this object and the comparison object
	private boolean equalCumulativeSales(Salesperson otherObject){
		
		return cumulativeSales == otherObject.getCumulativeSales();
	}

}

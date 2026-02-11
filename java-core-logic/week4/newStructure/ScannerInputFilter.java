/* *****************************************************************************************
 * Author: Christian Hunkus
 * Date: 8/30/2015
 * 
 * Summary of this module:
 * 		This module performs standardized and validated input operations. 
 * 
 * UPDATE:	After a number of validation iterations, I believe that this represents
 * 			a rather clean and functional implementation of dynamic validation masks.
 * 			This specific class implements the InputObject interface I created and I
 * 			used a Scanner to capture input. Other class are free to use other capture
 * 			methods.
 * 
 ******************************************************************************************
 * 
 * ADDED TO THE SALESPERSON PROJECT ON 9/7/2015
 * 
 ******************************************************************************************/

package week4.newStructure;  // Just my sorting mechanism, can be deleted.

import java.util.*;

public class ScannerInputFilter implements InputObject {
	
	// Constant fields
	static final int MINIMUM_NAME_LENGTH = 3;
	static final int MAXIMUM_NAME_LENGTH = 20;
	
	// Primitive fields
	private double doubleInput;
	private long longInput;
	private int integerInput;
	
	// Object fields
	private String stringInput;
	private Scanner inputReader; // I suspect making this field static may save memory
	
	/**
	 * No-args constructor. Initializes a Scanner object for this object to use.
	 */
	public ScannerInputFilter() {
		
		inputReader = new Scanner(System.in);
	}
	
	public boolean setInputObject(Object inputReader) {
		
			return false;
	}
	
	public double getDoubleInput(String message) {
		displayUserPrompt(message);
		validateDoubleInput();
		
		return doubleInput;
	}
	
	public double getDoubleWithinBounds(double lowerBound, double upperBound, String message) {
		
		do {
			getDoubleInput(message);
		} while (doubleInput < lowerBound || doubleInput > upperBound);
		
		return doubleInput;
	}
	
	public long getLongInput(String message) {
		displayUserPrompt(message);
		validateLongInput();
		
		return longInput;
	}
	
	public long getLongWithinBounds(long lowerBound, long upperBound, String message) {
		
		do {
			getLongInput(message);
		} while (longInput < lowerBound || longInput > upperBound);
		
		return longInput;
	}
	
	public int getIntegerInput(String message) {
		displayUserPrompt(message);
		validateIntegerInput();
		
		return integerInput;
	}
	
	public int getIntegerWithinBounds(int lowerBound, int upperBound, String message) {
		
		do {
			getIntegerInput(message);
		} while (integerInput < lowerBound || integerInput > upperBound);
		
		return integerInput;
	}
	
	public String getStringInput(String message) {
		displayUserPrompt(message);
		retrieveStringInput();
		
		return stringInput;
	}
	
	public String getAlphabeticStringInput(String message) {
		while (validateAlphabeticOnly(getStringInput(message)) == false) {
			System.out.println("Input must not contain non-alphabetic characters.\n");
		}
		
		return stringInput;
	}
	
	public String getEnglishName(String message){
		while (!isValidName(getStringInput(message))){
			System.out.println("Names must not contain non-alphabetic characters, "
					+ "and must be between 3 and 20 characters.\n");
		}
		
		return stringInput;
	}
	
	public char getCharInput(String message) {
		displayUserPrompt(message);
		retrieveStringInput();
		
		return stringInput.charAt(0);
	}
	
	public char getAlphabeticCharInput(String message) {
		getAlphabeticStringInput(message);
		
		return stringInput.charAt(0);
	}
	
	private void validateDoubleInput() {
		
		while (retrieveDoubleInput() == false) {
			System.out.print("Input must be a number: ");
		}
		
	}
	
	private void validateLongInput() {
		
		while (retrieveLongInput() == false) {
			System.out.print("Input must be a number: ");
		}
		
	}
	
	private void validateIntegerInput() {
		
		while (retrieveIntegerInput() == false) {
			System.out.print("Input must be an integer: ");
		}
		
	}
	
	// This method ensures that string values passed as names contain valid characters
	// and are not ridiculously long. 
	private boolean isValidName(String name){
		
		// A name must be within a certain length and contain appropriate characters
		// to return a true result. If a name is too long or contains invalid 
		// characters then this fails. 
		if (isAcceptableLength(name) && validateAlphabeticOnly(name)){
			return true;
		} else{
			return false;
		}
	}
	
	// This method makes sure that name strings are of an acceptable length.
	private boolean isAcceptableLength(String name){
		
		if (name.length() >= MINIMUM_NAME_LENGTH && name.length() <= MAXIMUM_NAME_LENGTH){
			return true;
		} else{
			return false;
		}
	}
	
	private boolean validateAlphabeticOnly(String input) {
		char currentChar; // Temporary storage for comparison values
		
		// Loop through each character in a string and check for validity
		for (int i = 0; i < input.length(); i++){
			currentChar = input.charAt(i); // Set the temporary char
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
	
	private boolean retrieveDoubleInput() {
		
		try {
			doubleInput = inputReader.nextDouble();
			clearInputBuffer();
			return true;
		} catch (InputMismatchException a) {
			clearInputBuffer();
			return false;
		}
	}
	
	private boolean retrieveLongInput() {
		
		try {
			longInput = inputReader.nextLong();
			clearInputBuffer();
			return true;
		} catch (InputMismatchException a) {
			clearInputBuffer();
			return false;
		}
	}
	
	private boolean retrieveIntegerInput() {
		
		try {
			integerInput = inputReader.nextInt();
			clearInputBuffer();
			return true;
		} catch (InputMismatchException a) {
			clearInputBuffer();
			return false;
		}
	}
	
	private void retrieveStringInput() {
		
		stringInput = inputReader.nextLine();
	}
	
	private void displayUserPrompt(String message) {
		
		System.out.print(message);
	}
	
	private void clearInputBuffer() {
		
		inputReader.nextLine();
	}

}

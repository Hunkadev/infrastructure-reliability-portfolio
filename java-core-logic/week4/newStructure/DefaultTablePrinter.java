/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		8/31/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *
 *INTENT:
 *		This class manages the printing of output to the console and was created as
 *		the default method of doing so. Other TablePrinters can be defined using the
 *		TablePrinter interface.
 *****************************************************************************************/

package week4.newStructure; // Just my sorting mechanism, can be deleted.

// Defines the class and signifies that it uses the TablePrinter interface type.
public class DefaultTablePrinter implements TablePrinter{
	
	// This is a defined number of character spaces representing table width.
	private final int LINE_LENGTH = 49;
	
	private double originalCumulativeSales; // Stores the original value of total sales.
	private int numberOfSalesLines; // Stores the number of lines required to present table data.
	
	private Salesperson employee; // Stores the calling Salesperson.
	
	/**
	 * Method used to display a table of sales data in the console.
	 * This method is required by the interface.
	 */
	public double displayTable(Salesperson employee){
		
		originalCumulativeSales = employee.getCumulativeSales(); // Store the original value.
		this.employee = employee; // Store the calling object.
		calculateNumSalesLines(); // Calculate the number of lines required to display table data.
		
		// Draw the table.
		for (int i = 0; i < (numberOfSalesLines + 3); i++){ // Add 3 to account for the header.
			printCurrentLine(i); // Call the content drawing algorithm.
		}
		printHorizontalBound(); // Print the table bottom boundary.
		
		return originalCumulativeSales; // Return the original total sales value.
	}
	
	// Content drawing algorithm.
	private void printCurrentLine(int i){
		
		if (i == 0){ 				// Draw the first line of output.
			printHorizontalBound(); // Print the tables top boundary.
		} else if (i == 1){ 		// Draw the second line of output.
			printHeader(); 			// Print the header text.
		} else if (i == 2){ 		// Draw the third line of output.
			printHorizontalBound(); // Print a boundary between the header and sales data.
		} else{ 					// Draw all successive lines of output.
			printSalesData(); 		// Print the current sales data.
		}
	}
	
	// Boundary drawing algorithm.
	private void printHorizontalBound(){
		
		// Draw the boundary line using the defined table width.
		for (int i = 0; i < LINE_LENGTH; i++){
			System.out.print("="); // Boundary symbol.
		}
		// Output a newline after drawing the boundary.
		System.out.println();
	}
	
	// Output the table header.
	private void printHeader(){
		
		System.out.print("|Sales Totals\t\t|Total Compensation\t|\n");
	}
	
	// Print the current line of sales data.
	private void printSalesData(){
		
		// Format the input and pass values.
		System.out.printf("\t$%,.2f\t|\t$%,.2f\t\n",
				employee.getCumulativeSales(),
				employee.totalAnnualCompensation());
		// Increase the employees total sales value for the next calculation.
		employee.setCumulativeSales(employee.getCumulativeSales() + 5000);
	}
	
	// Calculate the number of lines required to output all sales data.
	private void calculateNumSalesLines(){
		double currentSales; // Currently achieved sales.
		double possibleSales; // 150% sales value.
		
		currentSales = employee.getCumulativeSales(); // Set the current sales.
		possibleSales = currentSales * 1.5; // set the 150% sales.
		
		// Calculate the number of sales lines. This ends up being the possible sales
		// subtracted by the current sales to get the difference. This is then divided
		// by 5000 to get the number of 5000 sized increments in the difference. Then
		// the resultant data is cast into an integer type for the loop. Finally 1 is
		// added to the final result to compensate for rounding during the cast.
		numberOfSalesLines = (int)((possibleSales - currentSales) / 5000) + 1;
	}
}

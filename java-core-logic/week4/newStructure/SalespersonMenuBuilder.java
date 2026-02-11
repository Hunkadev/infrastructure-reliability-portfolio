/* ***************************************************************************************
 *Written by: 	Christian Hunkus
 *Date: 		9/8/2015
 *Class: 		PRG420
 *Instructor:	David Frank
 *
 *****************************************************************************************
 * This class is used as a bridge between the Salesperson classes and the menu classes.
 * This is an effort to decouple the dependence of the menu system from what is presents.
 * 
 * Menu spaces, menu items, and menu actions are all created separately and then combined
 * into the functioning menu system. Once the menus are built, the menu system can be
 * launched.
 * 
 * A menu space is simply a repository for a variable number of menu items. The menu space
 * does not know anything about its menu items, only how to display them.
 * 
 * A menu item is a singular entry on a menu. It knows the menu message to display and how
 * to execute the component it contains, but not what is in the component.
 * 
 * A menu action is created from a simple interface with one method 'executeAction'
 * This allows for is to execute custom defined actions while complying consistently
 * with the expectations of the menu system. The actions are created using anonymous 
 * classes and wrap the action to perform. when a menu item is executed, the action
 * is performed.
 * 
 * The order in which menu items are added to the menu determined how they are output
 * for the user. The first menu item is displayed first and the last menu item is
 * displayed last
 * 
 * Following are the definitions of the menu system and all its items and actions.
 *
 *****************************************************************************************/

package week4.newStructure;

import week4.menu.*;

public class SalespersonMenuBuilder {
	
	// Object fields
	private SalespersonOperations listManager;	// Manages the list of sales people
	
	private BasicMenuItem tempItem;				// Contains menu items during construction
	private BasicMenu mainMenu;					// Contains the main menu options
	private BasicMenu creationMenu;				// Contains the creation menu options
	private BasicMenu editMenu;					// Contains the edit menu options
	private BasicMenu retrieveMenu;				// Contains the retrieve menu options
	private BasicMenu comparisonMenu;			// Contains the comparison menu options
	private BasicMenu additionalFunctionsMenu;	// Contains extra functions
	
	/**
	 * Constructor that initializes this object and builds the menu
	 * @param listManager
	 */
	public SalespersonMenuBuilder(SalespersonOperations listManager){
		this.listManager = listManager;
		
		mainMenu = new BasicMenu();
		creationMenu = new BasicMenu();
		editMenu = new BasicMenu();
		retrieveMenu = new BasicMenu();
		comparisonMenu = new BasicMenu();
		additionalFunctionsMenu = new BasicMenu();
		
		buildMenus();
	}
	
	/**
	 * Method to launch the menu system
	 */
	public void launchMenus(){
		
		mainMenu.executeMenu();
	}
	
	// This method executes all the builder methods that construct the menu system
	private void buildMenus(){
		
		buildMainMenu();
		buildCreationMenu();
		buildEditMenu();
		buildRetrieveMenu();
		buildComparisonMenu();
		buildAdditionalFunctionsMenu();
	}
	
	// This method builds the main entry menu
	private void buildMainMenu(){
		
		// Set the main menu welcome message.
		mainMenu.setDisplayMessage("Welcome to the Salesperson program. "
				+ "Please select an option from below:");
		
		// Create the first menu option: Create Menu
		initializeTempMenuItem("Enter creation menu.");
		// Add an action to this menu item using an anonymous class
		tempItem.addComponent(new 				// Declare storage of a new MenuAction in the menu item
			MenuAction(){
				public void executeAction(){	// Method required by the interface
					creationMenu.executeMenu();	// This is the action to be performed
				}
			});
		mainMenu.addComponent(tempItem);		// Add the menu item to the main menu
		
		// Create the second menu option: Editing Menu
		initializeTempMenuItem("Enter the salesperson editing menu.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					editMenu.executeMenu();
				}
			});
		mainMenu.addComponent(tempItem);
		
		// Create the third menu option: Retrieval Menu
		initializeTempMenuItem("Enter the salesperson retrieval menu.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					retrieveMenu.executeMenu();
				}
			});
		mainMenu.addComponent(tempItem);
		
		// Create the fourth menu option: Compare Sales Employees
		initializeTempMenuItem("Enter the salesperson comparision menu.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					comparisonMenu.executeMenu();
				}
			});
		mainMenu.addComponent(tempItem);
		
		// Create the fifth menu option: Additional Functions for Sales Employees
		initializeTempMenuItem("Enter the additional features menu.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					additionalFunctionsMenu.executeMenu();
				}
			});
		mainMenu.addComponent(tempItem);
		
		// Create a menu option to exit the program.
		createExitMenuOption(mainMenu, "Exit program test.");
	}
	
	// This method builds the menu used to create new employees
	private void buildCreationMenu(){
		
		// Set the greeting message for this menu
		creationMenu.setDisplayMessage("This menu is used to create new sales people.\n"
				+ "Please select an option from below:");
		
		// Create a menu option for creating new employees
		initializeTempMenuItem("Create new salesperson.");
		tempItem.addComponent(new 
			MenuAction(){
				public void executeAction(){
					listManager.createNewEmployee();
				}
			});
		creationMenu.addComponent(tempItem);
		
		// Create a menu option to exit the program.
		createExitMenuOption(creationMenu, "Return to main menu.");
	}
	
	// This method builds the menu used to edit existing employee objects
	private void buildEditMenu(){
		
		// Set the greeting message for this menu
		editMenu.setDisplayMessage("This menu is used to edit information about a sales person.\n"
				+ "Please select an option from below:");
		
		// Create a menu option to view the current salesperson's name
		createViewCurrentEmployeeName(editMenu);
		
		// Create a menu option for editing a salesperson's salary
		initializeTempMenuItem("Edit the current salesperson's salary.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.setCurrentEmployeeSalary();
				}
			});
		editMenu.addComponent(tempItem);
		
		// Create a menu option for editing a salesperson's commission percentage
		initializeTempMenuItem("Edit the current salesperson's commission percentage.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.setCurrentEmployeeCommissionPercentage();
				}
			});
		editMenu.addComponent(tempItem);
		
		// Create a menu option for editing a salesperson's cumulative sales
		initializeTempMenuItem("Edit the current salesperson's cumulative sales.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.setCurrentEmployeeCumulativeSales();
				}
			});
		editMenu.addComponent(tempItem);
		
		// Create a menu option for editing a salesperson's first name
		initializeTempMenuItem("Edit the current salesperson's first name.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.setCurrentEmployeeFirstName();
				}
		});
		editMenu.addComponent(tempItem);
		
		// Create a menu option for editing a salesperson's last name
		initializeTempMenuItem("Edit the current salesperson's last name.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.setCurrentEmployeeLastName();
				}
			});
		editMenu.addComponent(tempItem);
		
		// Create a menu option for accessing a salesperson from the employee list
		createAccessEmployeeMenuOption(editMenu);
		
		// Create a menu option for displaying all sales people currently in the employee list
		createDisplayEmployeesInList(editMenu);
		
		// Create a menu option to exit the program.
		createExitMenuOption(editMenu, "Return to main menu.");
	}
	
	// This method builds the menu used to retrieve data about employees
	private void buildRetrieveMenu(){
		
		// Set the greeting message for this menu
		retrieveMenu.setDisplayMessage("This menu is used to retrieve information about a sales person.\n"
				+ "Please select an option from below:");
		
		// Create a menu option to view the current salesperson's name
		createViewCurrentEmployeeName(retrieveMenu);
		
		// Create a menu option for retrieving a salesperson's salary
		initializeTempMenuItem("Retrieve the current salesperson's salary.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeSalary();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving a salesperson's commission%
		initializeTempMenuItem("Retrieve the current salesperson's commission percentage.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeCommissionPercentage();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving a salesperson's cumulative sales
		initializeTempMenuItem("Retrieve the current salesperson's cumulative sales.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeCumulativeSales();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving a salesperson's sales target
		initializeTempMenuItem("Retrieve the current salesperson's target sales goal.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeTargetSales();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving a salesperson's acceleration factor
		initializeTempMenuItem("Retrieve the current salesperson's acceleration factor.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeAccelerationFactor();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving a salesperson's total compensation
		initializeTempMenuItem("Retrieve the current salesperson's total annual compensation.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeAnnualCompensation();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving all of a salesperson's information
		initializeTempMenuItem("Retrieve all information for the current salesperson.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.displayAllEmployeeData();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for retrieving all of a salesperson's information
		// and the potential earnings table for the salesperson
		initializeTempMenuItem("Retrieve all salesperson data and print a potential earnings table.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.displayAllEmployeeDataAndTable();
				}
			});
		retrieveMenu.addComponent(tempItem);
		
		// Create a menu option for accessing a salesperson from the employee list
		createAccessEmployeeMenuOption(retrieveMenu);
		
		// Create a menu option for displaying all sales people currently in the employee list
		createDisplayEmployeesInList(retrieveMenu);
		
		// Create a menu option to exit the program.
		createExitMenuOption(retrieveMenu, "Return to main menu.");
	}
	
	// This method builds the menu used to compare employees
	private void buildComparisonMenu(){
		
		// Set the greeting message for this menu
		comparisonMenu.setDisplayMessage("This menu is used to compare information between sales people.\n"
				+ "Please select an option from below:");
		
		// Create a menu option to view the current salesperson's name
		createViewCurrentEmployeeName(comparisonMenu);
		
		// Create a menu option to view all sales people currently in the comparision list
		initializeTempMenuItem("View all sales people currently in the comparison list.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.displayAllEmployeesInComparisonList();
				}
			});
		comparisonMenu.addComponent(tempItem);
		
		// Create a menu option for adding a salesperson to the working list
		initializeTempMenuItem("Add the current salesperson to the comparison list.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.addEmployeeToWorkingList();
				}
			});
		comparisonMenu.addComponent(tempItem);
		
		// Create a menu option for removing a salesperson from the the workinglist
		initializeTempMenuItem("Remove the current salesperson from the comparison list.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.removeEmployeeFromWorkingList();
				}
			});
		comparisonMenu.addComponent(tempItem);
		
		// Create a menu option to compare all sales people in the working list
		initializeTempMenuItem("Compare all sales people in the comparison list");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.compareWorkingList();
				}
			});
		comparisonMenu.addComponent(tempItem);
		
		// Create a menu option for accessing a salesperson from the employee list
		createAccessEmployeeMenuOption(comparisonMenu);
		
		// Create a menu option for displaying all sales people currently in the employee list
		createDisplayEmployeesInList(comparisonMenu);
		
		// Create a menu option to exit the program.
		createExitMenuOption(comparisonMenu, "Return to main menu.");
	}
	
	// This method build the menu used to present and additional functions a user might need
	public void buildAdditionalFunctionsMenu(){
		
		// Set the greeting message for this menu
		additionalFunctionsMenu.setDisplayMessage("This menu provides additional features to manage"
				+ "a list of sales people.\nPlease select an option from below:");
		
		// Create a menu option to view the current salesperson's name
		createViewCurrentEmployeeName(additionalFunctionsMenu);
		
		// Create a menu option for removing sales people from the employee list
		initializeTempMenuItem("Remove salesperson from the employee list.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.removeEmployee();
				}
			});
		additionalFunctionsMenu.addComponent(tempItem);
		
		// Create a menu option for accessing a salesperson from the employee list
		createAccessEmployeeMenuOption(additionalFunctionsMenu);
		
		// Create a menu option for displaying all sales people currently in the employee list
		createDisplayEmployeesInList(additionalFunctionsMenu);
		
		// Create a menu option to exit the program.
		createExitMenuOption(additionalFunctionsMenu, "Return to main menu.");
	}
	
	// This method factors out the repeated need to get the current employees name
	private void createViewCurrentEmployeeName(BasicMenu menu){
		
		initializeTempMenuItem("View the current salesperson's name.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployeeFullName();
				}
			});
		menu.addComponent(tempItem);
	}
	
	// This method factors out the repeated need to get an employee from the employee list
	private void createAccessEmployeeMenuOption(BasicMenu menu){
		
		initializeTempMenuItem("Retrieve salsperson from employee list.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.getCurrentEmployee();
				}
			});
		menu.addComponent(tempItem);
	}
	
	// This method factors out the repeated need to display all employees in the employee list
	private void createDisplayEmployeesInList(BasicMenu menu){
		
		initializeTempMenuItem("Display all sales people in the employee list.");
		tempItem.addComponent(new
			MenuAction(){
				public void executeAction(){
					listManager.displayAllEmployeesInEmployeeList();
				}
			});
		menu.addComponent(tempItem);
	}
	
	// This method factors out the repeated need to exit menus
	private void createExitMenuOption(BasicMenu menu, String message){
		
		initializeTempMenuItem(message);
		menu.addComponent(tempItem);
	}
	
	// This method factors out the initialization of menu items
	private void initializeTempMenuItem(String message){
		
		tempItem = new BasicMenuItem();
		tempItem.setDisplayMessage(message);
	}

}

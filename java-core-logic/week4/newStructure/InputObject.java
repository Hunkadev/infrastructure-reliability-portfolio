/* *****************************************************************************************
 * Author: Christian Hunkus
 * Date: 8/30/2015
 * Class: UoP PRG 420
 * Instructor:	David Frank
 * 
 * Summary of this module:
 * 		This module defines an interface for input strategies. Concrete strategy
 * 		implementations can be created by adhering to this interface. This will
 * 		the programmer to define how input works, as well as various input methods.
 ******************************************************************************************/

package week4.newStructure;  // Just my sorting mechanism, can be deleted.

public interface InputObject {
	
	public boolean setInputObject(Object inputReader);
	public double getDoubleInput(String message);
	public long getLongInput(String message);
	public int getIntegerInput(String message);
	public String getStringInput(String message);
	public String getAlphabeticStringInput(String message);
	public char getCharInput(String message);
	public char getAlphabeticCharInput(String message);

}

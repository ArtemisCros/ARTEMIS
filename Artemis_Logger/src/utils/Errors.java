package utils;

/*
 * Author : Olivier Cros
 * Error codes used for logging
 */

public class Errors {
	public static final int ERROR_GENERIC					= 10000;
	
	/* logger errors */ 
	public static final int ERROR_XML_CREATION 			= 10100;
	public static final int ERROR_XML_SAVE 				= 10101;
	public static final int ERROR_XML_SAVE_TRANSFORMER 	= 10102;
	public static final int ERROR_FILE_NOT_FOUND				= 10103;
	
	/* Machine errors */
	public static final int ERROR_CREATING_LINK 			= 10200;
	public static final int ERROR_CREATING_MACHINE 		= 10201;
	
	/*Scheduling errors */
	public static final int NULL_SCHEDULER_AT_LAUNCH		= 10300;

	/*XML Parser errors  */
	public static final int WCET_NOT_AN_INT				= 10400;
	public static final int ERROR_CREATING_MESSAGE		= 10401;
	public static final int FAIL_CREATING_NETWORK			= 10402;
}

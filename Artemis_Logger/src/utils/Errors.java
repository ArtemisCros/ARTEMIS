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
	public static final int ERROR_FILE_NOT_FOUND		= 10103;
	public static final int ERROR_DIR_NOT_CREATED		= 10104;
	
	/* Machine errors */
	public static final int ERROR_CREATING_LINK 			= 10200;
	public static final int ERROR_CREATING_MACHINE 		= 10201;
	
	/*Scheduling errors */
	public static final int NULL_SCHEDULER_AT_LAUNCH		= 10300;
	public static final int NULL_POLICY						= 10301;

	/*XML Parser errors  */
	public static final int WCET_NOT_AN_INT				= 10400;
	public static final int ERROR_CREATING_MESSAGE		= 10401;
	public static final int FAIL_CREATING_NETWORK		= 10402;
	public static final int CREATED_MESSAGE_NULL		= 10403;
	
	/* Ethernet errors */
	public static final int BAD_MAC						= 10500;
	public static final int BAD_MAC_SRC					= 10501;
	
	/* Simulation errors */
	public static final int ERROR_CREATING_MSG			= 10601;
	public static final int ERROR_COMPUTING_PATH		= 10602;
	
	/* Grapher errors */
	public static final int ERROR_MALFORMED_MSG_ID		= 11000;
	public static final int ERROR_XML_CONFIG_GRAPHER	= 11001;
}

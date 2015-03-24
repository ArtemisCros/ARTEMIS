package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import utils.Errors;

public class GlobalLogger extends Logger {
	public final static boolean DEBUG_ENABLED = false;
	public final static int LOGLEVEL = 5;
	
	public static int log(String message) {
		if(LOGLEVEL >= 4) {
			GlobalLogger.log(LogLevel.INFO, message);
		}
		return 0;
	}
	
	public static int error(String message) {	
		GlobalLogger.error(Errors.ERROR_GENERIC, message);
		
		return 0;
	}

	public static int display(String message) {	
		System.out.print(message);
		
		return 0;
	}
	
	public static int error(int errorCode, String message) {
		if(LOGLEVEL >= 2) {
			GlobalLogger.log(LogLevel.ERROR, " "+errorCode+" "+message);
		}
		return 0;
	}
	
	public static int warning(String message) {
		if(LOGLEVEL >= 3) {
			GlobalLogger.log(LogLevel.WARNING, message);
		}
		return 0;
	}
	
	public static int debug(String message) {
		if(LOGLEVEL >= 5) {
			GlobalLogger.log(LogLevel.DEBUG, message);
		}
		return 0;
	}
	
	public static int fatal(String message) {
		if(LOGLEVEL >= 1) {
			GlobalLogger.log(LogLevel.FATAL, message);
		}
		
		
		return 0;
	}
	
	public static int log(final LogLevel level, String message) {	
		String messageType = "";
		
		if(level == LogLevel.FATAL) {
			messageType = "FATAL";
		}
		if(level == LogLevel.ERROR) {
			messageType = "ERROR";
		}
		if(level == LogLevel.WARNING) {
			messageType = "WARNING";
		}
		if(level == LogLevel.INFO) {
			messageType = "INFO";
		}
		if(level == LogLevel.DEBUG) {
			messageType = "DEBUG";
		}
		
		
		Date maDateAvecFormat=new Date();
		SimpleDateFormat dateStandard = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss"); 
		
		message = "LOG:"+dateStandard.format(maDateAvecFormat)+"\t"+messageType+":\t"+message;
		
		new Logger().write(message);
		
		return 0;
	}
}

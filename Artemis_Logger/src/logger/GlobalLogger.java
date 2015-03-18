package logger;

import java.text.SimpleDateFormat;
import java.util.Date;

import utils.Errors;

public class GlobalLogger extends Logger {
	public final static boolean DEBUG_ENABLED = true;
	
	public static int log(String message) {
		GlobalLogger.log(LogLevel.INFO, message);
		
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
	
	public static int error(int error_code, String message) {
		GlobalLogger.log(LogLevel.ERROR, " "+error_code+" "+message);
		
		return 0;
	}
	
	public static int warning(String message) {
		GlobalLogger.log(LogLevel.WARNING, message);
		
		return 0;
	}
	
	public static int debug(String message) {
		GlobalLogger.log(LogLevel.DEBUG, message);
		
		return 0;
	}
	
	public static int fatal(String message) {
		GlobalLogger.log(LogLevel.FATAL, message);
		
		return 0;
	}
	public static int log(LogLevel level, String message) {	
		String messageType = "";
		
		if(level == LogLevel.DEBUG) {
			messageType = "DEBUG";
		}
		if(level == LogLevel.ERROR) {
			messageType = "ERROR";
		}
		if(level == LogLevel.FATAL) {
			messageType = "FATAL";
		}
		if(level == LogLevel.INFO) {
			messageType = "INFO";
		}
		if(level == LogLevel.WARNING) {
			messageType = "WARNING";
		}
		
		Date maDateAvecFormat=new Date();
		SimpleDateFormat dateStandard = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss"); 
		
		message = "LOG:"+dateStandard.format(maDateAvecFormat)+"\t"+messageType+":\t"+message;
		new Logger().write(message);
		
		return 0;
	}
}

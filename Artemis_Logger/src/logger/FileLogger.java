package logger;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import utils.Errors;

public class FileLogger extends Logger {
	
	public static int logToFile(String message, String fileName) {
		try {
			File file = new File(fileName);
 
			if (!file.exists()) {
				file.createNewFile();
			}
 
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(message);
			bw.close();
		} catch (IOException e) {
			//TODO Error Log
			GlobalLogger.error(Errors.ERROR_FILE_NOT_FOUND, "File not found:"+fileName+", can't create log");
			e.printStackTrace();
		}
		
		return 0;
	}
	
	

}

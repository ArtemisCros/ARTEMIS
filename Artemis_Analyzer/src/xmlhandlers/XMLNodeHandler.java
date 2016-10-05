package xmlhandlers;

import java.util.HashMap;

import logger.GlobalLogger;
import main.Message;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public class XMLNodeHandler extends DefaultHandler {
	public HashMap<String, Message> messagesList;
	
	public XMLNodeHandler() {
		messagesList = new HashMap<String, Message>();
	}
	
	public void startElement(final String uri, final String name, final String qualif, final Attributes pAttr) {
		if(qualif.equals("timer")) {
			
			if(pAttr.getValue("message") != null) {
				String message = pAttr.getValue("message");
				String time = pAttr.getValue("value");
				
				if(messagesList.get(message) == null) {
					/* If we find a message for the first time, it is its emission instant */
					Message msg = new Message();
					msg.emissionInstant = Double.parseDouble(time);
					messagesList.put(message, msg);
				}
				else {
					/* Else, we increment its size */
					messagesList.get(message).receptionInstant 
						= Double.parseDouble(time)+1;
				}
			}
		}
	}
}

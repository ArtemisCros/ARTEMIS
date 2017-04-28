package xmlhandlers;

import java.util.ArrayList;
import java.util.HashMap;

import main.Message;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import logger.GlobalLogger;

public class XMLNodeHandler extends DefaultHandler {
	public HashMap<String, Message> messagesList;
	
	public ArrayList<Double> multicastDelays;
	
	public XMLNodeHandler() {
		messagesList = new HashMap<String, Message>();
		multicastDelays = new ArrayList<Double>();
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
		
		if(qualif.equals("critswitch")) {
			String mltdel = pAttr.getValue("mltdel");
			String time = pAttr.getValue("value");
			
			multicastDelays.add(Double.parseDouble(mltdel));
		}
	}
}

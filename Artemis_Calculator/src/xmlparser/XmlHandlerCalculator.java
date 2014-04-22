package xmlparser;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

/*
 * Non-preemptive build
 */
public class XmlHandlerCalculator extends DefaultHandler{
	public String idSearchedMsg;
	public int startTime;
	public int endTime;
	public boolean messageTrigger;
	
	public XmlHandlerCalculator() {
		messageTrigger = false;
		endTime = -1;
		startTime = -1;
	}
	
	public void startElement(String uri, String name, String qualif, Attributes at) {
		String idMsg = "";
		int timeTrigger = 0;
		
		if(qualif == "timer") {
			/* Timer tag */		
			
			for(int cptAttr=0;cptAttr < at.getLength();cptAttr++) {
				if(at.getLocalName(cptAttr) == "value") {
					timeTrigger = Integer.parseInt(at.getValue(cptAttr));
				}	
				/* If current message is the one we want, we save its start value */
				if(at.getLocalName(cptAttr) == "message") {
					 idMsg = at.getValue(cptAttr);
				}	
			}
			/* If it's the first time we see the idMsg, we save the time */
			if(idSearchedMsg.compareTo(idMsg) == 0 && !messageTrigger) {
				messageTrigger = true;
				startTime = timeTrigger;
			}
			else if(messageTrigger && endTime == -1 && !(idSearchedMsg.compareTo(idMsg) == 0)) {
				/* If it's the first time idMsg is not correct after period, we save the time */
				endTime = timeTrigger;
			}
		}
	}
	
	public void endElement(String uri, String name, String qName) {		
		
	}
}

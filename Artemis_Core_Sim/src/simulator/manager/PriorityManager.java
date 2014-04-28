package simulator.manager;

import java.util.Vector;

import root.elements.network.modules.task.Message;
import root.util.constants.ConfigConstants;
import root.util.tools.PriorityPolicy;

public class PriorityManager {
	public PriorityManager() {
		
	}
	
	public Message getNextMessage(Vector<Message> buffer) {
		Message rstMessage = null;
		/*FIFO method */
		if(ConfigConstants.PRIORITY_POLICY == PriorityPolicy.FIFO) {
			rstMessage = buffer.firstElement();
		}
		
		if(ConfigConstants.PRIORITY_POLICY == PriorityPolicy.FIFOISOLATED) {
			for(int cpt_buffer=0;cpt_buffer<buffer.size();cpt_buffer++) {
				if(rstMessage == null || buffer.get(cpt_buffer).timerArrival < rstMessage.timerArrival) {
					rstMessage = buffer.get(cpt_buffer);
				}
				else if((buffer.get(cpt_buffer).timerArrival == rstMessage.timerArrival) && 
					!buffer.get(cpt_buffer).isObserved()){
					rstMessage = buffer.get(cpt_buffer);
				}
			}
		}
		
		if(ConfigConstants.PRIORITY_POLICY == PriorityPolicy.FIXEDPRIORITY) {
			for(int cpt_buffer=0;cpt_buffer<buffer.size();cpt_buffer++) {
				if(rstMessage == null || buffer.get(cpt_buffer).priority.get(0) < rstMessage.priority.get(0)) {
					rstMessage = buffer.get(cpt_buffer);
				}
			}
		}
		return rstMessage;
	}
}

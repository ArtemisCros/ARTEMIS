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
		int cptBuffer = 0;
		
		/*FIFO method */
		if(ConfigConstants.PRIORITY_POLICY == PriorityPolicy.FIFO) {
			rstMessage = buffer.firstElement();
		}
		
		if(ConfigConstants.PRIORITY_POLICY == PriorityPolicy.FIFOISOLATED) {
			for(cptBuffer=0;cptBuffer<buffer.size();cptBuffer++) {
				if(rstMessage == null || buffer.get(cptBuffer).timerArrival < rstMessage.timerArrival) {
					rstMessage = buffer.get(cptBuffer);
				}
				else if((buffer.get(cptBuffer).timerArrival == rstMessage.timerArrival) && 
					!buffer.get(cptBuffer).isObserved()){
					rstMessage = buffer.get(cptBuffer);
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
		
		if(ConfigConstants.PRIORITY_POLICY == PriorityPolicy.FIFOSTAR) {
			/* We search the lower activation instant */
			int activationInstantMin = -1;
			
			for(cptBuffer=0;cptBuffer < buffer.size();cptBuffer++) {
				if((activationInstantMin == -1 ) || (activationInstantMin > buffer.get(cptBuffer).nextSend)) {
					rstMessage = buffer.get(cptBuffer);
					activationInstantMin = buffer.get(cptBuffer).nextSend;
				}
			}
		}
		return rstMessage;
	}
}

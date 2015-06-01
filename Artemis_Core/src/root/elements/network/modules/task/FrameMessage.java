package root.elements.network.modules.task;

public abstract class FrameMessage extends AbstractMessage{

	public FrameMessage(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 *  Size of the message in bytes */
	public double size;
}

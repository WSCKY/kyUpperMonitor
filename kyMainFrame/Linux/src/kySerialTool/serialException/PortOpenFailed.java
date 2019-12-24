package kySerialTool.serialException;

public class PortOpenFailed extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortOpenFailed() {}

	@Override
	public String toString() {
		return "failed to open this port!";
	}
}

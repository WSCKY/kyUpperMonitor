package kySerialTool.serialException;

public class PortNotOpen extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public PortNotOpen() {}

	@Override
	public String toString() {
		return "port does not open.";
	}
}

package CommTool.exception;

public class SendDataFailure extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SendDataFailure() {}

	@Override
	public String toString() {
		return "error while sending data!";
	}
	
}

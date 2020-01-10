package CommTool.exception;

public class ReadDataFailure extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ReadDataFailure() {}

	@Override
	public String toString() {
		return "error while reading data!";
	}
	
}

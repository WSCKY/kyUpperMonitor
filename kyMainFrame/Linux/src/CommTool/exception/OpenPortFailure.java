package CommTool.exception;

public class OpenPortFailure extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String err_str = null;
	public OpenPortFailure(String err) {
		this.err_str = err;
	}

	@Override
	public String toString() {
		return "error while open port! (" + this.err_str + ")";
	}
}

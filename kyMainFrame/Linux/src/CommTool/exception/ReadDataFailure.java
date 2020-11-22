package CommTool.exception;

public class ReadDataFailure extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String err_str = null;
	public ReadDataFailure() {}
	public ReadDataFailure(String err) {
		this.err_str = err;
	}

	@Override
	public String toString() {
		if(this.err_str != null) {
			return "error while reading data! (" + this.err_str + ")";
		}
		return "error while reading data!";
	}
	
}

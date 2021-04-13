package CommTool.exception;

public class OpenPortFailure extends Exception {
	private static final long serialVersionUID = 1L;

	@Override
	public String toString() {
		return "error while open port!";
	}
}

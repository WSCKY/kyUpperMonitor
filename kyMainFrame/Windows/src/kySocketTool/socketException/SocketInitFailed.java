package kySocketTool.socketException;

public class SocketInitFailed extends Exception {
	private static final long serialVersionUID = 1L;

	public SocketInitFailed() {}

	public String toString() {
		return "Socket Init Failed!";
	}
}

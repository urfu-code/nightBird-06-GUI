package wood01;

public class WoodException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String message;
	
	public WoodException(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}

}

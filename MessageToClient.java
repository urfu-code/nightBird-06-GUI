import java.io.Serializable;


public class MessageToClient implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Action action;
	private String name;

	public MessageToClient(Action act) {
		setAction(act);
	}
	
	public MessageToClient(String name) {
		setName(name);
	}

	public Action getAction() {
		return action;
	}
	public void setAction(Action act) {
		this.action = act;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}

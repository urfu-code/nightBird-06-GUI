import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class Client extends Close {
	private static Socket socket;
	private static ObjectInputStream in;
	private static ObjectOutputStream out;
	public static void main(String[] args) throws Exception {
		Response m_response;
		Request m_request;
		Random random = new Random();
		StringBuffer name = new StringBuffer();		
		try {
			for (int i = 0; i < 6; i++) {
				name.append((char)Math.abs(random.nextInt(128)));
			}
			m_request = new Request(name.toString());
			MyMouse Minni = new MyMouse(name.toString());  
			socket = new Socket("localhost", 14306);
			out = new ObjectOutputStream(socket.getOutputStream());
			in =  new ObjectInputStream(socket.getInputStream());
			out.writeObject(m_request);
			out.flush();
			Action currentAction = Action.Ok;
			while (true) {
				if (currentAction == Action.Finish || currentAction == Action.Finish) {
					break;
				}
				Direction direction = Minni.NextMove(currentAction);
				Request message = new Request(name.toString(), direction);
				out.writeObject(message);
				out.flush();
				try {			
					m_response = (Response) in.readObject();
					currentAction = m_response.GetAction();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			tryClose(socket);
			tryClose(out);
			tryClose(in);
		}
	}
}


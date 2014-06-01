import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ThreadsServer extends Close implements Runnable  {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	private PrintableWood m_wood;
	private Request m_request;
	private Response m_response;
	private Point startPoint;
	private Point finishPoint;
	private Synchronizer sync;
	
	public ThreadsServer(Socket s, PrintableWood wood, Point point1, Point point2, Synchronizer synchronizer) {
		socket = s;
		m_wood = wood;
		startPoint = point1;
		finishPoint = point2;
		sync = synchronizer;
		try {
			in = new ObjectInputStream(socket.getInputStream());
		} catch (IOException e1) {

			e1.printStackTrace();
		}
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		Action currentAction = Action.Ok;
		try {
			while ((currentAction != Action.Finish) && (currentAction != Action.WoodmanNotFound)) {
				m_request = (Request) in.readObject();
				switch (m_request.GetMethod()) {
				case "CreateWoodman" :
				{		
					m_wood.createWoodman(m_request.GetName(), startPoint, finishPoint);				
					break;
				}
				case "MoveWoodman" :
				{
					synchronized (sync) {
						sync.wait();
					}
					synchronized (m_wood) {
						currentAction = m_wood.move(m_request.GetName(), m_request.GetDirection());
					}
					if (currentAction == Action.WoodmanNotFound) System.out.println("Mouse didn't reach finish");
					if (currentAction == Action.Finish) System.out.println("Mouse reached finish");
					m_response = new Response(currentAction);
					out.writeObject(m_response);
					out.flush();
					break;
				}
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			tryClose(in);
			tryClose(out);
			tryClose(socket);
		}
	}
}

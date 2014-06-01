import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;


public class Handler implements Runnable {

	private Socket socket;
	private Point start;
	private Point finish;
	private Wood pWood; 
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	private volatile ConcurrentHashMap<String, Thread> listOfClients;
	private String threadID; // он же - имя игрока

	public Handler(Socket socket, Wood wood, Point start, Point finish, ConcurrentHashMap<String, Thread> listOfClients, String threadID) {
		this.socket = socket;
		this.pWood = wood;
		this.start = start;
		this.finish = finish;
		this.listOfClients = listOfClients;
		this.threadID = threadID;
	}

	@Override
	public void run() {
		Action act = Action.Ok;

		MessageToClient giveMeAName = new MessageToClient(threadID);
		try { // посылаем клиенту имя, так как теперь имя вводится не при запуске клиента
			oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
			oos.writeObject(giveMeAName);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			ois = new ObjectInputStream(new BufferedInputStream(socket.getInputStream()));	

			do {		
				MessageToServer recieved = (MessageToServer) ois.readObject();
				synchronized (listOfClients.get(threadID)) {						
					listOfClients.get(threadID).wait();
				}
				switch (recieved.getMethodName()) {
				case "createWoodman":
					synchronized (pWood) { 
						pWood.createWoodman(recieved.getWoodmanName(), start, finish);
					}
					break;
				case "move":					
					synchronized (pWood) {
						act = pWood.move(recieved.getWoodmanName(), recieved.getDirection());
					}
					//TODO синглтон
//					oos = new ObjectOutputStream(new BufferedOutputStream(socket.getOutputStream()));
					MessageToClient toSend = new MessageToClient(act);					
					oos.writeObject(toSend);
					oos.flush();				
					break;
				}
			} while (act != Action.WoodmanNotFound && act != Action.Finish);

			synchronized (listOfClients) {
				if (act == Action.WoodmanNotFound || act == Action.Finish) {
					listOfClients.remove(threadID);
				}
			}
		} 

		catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (ois != null) {
					ois.close();
				}
				if (oos != null) {
					oos.close();
				}
				if (socket != null) {
					socket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}

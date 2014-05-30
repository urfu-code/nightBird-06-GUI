import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;


public class ServerThread implements Runnable{
	private ObjectInputStream inStream;
	private ObjectOutputStream outStream;
	private Socket socket;
	private PrintableWood wood;
	private Point points_s;
	private Point points_f;
	private Synchronizer mark;

	public ServerThread(Socket socket, PrintableWood wood,  Point points_s, Point points_f, Synchronizer mark) throws IOException {
		this.socket = socket;
		this.wood = wood;
		this.points_s = points_s;
		this.points_f = points_f;
		this.mark = mark;
		inStream = new ObjectInputStream(socket.getInputStream());
		outStream = new ObjectOutputStream(socket.getOutputStream());
	}

	@Override
	public void run() {
		Action action = Action.Ok;
		try {
			while (action != Action.WoodmanNotFound && action != Action.Finish) {
				MessageServer messageServer = (MessageServer)inStream.readObject();
				switch (messageServer.getMethod()) {
				case "createWoodman" :
					wood.createWoodman(messageServer.getName(), points_s, points_f);
					break;
				case "move" :
					synchronized (mark) {
						mark.wait();
					}
					synchronized (wood) {
						action = wood.move(messageServer.getName(), messageServer.getDirection());
					}
					
					if(action == Action.WoodmanNotFound) System.out.println("The player died.");
					if(action == Action.Finish) System.out.println("The player won.");
					MessageClient messageClient = new MessageClient(action);
					outStream.writeObject(messageClient);
					outStream.flush();
					break;
				}
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			closeObject(inStream);
			closeObject(outStream);	
			closeObject(socket);
		}
	}
	public static void closeObject(Closeable object){
		if (object != null){
			try{
				object.close();
			} catch(IOException ex){
				ex.printStackTrace();
			}
		}
	}
}
	
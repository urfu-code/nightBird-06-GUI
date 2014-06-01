import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Close {
	private static ServerSocket server;
	private static InputStream instream;
	Request m_request;
	Response m_response;
	volatile static ConcurrentHashMap <Integer, Thread> clients = new ConcurrentHashMap<Integer, Thread>();
	private static LinkedList<Point> startPoints = new LinkedList<Point>();
	private static LinkedList<Point> finishPoints = new LinkedList<Point>();
	public static void main(String[] args) throws CodeException, IOException {				
		startPoints.add(new Point(1,1));	
		finishPoints.add(new Point(1,2));
		try {
			PrintableWoodLoader W = new PrintableWoodLoader();
			PrintableWood wood = W.PrintableWoodLoad(instream,System.out);	
			server = new ServerSocket(14306);	
			Synchronizer notify = new Synchronizer(wood);
			Thread thread;
			thread = new Thread(new ThreadsServer(server.accept(), wood, startPoints.get(Math.abs(new Random().nextInt(startPoints.size()))), finishPoints.get(Math.abs(new Random().nextInt(finishPoints.size()))), notify));
			thread.start();
			thread = new Thread(notify);
			thread.start();
			while (true) {
				thread = new Thread(new ThreadsServer(server.accept(), wood, startPoints.get(Math.abs(new Random().nextInt(startPoints.size()))), finishPoints.get(Math.abs(new Random().nextInt(finishPoints.size()))), notify));
				thread.start();
			}
		}catch (IOException e) {
			e.printStackTrace();
		} catch(Exception e){
			tryClose(server);
			System.exit(0);
		} finally {
			tryClose(server);
		}
	}
}


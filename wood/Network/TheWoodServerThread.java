package Network;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

import wood01.Action;
import wood01.Point;
import wood01.PrintableTheWood;
import wood01.WoodException;

public class TheWoodServerThread implements Runnable {
	
	private ObjectInputStream iStream = null;
	private ObjectOutputStream oStream = null;
	private Socket fromClient = null;
	private PrintableTheWood wood;
	private MouseRequest request;
	private WoodResponse response;
	private Point startPoint;
	private Point endPoint;
	private TheWoodServerThreadSyncronizer monitor;
	private HashMap<String,Integer>leaders;
	private ArrayList<Point>startPoints;
	private ArrayList<Point>endPoints;
	
	public TheWoodServerThread(Socket socket, PrintableTheWood wood,Point startPoint,Point endPoint, TheWoodServerThreadSyncronizer monitor, HashMap<String,Integer> leaders, ArrayList<Point> startPoints, ArrayList<Point> endPoints) throws IOException {
		fromClient = socket;
		this.startPoints = startPoints;
		this.endPoints = endPoints;
		this.wood = wood;
		this.monitor = monitor;
		this.startPoint = startPoint;
		this.endPoint = endPoint;
		this.leaders = leaders;
		try {
			iStream = new ObjectInputStream(fromClient.getInputStream());
			oStream = new ObjectOutputStream(fromClient.getOutputStream());
		} catch (IOException e) {
			System.out.println("����� �����..");
		}
	}
	
	@Override
	public void run() {
		Action currentAction = Action.Ok;
		int moveCounter = 0;
		try {
			while (currentAction != Action.WoodmanNotFound && currentAction != Action.ExitFound) {
				request = (MouseRequest) iStream.readObject();
				if (request.getRequestType().equals("create")) {
					wood.createWoodman(request.getName(), startPoint, endPoint);
				}
				else if (request.getRequestType().equals("move")) {
					moveCounter++;
					currentAction = wood.move(request.getName(), request.getDirection());
				}
				synchronized(monitor) {
					monitor.wait();
				}
				response = new WoodResponse(currentAction);
				oStream.writeObject(response);
				oStream.flush();
			}
			if (currentAction == Action.WoodmanNotFound) {
				System.out.println("���� " + request.getName() + " �������, ������ " + moveCounter + "�����(�).");
			}
			else {
				if (!leaders.containsKey(request.getName())) {
					leaders.put(request.getName(), new Integer(moveCounter));
				}
				else if (leaders.get(request.getName()) > moveCounter) {
					leaders.remove(request.getName());
					leaders.put(request.getName(), moveCounter);
				}
				System.out.println("����� " + request.getName() + " ����� �����!");
			}
		} catch (WoodException e) {
			System.out.println("��������� ����� ��� ��� �������");
			if (e.getMessage().equals("WoodmanInWall") || e.getMessage().equals("isNotAWood")) {
				startPoints.remove(startPoint);
			}
			else if(e.getMessage().equals("IncorrectFinish")) {
				endPoints.remove(endPoint);
			}
		} catch (IOException e) {
			System.out.println("������ �����");
		} catch (Exception e) {
			System.out.println("������ � �����");
		}
		finally {
			try {
				if (iStream != null) {
					iStream.close();
				}
				if (oStream != null) {
					oStream.close();
				}
				if (fromClient != null) {
					fromClient.close();
				}
			}
			catch (IOException e) {
				System.out.println("�� �� ���������� �������!..");
			}
		}
	}

}

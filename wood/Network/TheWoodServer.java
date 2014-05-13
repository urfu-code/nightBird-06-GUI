package Network;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;



import java.util.HashMap;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

import wood01.Point;
import wood01.PrintableTheWood;
import wood01.TheWoodLoader;

public class TheWoodServer {
	private static JButton startButton;
	private static JButton addStartPointButton;
	private static JButton addFinishPointButton;
	private static JComboBox<String> woodSelector;
	private static ArrayList<Point>endPoints;
	private static ArrayList<Point>startPoints;
	private static HashMap<String,Integer> leaders;
	
	public static void main(String[] Args) {
		ServerSocket server = null;
		BufferedReader reader = null;
		BufferedWriter writer = null;
		Thread thread;
	    startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		leaders = new HashMap<String,Integer>();
		TheWoodLoader loader = new TheWoodLoader();
		try {
			server = new ServerSocket(6789);
			try {
				JFrame GUI = new JFrame();
				createWindow(GUI);
				final Object syncObj = new Object();
				startButton.addActionListener(new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						
						if (startButton.getText().equals("Начать")) {
							synchronized(syncObj) {
								syncObj.notify();
							}
							startButton.setText("Завершить");
						}
						else {
							System.exit(0);
						}
					}
				});
				synchronized (syncObj) {
					syncObj.wait();
				}
				PrintableTheWood wood = (PrintableTheWood) loader.Load(new FileInputStream(new File( (String) woodSelector.getSelectedItem())),System.out);
				wood.paintWood(GUI,leaders);
				System.out.println("Server started! Waiting for connection...");
				TheWoodServerThreadSyncronizer sync = new TheWoodServerThreadSyncronizer(wood);
				Thread threadSync = new Thread(sync);
				threadSync.start();
				int startPointChooserCounter = 0;
				int endPointChooserCounter = 0;
				while (true) {
					try {
						thread = new Thread(new TheWoodServerThread(server.accept(), wood,startPoints.get(startPointChooserCounter),endPoints.get(endPointChooserCounter) , sync, leaders));
						thread.start();
						startPointChooserCounter++;
						endPointChooserCounter++;
						if (startPointChooserCounter >= startPoints.size()) {
							startPointChooserCounter = 0;
						}
						if (endPointChooserCounter >= endPoints.size()) {
							endPointChooserCounter = 0;
						}
					}
					catch (SocketTimeoutException e) {
						continue;
					}
				}
			}
			catch (ClassNotFoundException e) {
				System.out.println("тестовая беда");
			}
			catch (IOException e) {
				System.out.println("всё, хватило с вудКлиента пакетов");
				e.printStackTrace();
			}
			catch (Exception e) {
				e.printStackTrace();
				System.out.println("беда с лесом");
			}
			finally {
				if (server != null) {
					server.close();
				}
				if (reader != null) {
					reader.close();
				}
				if (writer != null) {
					writer.close();
				}
			}
		}
		catch (IOException e) {
			System.out.println("port is busy");
			System.exit(-1);
		}


	}

	private static void createWindow(JFrame GUI) {
//		Бокс с выбором леса 
		woodSelector = new JComboBox<String>();
		try {
			File projectDir = new File(new File(".").getAbsolutePath());
			String[] woods = projectDir.list();
			Pattern txtFile = Pattern.compile("[A-Za-z0-9-]+\\.txt");
			Matcher matcher;
			for (int i = 0; i < woods.length; i++) {
				matcher = txtFile.matcher(woods[i]);
				if (matcher.find()) {
					woodSelector.addItem(woods[i]);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Файлы леса не загружены");
		}
//		Кнопка старта игры
		startButton = new JButton("Начать");
//		Кнопка добавления точки
		addStartPointButton = new JButton("Добавить начало");
		addFinishPointButton = new JButton("Добавить финиш");
		final JTextArea pointsArea = new JTextArea("Точки: \r\n");
		pointsArea.setEditable(false);
		final JScrollPane points = new JScrollPane(pointsArea);
		final JTextArea xAreaStart = new JTextArea();
		xAreaStart.setPreferredSize(new Dimension(20,14));
		final JTextArea yAreaStart = new JTextArea();
		yAreaStart.setPreferredSize(new Dimension(20,14));
		final JTextArea xAreaFinish = new JTextArea();
		xAreaFinish.setPreferredSize(new Dimension(20,14));
		final JTextArea yAreaFinish = new JTextArea();
		yAreaFinish.setPreferredSize(new Dimension(20,14));
		addStartPointButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					Scanner	scannerX = new Scanner(xAreaStart.getText());
					Scanner scannerY = new Scanner(yAreaStart.getText());
					Point point = new Point(scannerX.nextInt(),scannerY.nextInt());
					if (startPoints.contains(point)) {
						addStartPointButton.setText("Уже добавлена");
					}
					else {
						startPoints.add(point);
						scannerX.close();
						scannerY.close();
						pointsArea.append("Старт: " + startPoints.get(startPoints.size() - 1).toString() + "\r\n");
						points.revalidate();
						addStartPointButton.setText("Добавить точку");
					}
				} catch (Exception e) {
					addStartPointButton.setText("Не удалось");
				}
			}
		});
		addFinishPointButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Scanner	scannerX = new Scanner(xAreaFinish.getText());
					Scanner scannerY = new Scanner(yAreaFinish.getText());
					Point point = new Point(scannerX.nextInt(),scannerY.nextInt());
					if (endPoints.contains(point)) {
						addFinishPointButton.setText("Уже добавлена");
					}
					else {
						endPoints.add(point);
						scannerX.close();
						scannerY.close();
						pointsArea.append("Финиш: " + endPoints.get(endPoints.size() - 1).toString() + "\r\n");
						points.revalidate();
						addFinishPointButton.setText("Добавить точку");
					}
				} catch (Exception e1) {
					addFinishPointButton.setText("Не удалось");
				}
			}
		});
//		Пихаем всё в наш фрэйм
//		Создаём слой
		GridBagLayout ourLayout = new GridBagLayout();
//		Создаем константы
		GridBagConstraints c = new GridBagConstraints();
//		Устанавливаем лэйаут
		GUI.setLayout(ourLayout);
		c.anchor = GridBagConstraints.NORTHWEST; 
		c.fill   = GridBagConstraints.HORIZONTAL;  
		c.gridheight = 1;
		c.gridwidth  = 4; 
		c.gridx = 0; 
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 0;
		c.ipady = 0;
		c.weightx = 0.0;
		c.weighty = 0.0;
		GUI.add(woodSelector, c);
		c.gridy = 1;
		GUI.add(startButton, c);
		c.gridy = 2;
		GUI.add(addStartPointButton, c);
		c.gridy = 3;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridwidth = 1;
		c.gridx = 0;
		GUI.add(new JLabel("X axis: "),c);
		c.gridx = 1;
		GUI.add(xAreaStart,c);
		c.gridx = 2;
		GUI.add(new JLabel("Y axis: "),c);
		c.gridx = 3;
		GUI.add(yAreaStart,c);
		c.gridy = 4;
		c.gridx = 0;
		c.gridwidth = 4;
		c.anchor = GridBagConstraints.NORTHEAST;
		GUI.add(addFinishPointButton,c);
		c.gridy = 5;
		c.gridwidth = 1;
		c.anchor = GridBagConstraints.NORTHEAST;
		GUI.add(new JLabel("X axis: "), c);
		c.gridx = 1;
		GUI.add(xAreaFinish,c);
		c.gridx = 2;
		GUI.add(new JLabel("Y axis: "),c);
		c.gridx = 3;
		GUI.add(yAreaFinish,c);
		points.setPreferredSize(new Dimension(0,210));
		points.setBorder(new LineBorder(Color.YELLOW));
		c.gridy = 6;
		c.gridwidth = 4;
		c.gridheight = GridBagConstraints.RELATIVE;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		GUI.add(points,c);
		GUI.setTitle("Добро пожаловать в Night Bird Game!");
		GUI.setVisible(true);
		GUI.pack();
	}
}

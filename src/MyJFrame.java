
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;


public class MyJFrame {
	
	volatile static HashMap<Integer, Thread> clients = new HashMap<Integer, Thread>();
	private static JButton startButton;
	private static JButton addPointButton;
	private static JComboBox woodSelector;
	private static ArrayList<Point>endPoints;
	private static ArrayList<Point>startPoints;
	private static JPanel pointsArea;
	public static int i=1;
	public static void main(String[] Args) {
		ServerSocket server = null;
		Thread thread;
	    startPoints = new ArrayList<Point>();
		endPoints = new ArrayList<Point>();
		MyWoodLoader loader = new MyWoodLoader();
		try {
			server = new ServerSocket(25431);
			try {
				JFrame frame = new JFrame();
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				createWindow(frame);
				final Object syncObj = new Object();
				startButton.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {			
						
						if (startButton.getText().equals("Начать игру")) {
							synchronized(syncObj) {
								syncObj.notify();
							}
							startButton.setText("Игра началась");
						}
						else {
							System.exit(0);
						}
					}
				});
				synchronized (syncObj) {
					syncObj.wait();
				}
				PrintableWood wood = (PrintableWood) loader.Load(new FileInputStream(new File( (String) woodSelector.getSelectedItem())+".txt"),System.out);
				wood.paint(frame,startPoints,endPoints,pointsArea);
				System.out.println("Server started!");
				
				Synchronizer notify = new Synchronizer(wood);
				
				thread = new Thread(new ServerThread(server.accept(), wood, startPoints.get(0),endPoints.get(0), notify));
				thread.start();
				
				int startPointChooserCounter = 1;
				int endPointChooserCounter = 1;
				
				thread = new Thread(notify);
				thread.start();
				while (true) {
					thread = new Thread(new ServerThread(server.accept(), wood, startPoints.get(startPointChooserCounter),endPoints.get(endPointChooserCounter), notify));
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
			} catch (IOException e) {
				e.printStackTrace();
			} catch(Exception ter){
				closeObject(server);
				System.exit(0);
			} finally {
				closeObject(server);
			}
		}
		
		catch (IOException e) {
			System.out.println("Порт занят");
			System.exit(-1);
			}
	}

	private static void createWindow(JFrame frame) {
		String[] items = {"Brokilon", "Rampart", "Ardenwood"};		
		woodSelector = new JComboBox(items);
		startButton = new JButton("Начать игру");
		addPointButton = new JButton("Добавить точки");
		final JTextArea pointsArea = new JTextArea("Точки: \r\n");
		pointsArea.setEditable(false);
		final JScrollPane points = new JScrollPane(pointsArea);
		final JTextArea xAreaStart = new JTextArea();
		xAreaStart.setPreferredSize(new Dimension(14,11));
		final JTextArea yAreaStart = new JTextArea();
		yAreaStart.setPreferredSize(new Dimension(14,11));
		final JTextArea xAreaFinish = new JTextArea();
		xAreaFinish.setPreferredSize(new Dimension(14,11));
		final JTextArea yAreaFinish = new JTextArea();
		yAreaFinish.setPreferredSize(new Dimension(14,11));
		addPointButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					
					Point start = new Point(Integer.valueOf(xAreaStart.getText()).intValue(),Integer.valueOf(yAreaStart.getText()).intValue());
					startPoints.add(start);				
					Point finish = new Point(Integer.valueOf(xAreaFinish.getText()).intValue(),Integer.valueOf(yAreaFinish.getText()).intValue());
					endPoints.add(finish);
					pointsArea.append("№"+i+"\r\n");
					pointsArea.append("Старт: (" + startPoints.get(startPoints.size() - 1).getX() +"; "+startPoints.get(startPoints.size() - 1).getY()+")" + "\r\n");	
					pointsArea.append("Финиш: (" + endPoints.get(startPoints.size() - 1).getX() +"; "+endPoints.get(startPoints.size() - 1).getY()+")" + "\r\n");						
					points.revalidate();
					i++;
					
				} catch (Exception e) {
					pointsArea.append("Попробуйте ввести точки еще раз.");
				}
			}
		});

		GridBagLayout ourLayout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		frame.setLayout(ourLayout);
		c.anchor = GridBagConstraints.NORTHWEST; 
		c.fill   = GridBagConstraints.HORIZONTAL;  
		c.gridheight = 1;
		c.gridwidth  = 8; 
		c.gridx = 0; 
		c.gridy = 0;
		c.insets = new Insets(0, 0, 0, 0);
		c.ipadx = 5;
		c.ipady = 5;
		c.weightx = 0.0;
		c.weighty = 0.0;
		JLabel label;
		try {
			label = new JLabel(new ImageIcon(ImageIO.read(new File("game.png"))));
			frame.add(label,c);
			c.gridy = 1;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		frame.add(startButton, c);
		c.gridy = 2;
		frame.add(woodSelector, c);
		c.gridy = 3;
		frame.add(addPointButton, c);
		c.gridy = 4;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTHEAST;
		c.gridwidth = 1;
		c.gridx = 0;
		frame.add(new JLabel("Старт:  X"),c);
		c.gridx = 1;
		frame.add(xAreaStart,c);
		c.gridx = 2;
		frame.add(new JLabel(" Y"),c);
		c.gridx = 3;
		frame.add(yAreaStart,c);
		c.gridx = 4;
		frame.add(new JLabel("  Финиш:  X"), c);
		c.gridx = 5;
		frame.add(xAreaFinish,c);
		c.gridx = 6;
		frame.add(new JLabel(" Y"),c);
		c.gridx = 7;
		frame.add(yAreaFinish,c);
		c.gridx = 8;
		c.gridy = 4;
		c.gridwidth = 8;		
	
		c.anchor = GridBagConstraints.NORTHEAST;
		points.setPreferredSize(new Dimension(200,95));
		points.setBorder(new LineBorder(Color.GREEN));
		c.gridy = 1;
		c.gridwidth = 8;
		c.gridheight = GridBagConstraints.RELATIVE;
		c.gridx = 8;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		frame.add(points,c);		
		frame.setTitle("Night Bird Game");
		File mouseFile = new File("mouse_yellow.png");
		try {
			BufferedImage mouse = ImageIO.read(mouseFile);
			frame.setIconImage(mouse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		frame.setVisible(true);
		frame.pack();
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
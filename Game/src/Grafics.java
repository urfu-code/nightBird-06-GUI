import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;


public class Grafics extends Close  {
	private static ServerSocket server;
	private static JPanel pointsArea;
	private static JButton startButton;
	private static JButton addStartPoints;
	private static JButton addFinishPoints;
	private static JComboBox<String> woods;
	private static LinkedList<Point> startPoints = new LinkedList<Point>();
	private static LinkedList<Point> finishPoints = new LinkedList<Point>();

	public static void main(String[] Args) {
		Thread thread;
		try {
			server = new ServerSocket(14306);
			try {
				JFrame frame = new JFrame();
				createFrame(frame);
				final Object object = new Object();
				startButton.addActionListener(new ActionListener() {			
					@Override
					public void actionPerformed(ActionEvent e) {			
						if (startButton.getText().equals("START")) {
							synchronized(object) {
								object.notify();
							}
							startButton.setText("FINISH");
						}
						else {
							System.exit(0);
						}
					}
				});
				synchronized (object) {
					object.wait();
				}
				PrintableWoodLoader W = new PrintableWoodLoader();
				PrintableWood wood = W.PrintableWoodLoad(new FileInputStream(new File( (String) woods.getSelectedItem())),System.out);	
				wood.Paint(frame, startPoints, finishPoints, pointsArea);
				Synchronizer synchronizer = new Synchronizer(wood);
				thread = new Thread(new ThreadsServer(server.accept(), wood, startPoints.get(0), finishPoints.get(0), synchronizer));
				thread.start();
				int startPointChooserCounter = 1;
				int endPointChooserCounter = 1;
				thread = new Thread(synchronizer);
				thread.start();
				while (true) {
					thread = new Thread(new ThreadsServer(server.accept(), wood, startPoints.get(startPointChooserCounter), finishPoints.get(endPointChooserCounter), synchronizer));
					thread.start();
					startPointChooserCounter++;
					endPointChooserCounter++;
					if (startPointChooserCounter >= startPoints.size()) {
						startPointChooserCounter = 0;
					}
					if (endPointChooserCounter >= finishPoints.size()) {
						endPointChooserCounter = 0;
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			} catch(Exception ter){
				tryClose(server);
				System.exit(0);
			} finally {
				tryClose(server);
			}
		}
		catch (IOException e) {
			System.exit(-1);
		}
	}

	private static void createFrame(JFrame GUI) {
		woods = new JComboBox<String>();
		woods.setBackground(Color.GREEN);
		Image img=Toolkit.getDefaultToolkit().getImage("C://1.png");
		GUI.setIconImage(img); 
		try {
			File file = new File(new File(".").getAbsolutePath());
			String[] wood = file.list();
			Pattern txtFile = Pattern.compile("[A-Za-z0-9-]+\\.txt");
			Matcher matcher;
			for (int i = 0; i < wood.length; i++) {
				matcher = txtFile.matcher(wood[i]);
				if (matcher.find()) {
					woods.addItem(wood[i]);
				}
			}
		}
		catch (Exception e) {
			System.out.println("Woods weren't loaded");
		}	
		startButton = new JButton("START");
		startButton.setBackground(Color.YELLOW);
		addStartPoints = new JButton("Add start point");
		addStartPoints.setBackground(Color.PINK);
		addFinishPoints = new JButton("Add finish point");
		addFinishPoints.setBackground(Color.PINK);
		final JTextArea pointsArea = new JTextArea("Point's list : \r\n");
		pointsArea.setEditable(false);
		final JScrollPane points = new JScrollPane(pointsArea);
		points.setBackground(Color.lightGray);
		final JTextArea xStart = new JTextArea();
		xStart.setPreferredSize(new Dimension(20,20));
		xStart.setBackground(Color.CYAN);
		final JTextArea yStart = new JTextArea();
		yStart.setPreferredSize(new Dimension(20,20));
		yStart.setBackground(Color.CYAN);
		final JTextArea xFinish = new JTextArea();
		xFinish.setPreferredSize(new Dimension(20,20));
		xFinish.setBackground(Color.CYAN);
		final JTextArea yFinish = new JTextArea();
		yFinish.setPreferredSize(new Dimension(20,20));
		yFinish.setBackground(Color.CYAN);
		addStartPoints.addActionListener(new ActionListener() {
			@Override		
			public void actionPerformed(ActionEvent arg0) {
				try {
					Point start = new Point(Integer.valueOf(xStart.getText()).intValue(),Integer.valueOf(yStart.getText()).intValue());
					startPoints.add(start);				
					pointsArea.append("Start location : (" + startPoints.get(startPoints.size() - 1).getX()+ ", "+ startPoints.get(startPoints.size() - 1).getY() + ")\r\n");
					points.revalidate();
				} catch (Exception e) {
					pointsArea.append("Try again");
				}
			}
		});
		addFinishPoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Point finish = new Point(Integer.valueOf(xFinish.getText()).intValue(),Integer.valueOf(yFinish.getText()).intValue());
					finishPoints.add(finish);
					pointsArea.append("Finish location: (" + finishPoints.get(startPoints.size() - 1).getX() +"; "+finishPoints.get(startPoints.size() - 1).getY()+")" + "\r\n");						
					points.revalidate();
				} catch (Exception e1) {
					pointsArea.append("Try again");
				}
			}
		});
		GridBagLayout ourLayout = new GridBagLayout();
		GridBagConstraints constraints = new GridBagConstraints();
		GUI.setLayout(ourLayout);
		constraints.anchor = GridBagConstraints.NORTHWEST; 
		constraints.fill = GridBagConstraints.HORIZONTAL;  
		constraints.gridheight = 1;
		constraints.gridwidth = 4; 
		constraints.gridx = 0; 
		constraints.gridy = 0;
		constraints.insets = new Insets(10, 10, 10, 30);
		constraints.ipadx = 0;
		constraints.ipady = 0;
		constraints.weightx = 0.0;
		constraints.weighty = 0.0;
		GUI.add(woods, constraints);
		constraints.gridy = 1;
		GUI.add(startButton, constraints);
		constraints.gridy = 2;
		GUI.add(addStartPoints, constraints);
		constraints.gridy = 3;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		constraints.anchor = GridBagConstraints.NORTHEAST;
		constraints.gridwidth = 1;
		constraints.gridx = 0;
		JLabel l = new  JLabel("X");
		GUI.add(l,constraints);
		constraints.gridx = 1;
		GUI.add(xStart,constraints);
		constraints.gridx = 2;
		GUI.add(new JLabel("Y"),constraints);
		constraints.gridx = 3;
		GUI.add(yStart,constraints);
		constraints.gridy = 4;
		constraints.gridx = 0;
		constraints.gridwidth = 4;
		constraints.anchor = GridBagConstraints.NORTHEAST;
		GUI.add(addFinishPoints,constraints);
		constraints.gridy = 5;
		constraints.gridwidth = 1;
		constraints.anchor = GridBagConstraints.NORTHEAST;
		GUI.add(new JLabel("X"), constraints);
		constraints.gridx = 1;
		GUI.add(xFinish,constraints);
		constraints.gridx = 2;
		GUI.add(new JLabel("Y "),constraints);
		constraints.gridx = 3;
		GUI.add(yFinish,constraints);
		points.setPreferredSize(new Dimension(20,210));
		points.setBorder(new LineBorder(Color.YELLOW));
		constraints.gridy = 6;
		constraints.gridwidth = 4;
		constraints.gridheight = GridBagConstraints.RELATIVE;
		constraints.gridx = 0;
		constraints.anchor = GridBagConstraints.NORTHWEST;
		constraints.fill = GridBagConstraints.HORIZONTAL;
		GUI.add(points,constraints);
		GUI.setTitle("MOUSE MAZE");
		File mouseFile = new File("originalmouse.png");
		try {
			BufferedImage mouse = ImageIO.read(mouseFile);
			GUI.setIconImage(mouse);
		} catch (IOException e) {
			e.printStackTrace();
		}
		GUI.setVisible(true);
		GUI.pack();
	}
}
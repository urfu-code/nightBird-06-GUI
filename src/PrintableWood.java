import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.LineBorder;

public class PrintableWood extends MyWood {

	private JFrame GUI;
	private JPanel legend;
	private JPanel pointsArea;
	private HashMap<String,JLabel>mouseImages;
	private HashMap<String,JLabel>startPoints;
	private HashMap<String,JLabel>endPoints;
	private HashMap<String,JLabel> legendLabels;
	private Queue<String> startList;
	private Queue<String> finishList;
	private Queue<String> mouseList;
	private ArrayList<Point> endPointsArray;
	private ArrayList<Point> startPointsArray;
	private BufferedImage wall;
	private BufferedImage trap;
	private BufferedImage free;
	private BufferedImage live;
	
	
	public PrintableWood(char[][] wood, OutputStream stream) throws IOException {
		super(wood);
		legendLabels = new HashMap<String,JLabel>();
		startList = new LinkedList<String>();
		startList.offer("start_red.png");
		startList.offer("start_yellow.png");
		startList.offer("start_pink.png");
		startList.offer("start_blue.png");
		startList.offer("start_green.png");
		startList.offer("start_indigo.png");
		
		finishList = new LinkedList<String>();
		finishList.offer("finish_red.png");
		finishList.offer("finish_yellow.png");
		finishList.offer("finish_pink.png");
		finishList.offer("finish_blue.png");
		finishList.offer("finish_green.png");
		finishList.offer("finish_indigo.png");
		
		mouseList = new LinkedList<String>();
		mouseList.offer("mouse_red.png");
		mouseList.offer("mouse_yellow.png");
		mouseList.offer("mouse_pink.png");
		mouseList.offer("mouse_blue.png");
		mouseList.offer("mouse_green.png");
		mouseList.offer("mouse_indigo.png");
		
		wall = ImageIO.read(new File("wall.png"));
		trap = ImageIO.read(new File("trap.png"));
		free = ImageIO.read(new File("free.png"));
		live = ImageIO.read(new File("live.png"));
		startPoints = new HashMap<String,JLabel>();
		endPoints = new HashMap<String,JLabel>();
		mouseImages = new HashMap<String,JLabel>();
	}
	
	public void paint(JFrame window, ArrayList<Point>startPoints, ArrayList<Point>endPoints, JPanel pointsArea) throws Exception {
		this.GUI = window;
		this.pointsArea = pointsArea;
		this.startPointsArray = startPoints;
		this.endPointsArray = endPoints;
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = 20;
		c.gridy = 1;
		c.anchor = GridBagConstraints.NORTH;
		for (int i = 0; i < m_wood[0].length; i++) {
			for (int j = 0; j < m_wood.length; j++) {
				c.gridx++;
				window.add(getImages(m_wood[j][i]),c);
			}
			c.gridy++;
			c.gridx = 20;
		}
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;		
		window.add(new JLabel("Легенда:"),c);
		c.gridwidth = 9;
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		JLabel label = new JLabel(new ImageIcon(live));
		label.setText(" - Жизнь\r\n");
		window.add(label, c);
		c.gridy++;
		label = new JLabel(new ImageIcon(trap));
		label.setText(" - Ловушка\r\n");
		window.add(label,c);
		c.gridy++;
		label = new JLabel(new ImageIcon(wall));
		label.setText(" - Стена\r\n");
		window.add(label,c);
		c.fill = GridBagConstraints.BOTH;
		c.anchor = GridBagConstraints.NORTH;
		c.gridx = 13;
		c.gridy = m_wood.length + 1;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 6;
		legend = new JPanel();
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));	
		JScrollPane legendScrollPane = new JScrollPane(legend);
		legendScrollPane.setPreferredSize(new Dimension(0,200));
		legendScrollPane.setBorder(new LineBorder(Color.GREEN));
		c.gridy = 4;
		c.gridwidth = 8;
		window.add(legendScrollPane,c);
		window.setResizable(false);
		window.pack();
		legend.revalidate();
		window.repaint();
	}
	
	private Component getImages(char c) {
		switch (c) {
		case '1':
			return new JLabel(new ImageIcon(wall));
		case '0':
			return new JLabel(new ImageIcon(free));
		case 'L':
			return new JLabel(new ImageIcon(live));
		case 'K':
			return new JLabel(new ImageIcon(trap));
		default:
			return null;
		}
	}
	
	public void repaint() {
		Point position;
		Point finish;
		Point start;
		
		loop:for (MyWoodman i: super.m_woodmanList.values()) {
			position = i.GetLocation();
			start = i.GetStart();
			finish = i.GetFinish();
			GridBagConstraints c = new GridBagConstraints();
			for (int y = 0; y < m_wood[0].length; y++) {
				for (int x = 0; x < m_wood.length; x++) {
					if ((position.getX() == x) && (position.getY() == y)) {
						try {
							c.gridx = x + 21;
							c.gridy = y+1;
							GUI.add(mouseImages.get(i.GetName()),c, GUI.getComponentCount());
							
							c.gridx = start.getX() + 21;
							c.gridy = start.getY() + 1;
							GUI.add(startPoints.get(i.GetName()),c, GUI.getComponentCount());
							
							c.gridx = finish.getX() + 21;
							c.gridy = finish.getY() + 1;
							GUI.add(endPoints.get(i.GetName()),c, GUI.getComponentCount());
							
							continue loop;
						} catch (Exception e) {
							continue loop;
						}
					}
				}
			}
		}
		legend.revalidate();
		GUI.pack();
		GUI.repaint();
	}
	
		@Override
	public Action move(String name, Direction direction) throws Exception {
		Action currentAction;
		currentAction = super.move(name, direction);
		if (currentAction == Action.WoodmanNotFound || currentAction == Action.Finish) {
			GUI.remove(startPoints.get(name));
			GUI.remove(endPoints.get(name));
			GUI.remove(mouseImages.get(name));
			mouseImages.remove(name);
			startPoints.remove(name);
			endPoints.remove(name);
			if (currentAction == Action.WoodmanNotFound) {
				legendLabels.get(name).setText(name + " is dead :(\r\n");
			} else {
				legendLabels.get(name).setText(name + " is a winner :)\r\n");
			}
	
		} else {
				legendLabels.get(name).setText(name + ": " + super.m_woodmanList.get(name).GetLifeCount() + " lives \r\n");
		}
		return currentAction;
	}
	
	@Override
	public void createWoodman(String name, Point startPoint, Point finishPoint) throws Exception {
		super.createWoodman(name, startPoint, finishPoint);
		String mouse_start = startList.poll();
		String mouse_finish = finishList.poll();
		String mouse = mouseList.poll();
		GridBagConstraints c = new GridBagConstraints();

		startPoints.put(name, new JLabel(new ImageIcon(ImageIO.read(new File(mouse_start)))));
		c.gridx = startPoint.getX() + 21;
		c.gridy = startPoint.getY() + 1;
		GUI.add(startPoints.get(name),c);
		
		endPoints.put(name,new JLabel(new ImageIcon(ImageIO.read(new File(mouse_finish)))));
		c.gridx = finishPoint.getX() + 21;
		c.gridy = finishPoint.getY() + 1;
		GUI.add(endPoints.get(name),c);
		
		mouseImages.put(name,new JLabel(new ImageIcon(ImageIO.read(new File(mouse)))));
		JLabel label = new JLabel(new ImageIcon(ImageIO.read(new File(mouse))));
		label.setText(super.m_woodmanList.get(name).GetName() + ": " + super.m_woodmanList.get(name).GetLifeCount() + " live(s)\r\n");
		legendLabels.put(name,label);
		legend.add(label);
	
		mouseList.offer(mouse);
		startList.offer(mouse_start);
		finishList.offer(mouse_finish);
	}
}


	

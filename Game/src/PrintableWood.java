
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class PrintableWood extends MyWood {
	List<StringBuilder> printList = new LinkedList<StringBuilder>(); 
	int wl;
	int ww;
	private BufferedImage life;
	private BufferedImage trap;
	private BufferedImage space;
	private BufferedImage wall;
	private BufferedImage mouse;
	private JFrame frame;
	private JPanel panel = new JPanel();
	private HashMap <String,JLabel> startPoints;
	private HashMap <String,JLabel> finishPoints;
	private HashMap <String,JLabel> listOfMouseImages  = new HashMap<String,JLabel>();
	private Queue<Color> colors = new LinkedList<Color>();
	private HashMap<String,JLabel> legendLabels = new HashMap<String,JLabel>();
	private Queue<Color> startList = new LinkedList<Color>();
	private Queue<Color> finishList = new LinkedList<Color>();
	private LinkedList<String> mouseList = new LinkedList<String>();

	public PrintableWood(char[][] wood,OutputStream stream) throws IOException, CodeException {
		super(wood);
		startList.offer(Color.BLUE);
		startList.offer(Color.DARK_GRAY);
		startList.offer(Color.ORANGE);
		startList.offer(Color.GREEN);
		startList.offer(Color.GRAY);
		startList.offer(Color.PINK);
		startList.offer(Color.MAGENTA);
		startList.offer(Color.RED);

		finishList.offer(Color.BLUE);
		finishList.offer(Color.DARK_GRAY);
		finishList.offer(Color.ORANGE);
		finishList.offer(Color.GREEN);
		finishList.offer(Color.GRAY);
		finishList.offer(Color.PINK);
		finishList.offer(Color.MAGENTA);
		finishList.offer(Color.RED);

		mouseList.offer("bluemouse.png");
		mouseList.offer("greymouse.png");
		mouseList.offer("originalmouse.png");
		mouseList.offer("brownmouse.png");
		mouseList.offer("greenmouse.png");
		mouseList.offer("pinkmouse.png");
		mouseList.offer("purplemouse.png");
		mouseList.offer("redmouse.png");

		wall = ImageIO.read(new File("flower.png"));
		trap = ImageIO.read(new File("cross.png"));
		space = ImageIO.read(new File("space.png"));
		life = ImageIO.read(new File("heart.png"));
		mouse = ImageIO.read(new File("originalmouse.png"));
		startPoints = new HashMap<String,JLabel>();
		finishPoints = new HashMap<String,JLabel>();
		listOfMouseImages = new HashMap<String,JLabel>();
		m_wood = wood;
		wl = wood.length;
		ww = wood[0].length;
	}

	@Override
	public void createWoodman(String name, Point start,Point finish) throws CodeException, IOException{
		super.createWoodman(name, start, finish);	
		Color mouseColor = colors.poll();
		GridBagConstraints constraints = new GridBagConstraints();
		startPoints.put(name, new JLabel("start"));
		startPoints.get(name).setBorder(new LineBorder(mouseColor));
		startPoints.get(name).setPreferredSize(new Dimension(30, 30));
		constraints.gridx = start.getX() + 6;
		constraints.gridy = start.getY();
		frame.add(startPoints.get(name), constraints);
		finishPoints.put(name, new JLabel("finish"));
		finishPoints.get(name).setBorder(new LineBorder(mouseColor));
		finishPoints.get(name).setPreferredSize(new Dimension(30, 30));
		constraints.gridx = finish.getX() + 6;
		constraints.gridy = finish.getY();
		frame.add(finishPoints.get(name),constraints);
		listOfMouseImages.put(name,new JLabel(new ImageIcon(mouse)));
		colors.offer(mouseColor);
		listOfMouseImages.get(name).setBorder(new LineBorder(mouseColor));
		listOfMouseImages.get(name).setPreferredSize(new Dimension(30, 30));
		JLabel label = new JLabel(new ImageIcon(mouse));
		label.setText(super.m_woodmanList.get(name).GetName() + '(' + super.m_woodmanList.get(name).GetLifeCount() + " lives)"+ System.lineSeparator());
		label.setBorder(new LineBorder(mouseColor));
		legendLabels.put(name,label);
		panel.add(label);
	}

	@Override
	public Action move(String name, Direction direction) throws IOException, CodeException {
		Action result = super.move(name, direction);
		if (result == Action.WoodmanNotFound || result == Action.Finish) {
			frame.remove(startPoints.get(name));
			frame.remove(finishPoints.get(name));
			frame.remove(listOfMouseImages.get(name));
			listOfMouseImages.remove(name);
			startPoints.remove(name);
			finishPoints.remove(name);
			if (result == Action.WoodmanNotFound) {
				legendLabels.get(name).setText(name + " didn't reach finish\r\n");
			} else {
				legendLabels.get(name).setText(name + " reached finish\r\n");
			}
		} else {
			legendLabels.get(name).setText(name + '('+ super.m_woodmanList.get(name).GetLifeCount() + " lives)"+ System.lineSeparator());
		}
		return result;
	}

	public void Paint(JFrame fr, LinkedList<Point>startPoints, LinkedList<Point>finishPoints, JPanel pointsArea) throws Exception {
		frame = fr;
		GridBagConstraints constraint = new GridBagConstraints();
		constraint.gridy = 13;
		constraint.gridwidth = 4;
		constraint.gridx = 0;
		constraint.anchor = GridBagConstraints.NORTHWEST;
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.gridheight = GridBagConstraints.RELATIVE;
		constraint = new GridBagConstraints();
		constraint.gridx = 5;
		constraint.gridy = 0;
		constraint.anchor = GridBagConstraints.NORTH;
		for (int i = 0; i < wl; i++) {
			for (int j = 0; j < ww; j++) {
				constraint.gridx++;
				fr.add(getImageOfWood(m_wood[i][j]),constraint);
			}
			constraint.gridy++;
			constraint.gridx = 5;
		}
		constraint.gridwidth = GridBagConstraints.REMAINDER;
		constraint.gridheight = 1;
		constraint.fill = GridBagConstraints.HORIZONTAL;
		constraint.anchor = GridBagConstraints.NORTH;		
		JLabel label = new JLabel(new ImageIcon(ImageIO.read(new File("mousemaze.png"))));
		fr.add(label,constraint);
		constraint.gridwidth = 4;
		constraint.gridy++;
		constraint.fill = GridBagConstraints.NONE;
		constraint.anchor = GridBagConstraints.WEST;
		label = new JLabel(new ImageIcon(life));
		label.setText(" - more life \r\n");
		fr.add(label, constraint);
		constraint.gridy++;
		label = new JLabel(new ImageIcon(trap));
		label.setText(" - less life \r\n");
		fr.add(label,constraint);
		constraint.gridy++;
		label = new JLabel(new ImageIcon(wall));
		label.setText(" - wall \r\n");
		fr.add(label,constraint);
		constraint.fill = GridBagConstraints.BOTH;
		constraint.anchor = GridBagConstraints.NORTH;
		constraint.gridx = 9;
		constraint.gridy = m_wood.length + 2;
		constraint.gridheight = GridBagConstraints.REMAINDER;
		constraint.gridwidth = 6;
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		JScrollPane newscroll = new JScrollPane(panel);
		newscroll.setPreferredSize(new Dimension(0,200));
		fr.add(newscroll,constraint);
		fr.setResizable(true);
		fr.pack();
		panel.revalidate();
		fr.repaint();
	}

	private Component getImageOfWood(char symbol) {
		switch (symbol) {
		case 'L':
			return new JLabel(new ImageIcon(life));
		case 'K':
			return new JLabel(new ImageIcon(trap));
		case '1':
			return new JLabel(new ImageIcon(wall));
		case '0':
			return new JLabel(new ImageIcon(space));
		default:
			return null;
		}
	}

	public void Repaint() {
		Point woodmanPosition;
		Point start;
		Point finish;		
		loop:for (MyWoodman i: super.m_woodmanList.values()) {
			woodmanPosition = i.GetLocation();
			start = i.GetStart();
			finish = i.GetFinish();
			GridBagConstraints c = new GridBagConstraints();
			for (int j = 0; j < wl; j++) {
				for (int k = 0; k < ww; k++) {
					if (woodmanPosition.getX() == k && woodmanPosition.getY() == j) {
						c.gridx = k + 6;
						c.gridy = j;
						frame.add(listOfMouseImages.get(i.GetName()), c, frame.getComponentCount());
						c.gridx = start.getX() + 6;
						c.gridy = start.getY();
						frame.add(startPoints.get(i.GetName()), c, frame.getComponentCount());
						c.gridx = finish.getX() + 6;
						c.gridy = finish.getY();
						frame.add(finishPoints.get(i.GetName()), c, frame.getComponentCount());
						continue loop;
					}
				}
			}
		}
		panel.revalidate();
		frame.pack();
		frame.repaint();
	}
}

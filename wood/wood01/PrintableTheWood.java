package wood01;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;



public class PrintableTheWood extends TheWood {
	//'┌','─','┬','┐','│','┼','┤','├','└','─','┴','┘','♥','Ⓣ',' ','□'
    //'α','β','γ','δ','ε','ζ','η','θ','ι','κ','λ','μ','ν','ξ','ο','π','ρ','ς','σ','τ','υ','φ','χ','ψ','ω'	
	@Deprecated
	private Map<String,Character>graphList;
	@Deprecated
	private Map<String,Character>woodmanNames;
	@Deprecated
	private char[] nameList;
	private BufferedImage wall;
	private BufferedImage trap;
	private BufferedImage free;
	private BufferedImage live;
	private BufferedImage mouse;
	private JFrame GUI;
	private JPanel legend;
	private HashMap<String,JLabel>startPoints;
	private HashMap<String,JLabel>endPoints;
	private HashMap<String,Integer> leaders;
	private HashMap<String,JLabel>mouseImages;
	private JTextArea leadersArea;
	private Queue<Color> colorList;
	private HashMap<String,JLabel> legendLabels;
	
	private char getElement(int i,int j) {
		return wood[i][j];
	}
	public PrintableTheWood(char[][] _wood,OutputStream _stream) throws Exception {
		super(_wood);
		legendLabels = new HashMap<String,JLabel>();
		colorList = new LinkedList<Color>();
		colorList.offer(Color.BLACK);
		colorList.offer(Color.BLUE);
		colorList.offer(Color.CYAN);
		colorList.offer(Color.DARK_GRAY);
		colorList.offer(Color.GREEN);
		colorList.offer(Color.MAGENTA);
		colorList.offer(Color.ORANGE);
		colorList.offer(Color.PINK);
		colorList.offer(Color.RED);
		colorList.offer(Color.YELLOW);
		wall = ImageIO.read(new File("stick.png"));
		trap = ImageIO.read(new File("trap.png"));
		free = ImageIO.read(new File("free.png"));
		live = ImageIO.read(new File("live.png"));
		mouse = ImageIO.read(new File("raccoon.png"));
		startPoints = new HashMap<String,JLabel>();
		endPoints = new HashMap<String,JLabel>();
		mouseImages = new HashMap<String,JLabel>();
//		graphList = new HashMap<String,Character>();
//		woodmanNames = new HashMap<String,Character>();
//		graphList.put("U", '│');
//		graphList.put("D", '│');
//		graphList.put("L", '─');
//		graphList.put("R", '─');
//		graphList.put("UD", '│');
//		graphList.put("LR", '─');
//		graphList.put("DR",'┌');
//		graphList.put("LDR",'┬');
//		graphList.put("LD",'┐');
//		graphList.put("ULDR",'┼');
//		graphList.put("ULD",'┤');
//		graphList.put("UDR",'├');
//		graphList.put("UR",'└');
//		graphList.put("ULR",'┴');
//		graphList.put("UL",'┘');
//		graphList.put("Life",'♥');
//		graphList.put("0",' ');
//		graphList.put("A",'□');
//		graphList.put("Trap",'ѻ');
//		nameList = new char[]{'α','β','γ','δ','ε','ζ','η','θ','ι','κ','λ','μ','ν','ξ','ο','π','ρ','ς','σ','τ','υ','φ','χ','ψ','ω'};	
	}
	@Deprecated
	private void printWood(OutputStream stream) throws Exception {
		char[] line = new char[wood[0].length];
		for (int i = 0; i < wood.length; i++) {
			for (int j = 0; j < wood[0].length; j++) {
				line[j] = findElement(i,j);
			}
			String str = new String(line);
			stream.write((str + "\n").getBytes());
		}
		stream.write("------------\n".getBytes());
		stream.write(("♥" + " - live\n").getBytes());
		stream.write(("ѻ" + " - trap\n").getBytes());
		stream.write("------------\n".getBytes());
		for (TheWoodman i:woodmans.values()) {
			stream.write((i.GetName() + " (" + getWoodmanName(i.GetName()) + ")" + " - " + i.GetLifeCount() + " live(s)\n").getBytes());
		}
	}
	public void paintWood(JFrame window, HashMap<String,Integer> leaders) throws Exception {
		this.GUI = window;
		this.leaders = leaders;
		GridBagConstraints c = new GridBagConstraints();
		c.gridy = 13;
		c.gridwidth = 4;
		c.gridx = 0;
		c.anchor = GridBagConstraints.NORTHWEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridheight = GridBagConstraints.RELATIVE;
		leadersArea = new JTextArea("Список лидеров:\r\n");
		JScrollPane leadersPane = new JScrollPane(leadersArea);
		leadersPane.setPreferredSize(new Dimension(0,200));
		leadersArea.setEditable(false);
		leadersPane.setBorder(new LineBorder(Color.GREEN));
		GUI.add(leadersPane,c);
		c = new GridBagConstraints();
		c.gridx = 5;
		c.gridy = 0;
		c.anchor = GridBagConstraints.NORTH;
		for (int i = 0; i < wood.length; i++) {
			for (int j = 0; j < wood[0].length; j++) {
				c.gridx++;
				window.add(newGraphUnit(wood[i][j]),c);
			}
			c.gridy++;
			c.gridx = 5;
		}
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = 1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.NORTH;
//		легенда			
		JLabel label = new JLabel(new ImageIcon(ImageIO.read(new File("legend.png"))));
		window.add(label,c);
		c.gridwidth = 4;
		c.gridy++;
		c.fill = GridBagConstraints.NONE;
		c.anchor = GridBagConstraints.WEST;
		label = new JLabel(new ImageIcon(live));
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
		c.gridx = 9;
		c.gridy = wood.length + 1;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.gridwidth = 6;
		legend = new JPanel();
		legend.setLayout(new BoxLayout(legend, BoxLayout.Y_AXIS));
		JScrollPane legendScrollPane = new JScrollPane(legend);
		legendScrollPane.setPreferredSize(new Dimension(0,200));
		window.add(legendScrollPane,c);
		window.setResizable(false);
		window.setTitle("Сервер с игрой запущен!");
		window.pack();
		legend.revalidate();
		window.repaint();
	}
	public void repaintWood() {
		Point woodmanPosition;
		loop:for (TheWoodman i: super.woodmans.values()) {
			woodmanPosition = i.GetLocation();
			GridBagConstraints c = new GridBagConstraints();
			for (int j = 0; j < wood.length; j++) {
				for (int k = 0; k < wood[0].length; k++) {
					if (woodmanPosition.getX() == k && woodmanPosition.getY() == j) {
						c.gridx = k + 6;
						c.gridy = j;
						GUI.add(mouseImages.get(i.GetName()),c);
						continue loop;
					}
				}
			}
		}
		leadersArea.revalidate();
		legend.revalidate();
		GUI.pack();
		GUI.repaint();
	}
	private Component newGraphUnit(char c) {
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
	@Deprecated
	private char getWoodmanName(String name) throws Exception {
		if (woodmanNames.containsKey(name)) {
			return woodmanNames.get(name);
		}
		for (int i = 0; i < nameList.length; i++) {
			if (nameList[i] != 'N') {
				woodmanNames.put(name, nameList[i]);
				nameList[i] = 'N';
				return woodmanNames.get(name);
			}
		}
		throw new Exception("обозначалки для персонажей кончились :((");
	}
	@Deprecated
	private char findElement(int line, int column) throws Exception
	{
		Point currentPoint = new Point(column,line);
		for (TheWoodman i:woodmans.values()) {
			if (i.GetLocation().equals(currentPoint)) {
				return getWoodmanName(i.GetName());
			}
		}
		switch (getElement(line,column)) {
		
		case 'L':
			//life
			return graphList.get("Life");
		case 'K':
			//trap
			return graphList.get("Trap");
		case '0':
			//free
			return graphList.get("0");
		default:
			break;
			
		}
		StringBuffer element = new StringBuffer();
		//up
		if ((line - 1 >= 0)&&(getElement(line - 1,column) == '1')) {
			element.append("U");
		}
		//left
		if ((column - 1 >= 0)&&(getElement(line,column - 1) == '1')) {
			element.append("L");
		}
		//down
		if ((line + 1 < wood.length)&&(getElement(line + 1,column) == '1')) {
			element.append("D");
		}
		//right
		if ((column + 1 < wood[0].length)&&(getElement(line,column + 1) == '1')) {
			element.append("R");
		}
		if (element.length() > 0) {
			return graphList.get(element.toString());
		}
		else {
			return graphList.get("A");
		}
	}
		@Override
	public Action move(String name, Direction direction) throws Exception {
		Action currentAction;
		currentAction = super.move(name, direction);
		if (currentAction == Action.WoodmanNotFound || currentAction == Action.ExitFound) {
			GUI.remove(startPoints.get(name));
			GUI.remove(endPoints.get(name));
			GUI.remove(mouseImages.get(name));
			mouseImages.remove(name);
			startPoints.remove(name);
			endPoints.remove(name);
			legend.remove(legendLabels.remove(name));
		
		}
		else if (currentAction != Action.Ok){
			legendLabels.get(name).setText(name + " " + super.woodmans.get(name).GetLifeCount() + " - жизней(и)\r\n");
		}
		return currentAction;
	}
	@Override
	public void createWoodman(String name, Point startPoint, Point finishPoint) throws Exception {
		super.createWoodman(name, startPoint, finishPoint);
		Color mouseColor = colorList.poll();
		GridBagConstraints c = new GridBagConstraints();
		startPoints.put(name, new JLabel("start"));
		startPoints.get(name).setBorder(new LineBorder(mouseColor));
		startPoints.get(name).setPreferredSize(new Dimension(30,30));
		c.gridx = startPoint.getX() + 6;
		c.gridy = startPoint.getY();
		GUI.add(startPoints.get(name),c);
		endPoints.put(name, new JLabel("end"));
		endPoints.get(name).setBorder(new LineBorder(mouseColor));
		endPoints.get(name).setPreferredSize(new Dimension(30, 30));
		c.gridx = finishPoint.getX() + 6;
		c.gridy = finishPoint.getY();
		GUI.add(endPoints.get(name),c);
		mouseImages.put(name,new JLabel(new ImageIcon(mouse)));
		colorList.offer(mouseColor);
		mouseImages.get(name).setBorder(new LineBorder(mouseColor));
		mouseImages.get(name).setPreferredSize(new Dimension(30,30));
//		Добавить в легенду еще надо
		JLabel label = new JLabel(new ImageIcon(mouse));
		label.setText(super.woodmans.get(name).GetName() + " " + super.woodmans.get(name).GetLifeCount() + " - Жизней(и)\r\n");
		label.setBorder(new LineBorder(mouseColor));
		legendLabels.put(name,label);
		legend.add(label);
	}
}



import java.awt.Dimension;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class PrintableWood extends Wood {

	private static HashMap<Point, Character> initialLabyrinth; // изначальное представление лабиринта, не меняется

	//	volatile ConcurrentHashMap<String, Point> namesAndImages = new ConcurrentHashMap<>();

	volatile private static PictureWood frame;
	Image treeImage;
	Image trapImage;
	Image lifeImage;
	JComponent wood;
	JPanel panelForNotes;

	final ImageIcon trapIcon = new ImageIcon("trap3.png"); // для легенды
	final ImageIcon lifeIcon = new ImageIcon("heart3.png");	// для легенды
	private HashMap<String, ImageIcon> namesAndIcons = new HashMap<String, ImageIcon>(); // для легенды

	static Sprite mouse_01, mouse_02, mouse_03, mouse_04, mouse_05, mouse_06, mouse_07, mouse_08, mouse_09, mouse_10;
	private Sprite[] sprites = {mouse_01, mouse_02, mouse_03, mouse_04, mouse_05, mouse_06, mouse_07, mouse_08, mouse_09, mouse_10};
	private ImageIcon[] icons = new ImageIcon[sprites.length]; // для легенды
	volatile private static ConcurrentHashMap<Sprite, Point> locationOfImages = new ConcurrentHashMap<Sprite, Point>(); // для запоминания позиций картинок
	private boolean[] busiedImages = new boolean[sprites.length]; // чтобы учитывать освободившиеся картинки в случае окончательной смерти
	ArrayList<Point> traps;
	ArrayList<Point> lifes;

	public Sprite getSprite(File fileMouse) throws IOException {
		Sprite sprite = new Sprite(ImageIO.read(fileMouse));
		return sprite;
	}

	PrintableWood(HashMap<Point, Character> lab, final int length, final int width, PictureWood frame) throws IOException {
		super(lab, length, width);
		PrintableWood.frame = frame;
		initialLabyrinth = lab;
		traps = traps();
		lifes = lifes();

		String listOfMice[] = new File("mice/").list();
		try {
			for (int i = 0; i < sprites.length; i++) {
				sprites[i] = getSprite(new File ("mice/" + listOfMice[i]));
				icons[i] = new ImageIcon("mice/" + listOfMice[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			treeImage = ImageIO.read(new File("fir3.png"));
			trapImage = ImageIO.read(new File("trap3.png"));
			lifeImage = ImageIO.read(new File("heart3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < busiedImages.length; i++) {
			busiedImages[i] = false;
		}

		panelForNotes = new JPanel();
		panelForNotes.setLayout(new BoxLayout(panelForNotes, BoxLayout.Y_AXIS));
		panelForNotes.setAlignmentX(SwingConstants.RIGHT);
		panelForNotes.setBorder((new EmptyBorder(0, 0, 0, 5)));
		panelForNotes.setMinimumSize(new Dimension(100, 100));
		PrintableWood.frame.panelForWoodAndNotes.add(panelForNotes);

		wood = new WorkingWoodField(traps, lifes, locationOfImages, length, width, initialLabyrinth);

		wood.setPreferredSize(new Dimension(treeImage.getWidth(null)*length, treeImage.getHeight(null)*width));
		wood.repaint();
		wood.setVisible(true);

		PrintableWood.frame.wf = wood;

		printLabyrinth();
	}

	@Override
	public void createWoodman(String name, Point start, Point finish) {
		// сначала проверяем, есть ли свободные картинки
		int counterOfSymbols;
		for (counterOfSymbols = 0; counterOfSymbols < busiedImages.length; counterOfSymbols++) {
			if (busiedImages[counterOfSymbols] == false)
				break;
		}

		super.createWoodman(name, start, finish);

		namesAndIcons.put(name, icons[counterOfSymbols]); // взяли иконку с мышью, проассоциировали с именем
		busiedImages[counterOfSymbols] = true;	
		//		labyrinthOfSymbols.put(start, namesAndImages.get(name)); // поместили изображение на указаную точку
		//		namesAndImages.put(name, start);
		locationOfImages.put(sprites[counterOfSymbols], start);
		printLabyrinth();
	}


	@Override
	public Action move(String name, Direction direction) {
		return super.move(name, direction);
	}

	@Override
	public Action result(Point currentLocation, String name, Point newLocation) {
		int i = 0;
		for (i = 0; i < icons.length; i++) {
			if (icons[i].equals(namesAndIcons.get(name)))
				break;
		}
		if (newLocation.equals(getWoodman(name).finish)) {		
			//			ifThereisAnotherWoodman(currentLocation);
			getWoodman(name).SetLocation(newLocation);
			locationOfImages.put(sprites[i], newLocation);
			printLabyrinth();
			//			ifThereisAnotherWoodman(newLocation);
			listOfWoodmen.remove(name);
			busiedImages[i] = false;
			namesAndIcons.remove(name);
			locationOfImages.remove(sprites[i]);
			printLabyrinth();
			if (noMoreWoodmen())
				frame.setNewGameIsPossible(true);
			return Action.Finish;
		}
		switch (getChar(newLocation)) {
		case ' ' : 
			//			ifThereisAnotherWoodman(currentLocation);
			getWoodman(name).SetLocation(newLocation);
			locationOfImages.put(sprites[i], newLocation);
			printLabyrinth();
			return Action.Ok;

		case '◘' : 
			if (getChar(currentLocation) == '‡') {
				if (!getWoodman(name).Kill()) {
					listOfWoodmen.remove(name);
					busiedImages[i] = false;
					namesAndIcons.remove(name);
					locationOfImages.remove(sprites[i]);
					if (noMoreWoodmen())
						frame.setNewGameIsPossible(true);
					return Action.WoodmanNotFound;
				}
			}
			if (getChar(currentLocation) == '♥')
				getWoodman(name).AddLife();
			printLabyrinth();
			return Action.Fail;

		case '♥' :
			//			ifThereisAnotherWoodman(currentLocation);
			getWoodman(name).SetLocation(newLocation);
			locationOfImages.put(sprites[i], newLocation);
			getWoodman(name).AddLife();
			printLabyrinth();
			return Action.Life;

		case '‡' :
			//			ifThereisAnotherWoodman(currentLocation);
			getWoodman(name).SetLocation(newLocation);
			if (getWoodman(name).Kill()) {
				locationOfImages.put(sprites[i], newLocation);
				printLabyrinth();
				return Action.Dead;
			}
			else {
				listOfWoodmen.remove(name);
				// надо зафиксировать, что картинка освободилась
				busiedImages[i] = false;
				namesAndIcons.remove(name);
				locationOfImages.remove(sprites[i]);
				printLabyrinth();
				if (noMoreWoodmen())
					frame.setNewGameIsPossible(true);
				return Action.WoodmanNotFound;
			}		
		}
		return Action.Ok;
	}

	/**
	 * 
	 * @return true, если все игроки умерли или финишировали
	 */
	private boolean noMoreWoodmen() {
		if (listOfWoodmen.isEmpty())
			return true;
		return false;
	}

	//выводим мышей и обновляем легенду
	// пробегаемся по хэшмэпу с координатами
	void printLabyrinth() {
		// не обновляется само, надо менять размеры окна. не получается исправить никак
		panelForNotes.removeAll();
//		frame.panelForWoodAndNotes.remove(panelForNotes);

		JLabel noteLife = new JLabel("жизнь", lifeIcon, SwingConstants.LEFT);
		JLabel noteTrap = new JLabel("капкан", trapIcon, SwingConstants.LEFT);

		panelForNotes.add(noteLife);
		panelForNotes.add(noteTrap);
		Iterator<String> keySetIterator = namesAndIcons.keySet().iterator();
		while(keySetIterator.hasNext()){
			String key = keySetIterator.next();
			JLabel noteMouse = new JLabel(" " + key + " (жизней: " + getWoodman(key).GetLifeCount() + ")", namesAndIcons.get(key), SwingConstants.LEFT);
			panelForNotes.add(noteMouse);
			System.out.println(getWoodman(key).GetLocation());
//			wood.getGraphics().drawImage(trapImage, locationOfImages.get(key).getX()*treeImage.getWidth(null), locationOfImages.get(key).getY()*treeImage.getHeight(null), null);

		}
		panelForNotes.setVisible(true);
		wood.repaint();
//		frame.panelForWoodAndNotes.add(panelForNotes);
		
//		frame.panelForWoodAndNotes.setVisible(true);
		
		frame.panelForWoodAndNotes.repaint();

	}

	@Override
	Character getChar(Point location) {
		return initialLabyrinth.get(location);
	}

	public ArrayList<Point> traps() {
		ArrayList<Point> result = new ArrayList<>();		
		for (int i = 0; i < getLength(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				Point tempPoint = new Point(i, j);
				if (getChar(tempPoint).equals('‡'))
					result.add(tempPoint);
			}
		}	
		return result;
	}

	public ArrayList<Point> lifes() {
		ArrayList<Point> result = new ArrayList<>();	
		for (int i = 0; i < getLength(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				Point tempPoint = new Point(i, j);
				if (getChar(tempPoint).equals('♥'))
					result.add(tempPoint);
			}
		}		
		return result;
	}

}

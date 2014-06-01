import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * 
 * лабиринт, где будет бегать мышь
 *
 */
public class WorkingWoodField extends JComponent {

	private static final long serialVersionUID = 1L;
	private Image treeImage;
	private Image trapImage;
	private Image lifeImage;	
	private ArrayList<Point> traps;
	private ArrayList<Point> lifes;
	private volatile static ConcurrentHashMap<Sprite, Point> locationOfImages;
	private static HashMap<Point, Character> initialLabyrinth;
	private int length;
	private int width;
	static Graphics g;

	public WorkingWoodField(ArrayList<Point> traps, ArrayList<Point> lifes, ConcurrentHashMap<Sprite, Point> locationOfImages, int width, int height, HashMap<Point, Character> initialLabyrinth) {
		super();
		try {
			treeImage = ImageIO.read(new File("fir3.png"));
			trapImage = ImageIO.read(new File("trap3.png"));
			lifeImage = ImageIO.read(new File("heart3.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.traps = traps;
		this.lifes = lifes;
		WorkingWoodField.locationOfImages = locationOfImages;
		WorkingWoodField.initialLabyrinth = initialLabyrinth;
		this.length = width;
		this.width = height;
		
	}

	public void paintComponent(Graphics g) {
		WorkingWoodField.g = g;
		for (int i = 0; i < length; i++) {
			for (int j = 0; j < width; j++) {
				if (initialLabyrinth.get(new Point(i, j)).equals('1'))
					WorkingWoodField.g.drawImage(treeImage, i*treeImage.getWidth(null), j*treeImage.getHeight(null), null);
			}
		}
		for (int i = 0; i < traps.size(); i++) {
			WorkingWoodField.g.drawImage(trapImage, traps.get(i).getX()*treeImage.getWidth(null), traps.get(i).getY()*treeImage.getHeight(null), null);
		}
		for (int i = 0; i < lifes.size(); i++) {
			WorkingWoodField.g.drawImage(lifeImage, lifes.get(i).getX()*treeImage.getWidth(null), lifes.get(i).getY()*treeImage.getHeight(null), null);
		}
	}
	
//	public void repaint() {
//		Iterator<Sprite> keySetIterator = locationOfImages.keySet().iterator();
//		while (keySetIterator.hasNext()){
//			Sprite key = keySetIterator.next();
//			key.draw(g, locationOfImages.get(key).getX()*treeImage.getWidth(null), locationOfImages.get(key).getY()*treeImage.getHeight(null));
//			System.out.println(WorkingWoodField.g.drawImage(trapImage, 1*treeImage.getWidth(null), 1*treeImage.getHeight(null), null));
//			
//		}
//	}

}

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 * 
 * для просмотра лесов
 *
 */
public class WoodField extends JComponent {
	private Wood wood;
	private Image tree;
	private Image trap;
	private Image life;
	private ArrayList<Point> traps;
	private ArrayList<Point> lifes;

	private static final long serialVersionUID = 1L;


	public WoodField(Wood wood) {
		
		super();
		final File fileFir = new File("fir3.png");
		final File fileTrap = new File("trap3.png");
		final File fileHeart = new File("heart3.png");
		this.wood = wood;
		try {
			this.tree = ImageIO.read(fileFir);
			this.trap = ImageIO.read(fileTrap);
			this.life = ImageIO.read(fileHeart);
		} catch (IOException e) {
			e.printStackTrace();
		}		
		this.traps = wood.traps();
		this.lifes = wood.lifes();
		setVisible(false);
	}

	public void paintComponent(Graphics g) {
		for (int i = 0; i < wood.getLength(); i++) {
			for (int j = 0; j < wood.getWidth(); j++) {
				if (wood.getChar(new Point(i, j)) == '1')
					g.drawImage(tree, i*tree.getWidth(null), j*tree.getHeight(null), null);
			}
		}
		for (int i = 0; i < traps.size(); i++) {
			g.drawImage(trap, traps.get(i).getX()*tree.getWidth(null), traps.get(i).getY()*tree.getHeight(null), null);
		}
		for (int i = 0; i < lifes.size(); i++) {
			g.drawImage(life, lifes.get(i).getX()*tree.getWidth(null), lifes.get(i).getY()*tree.getHeight(null), null);
		}			
	}
	
	public int getWoodWidth() {
		return tree.getWidth(null)*wood.getLength();
	}
	public int getWoodHeight() {
		return tree.getHeight(null)*wood.getWidth();
	}
	

}

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

import javax.swing.JComponent;


public class WoodComponent extends JComponent {

	private static final long serialVersionUID = 1L;
	private Wood wood;
	private Image tree;
	private Image trap;
	private Image life;
	private ArrayList<Point> traps;
	private ArrayList<Point> lifes;

	public WoodComponent(Wood wood, Image tree, Image trap, Image heart) {
		this.wood = wood;
		this.tree = tree;
		this.trap = trap;
		this.life = heart;
		this.traps = wood.traps();
		this.lifes = wood.lifes();
	}

	public void paintComponent(Graphics g) {
		g.setColor(Color.white);
		g.fillRect(0, 0, wood.getLength()*tree.getWidth(null), wood.getWidth()*tree.getHeight(null));
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

		//g.drawImage(mouse, mouseX*woodOfFir.getWidth(null), mouseY*woodOfFir.getHeight(null), null);
	}
}


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class Wood implements IWood {
	protected HashMap<Point, Character> labyrinth; // инфа по стенам и тропинкам
	protected int labLength = 0;
	protected int labWidth = 0;
	protected HashMap<String, Woodman> listOfWoodmen = new HashMap<String, Woodman>(); // список игроков
	
	int getLength() {
		return this.labLength;
	}
	int getWidth() {
		return this.labWidth;
	}

	Wood(HashMap<Point, Character> lab, int length, int width) {
		this.labyrinth = lab;
		this.labLength = length;
		this.labWidth = width;
	}

	Character getChar(Point location) {
		return labyrinth.get(location);
	}

	Woodman getWoodman(String name) {
		return listOfWoodmen.get(name);
	}

	@Override
	public void createWoodman(String name, Point start, Point finish) {
		if (start.getX() > 0 && start.getY() > 0 && start.getX() < labLength - 1 && start.getY() < labWidth - 1)
			if (!listOfWoodmen.containsKey(name)) 
				listOfWoodmen.put(name, new Woodman(name, start, finish));
			else throw new WoodmanExistsException("Такой вудмен уже есть в лесу!");		
		else throw new WoodmanOnTheWallException("Нельзя создавать персонажа на стене!");
	}

	Action result(Point currentLocation, String name, Point newLocation) { 
		switch (getChar(newLocation)) {  // символ лабиринта, на который мы встанем
		case '0' : 
			getWoodman(name).SetLocation(newLocation);
			if (newLocation != getWoodman(name).finish) {			
				return Action.Ok;
			}
			else
				return Action.Finish;
		case '1' : 
			if (getChar(currentLocation) == 'K')
				if (!getWoodman(name).Kill()) {
					listOfWoodmen.remove(name);
					return Action.WoodmanNotFound;
				}	
			if (getChar(currentLocation) == 'L')
				getWoodman(name).AddLife();
			return Action.Fail;
		case 'L' :
			getWoodman(name).SetLocation(newLocation);
			getWoodman(name).AddLife();
			if (newLocation != getWoodman(name).finish) {
				return Action.Life;
			}
			else
				return Action.Finish;
		case 'K' :
			getWoodman(name).SetLocation(newLocation);
			if (getWoodman(name).Kill()) {
				if (newLocation != getWoodman(name).finish) {
					return Action.Dead;
				}
				else
					return Action.Finish;
			}
			else {
				listOfWoodmen.remove(name);
				return Action.WoodmanNotFound;
			}		
		}
		return Action.Ok;
	}
	@Override
	public Action move(String name, Direction direction) throws RuntimeException {
		if (!listOfWoodmen.containsKey(name))
			return Action.WoodmanNotFound;
		switch (direction) {
		case Up : 
			return result (getWoodman(name).GetLocation(), name, getWoodman(name).GetLocation().MoveUp());
		case Down :
			return result (getWoodman(name).GetLocation(), name, getWoodman(name).GetLocation().MoveDown());
		case Right :
			return result (getWoodman(name).GetLocation(), name, getWoodman(name).GetLocation().MoveRight());
		case Left :
			return result (getWoodman(name).GetLocation(), name, getWoodman(name).GetLocation().MoveLeft());
		case None :
			return result (getWoodman(name).GetLocation(), name, getWoodman(name).GetLocation());
		default: throw new RuntimeException("Введено неверное направление!");
		}
	}
	
	public ArrayList<Point> traps() {
		ArrayList<Point> result = new ArrayList<>();		
		for (int i = 0; i < getLength(); i++) {
			for (int j = 0; j < getWidth(); j++) {
				Point tempPoint = new Point(i, j);
				if (this.getChar(tempPoint) == 'K')
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
				if (this.getChar(tempPoint) == 'L')
					result.add(tempPoint);
			}
		}		
		return result;
	}
	
	public Point[] points() {		
		ArrayList<Point> temp = new ArrayList<>();
		Iterator<Point> keySetIterator = labyrinth.keySet().iterator();
		while (keySetIterator.hasNext()) {
			Point key = keySetIterator.next();
			switch (labyrinth.get(key)) {
			case '1' : break;
			default :
				temp.add(key); break;
			}
		}
		Point[] result = new Point[temp.size()];
		for (int i = 0; i < result.length; i++) {
			result[i] = temp.get(i);
		}
		return result;
	}

}

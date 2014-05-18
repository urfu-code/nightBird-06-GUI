package wood01;

import java.util.HashMap;
import java.util.Map;

import woodInterfaces.Wood;


public class TheWood implements Wood {
	
	protected char[][] wood;
	protected Map<String,TheWoodman>woodmans;
	private Map<String,Point>woodmansFinish;
	
	public TheWood(char[][] _wood) throws Exception {
		wood = _wood;
		woodmans = new HashMap<String,TheWoodman>();
		woodmansFinish = new HashMap<String,Point>();
	}
	
	@Override
	public void createWoodman(String name, Point start, Point finish) throws Exception {
		if (woodmans.containsKey(name)) {
			throw new WoodException("nameExisting");
		}
		else if ((start.getX() < 0)||(start.getY() < 0)||
			(start.getX() >= wood[0].length)||(start.getY() >= wood.length)) {
			throw new WoodException("isNotAWood");
		}
		else if (wood[start.getY()][start.getX()] == '1') {
			throw new WoodException("WoodmanInWall");
		}
		if ((finish.getX() < 0)||(finish.getY() < 0)||(finish.getX() > wood[0].length)||(finish.getY() >= wood.length)) {
			throw new WoodException("IncorrectFinish");
		}
		if (wood[finish.getY()][finish.getX()] == '1') {
			throw new WoodException("IncorrectFinish");
		}
		woodmans.put(name,new TheWoodman(name, start));
		woodmansFinish.put(name, finish);
	}

	@Override
	public Action move(String name, Direction direction) throws Exception {
		TheWoodman currentWoodman = woodmans.get(name);
		if (currentWoodman == null) {
			return Action.WoodmanNotFound;
		} 
		Point newPosition = currentWoodman.GetLocation();
		Action currentAction;
		
		switch (direction)  {
		
		case Up:
			newPosition = newPosition.MoveUp();
			break;
		case Down:
			newPosition = newPosition.MoveDown();
			break;
		case Left:
			newPosition = newPosition.MoveLeft();
			break;
		case None:
			break;
		case Right:
			newPosition = newPosition.MoveRight();
			break;
		default:
			break;
		}
		if ((newPosition.getX() < 0)||(newPosition.getX() >= wood[0].length)||
		    (newPosition.getY() < 0)||(newPosition.getY() >= wood.length)) {
			throw new Exception("вышли за границы игрового мира!");
		}

		switch(wood[newPosition.getY()][newPosition.getX()]) {
		
		case '1':
			currentAction = Action.Fail;
			break;
		case '0':
			currentWoodman.SetLocation(newPosition);
			currentAction = Action.Ok;
			break;
		case 'K':
			if (currentWoodman.Kill()) {
				currentWoodman.SetLocation(newPosition);
				currentAction = Action.Dead;
			}
			else {
				woodmans.remove(name);
				currentAction = Action.WoodmanNotFound;
			}
			break;
		case 'L':
			currentWoodman.AddLife();
			currentWoodman.SetLocation(newPosition);
			currentAction = Action.Life;
			break;
		default:
			throw new Exception("неопознанная клетка!");
		}
		if (currentAction == Action.Fail) {
			if (wood[currentWoodman.GetLocation().getY()][currentWoodman.GetLocation().getX()] == 'K') {
				if(currentWoodman.Kill()) {
					currentAction = Action.Fail;
				}
				else {
					woodmans.remove(name);
					currentAction = Action.WoodmanNotFound;
				}
			}
			else if (wood[currentWoodman.GetLocation().getY()][currentWoodman.GetLocation().getX()] == 'L') {
				currentWoodman.AddLife();
				currentAction = Action.Fail;
			}
		}
		if (woodmansFinish.get(currentWoodman.GetName()).equals(newPosition)) {
			woodmans.remove(name);	
			return Action.ExitFound;
		}
		return currentAction;
	}
	
	public boolean equalsOfWoods(TheWood eqWood) {
		for (int i = 0; i < wood.length; i++) {
			for (int j = 0; j < wood[0].length; j++) {
				if (wood[i][j] != eqWood.wood[i][j]) {
					return false;
				}
			}
		}
		return true;
	}
}

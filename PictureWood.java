import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class PictureWood extends JFrame {

	private static final long serialVersionUID = 1L;

	static Wood wood;
	static private boolean alreadyPushed = false;
	static private boolean notSelectedStart = true;
	static private boolean notSelectedFinish = true;
	static private boolean newGameIsPossible = false; 
	volatile private static boolean pressedStart;
	JComponent wf;

	public ArrayList<Point> starts;
	public ArrayList<Point> finishes;
	public ArrayList<String> names;
	public File fileWood;
	
	volatile JPanel panelForWoodAndNotes;

	public boolean isPressedStart() {
		return pressedStart;
	}
	public static void setPressedStart(boolean pressedStart) {
		PictureWood.pressedStart = pressedStart;
	}

	public void setNewGameIsPossible(boolean newGameIsPossible) {
		PictureWood.newGameIsPossible = newGameIsPossible;
	}

	PictureWood() throws FileNotFoundException {

		super("Night Bird");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		final String list[] = new File("woods/").list();
		for (int i = 0; i < list.length; i++) {
			list[i] = list[i].substring(0, list[i].indexOf('.'));
		}

		pressedStart = false;
		names = new ArrayList<>();
		starts = new ArrayList<>();
		finishes = new ArrayList<>();

		final Container content = getContentPane();	
		// части: в верхней кнопки-ввод, посередине кнопка "Начать игру", в нижней - само поле
		content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

		// часть для кнопок и ввода
		final JPanel panelForTools = new JPanel();
		panelForTools.setLayout(new GridLayout(1, 2, 5, 1));
		panelForTools.setMaximumSize(new Dimension(670, 100));
		// участок для выбора леса: надпись, список, кнопка
		JPanel panelForWoodChoice = new JPanel();
		panelForWoodChoice.setLayout(new GridLayout(3, 1, 2, 2));
		// участок для выбора точек
		final JPanel panelForPoints = new JPanel();
		panelForPoints.setLayout(new GridLayout(2, 2, 1, 1));
		//верхушка с приветствием и инструкцией
		final JPanel panelForIntro = new JPanel();
		panelForIntro.setLayout(new BoxLayout(panelForIntro, BoxLayout.Y_AXIS));
		panelForIntro.setMaximumSize(new Dimension(670, 80));
		JLabel intro1 = new JLabel("Вас приветствует GUI игры \"Night bird\". Прежде чем начать, выберите лес из списка ниже, нажимая");
		JLabel intro2 = new JLabel("\"Отрисовать\" для просмотра. Затем - точки старта и финиша. Кнопки с \"+\" позволяют добавить больше");
		JLabel intro3 = new JLabel("точек. После введите имена игроков, нажимая \"Ввести ...\", чтобы сохранить имя. Нажмите");
		JLabel intro4 = new JLabel("\"Начать игру\": отобразится легенда и запустится сервер с клиентами. Приятной игры");
		intro1.setAlignmentX(CENTER_ALIGNMENT);
		intro1.setFont(new Font("Serif", Font.PLAIN, 15));
		intro2.setAlignmentX(CENTER_ALIGNMENT);
		intro2.setFont(new Font("Serif", Font.PLAIN, 15));
		intro3.setAlignmentX(CENTER_ALIGNMENT);
		intro3.setFont(new Font("Serif", Font.PLAIN, 15));
		intro4.setAlignmentX(CENTER_ALIGNMENT);
		intro4.setFont(new Font("Serif", Font.PLAIN, 15));
		panelForIntro.add(intro1);
		panelForIntro.add(intro2);
		panelForIntro.add(intro3);
		panelForIntro.add(intro4);
		content.add(panelForIntro);

		JPanel panelForButtons = new JPanel();
		panelForButtons.setLayout(new GridLayout(1, 3, 2, 2));
		panelForButtons.setMaximumSize(new Dimension(670, 30));
		
		//нижняя часть окна, лес и легенда
		panelForWoodAndNotes = new JPanel();
		panelForWoodAndNotes.setLayout(new BoxLayout(panelForWoodAndNotes, BoxLayout.X_AXIS));

		final JLabel label = new JLabel(" ");
		label.setAlignmentX(LEFT_ALIGNMENT);
		label.setAlignmentY(TOP_ALIGNMENT);
		panelForWoodChoice.add(label, BorderLayout.WEST);

		// список для выбора леса. для того чтобы увидеть лес, нужно кликнуть на кнопку "Отрисовать"
		final WoodActionListener woodActionListener = new WoodActionListener(label);
		final JComboBox<String> dropdownList = new JComboBox<String>(list);
		dropdownList.setAlignmentX(LEFT_ALIGNMENT);
		dropdownList.setAlignmentY(CENTER_ALIGNMENT);
		dropdownList.addActionListener(woodActionListener);
		dropdownList.setMaximumSize(new Dimension(335, 50));
		panelForWoodChoice.add(dropdownList, BorderLayout.WEST);

		// списки для выбора точек старта или финиша. выбор осуществляется кликом по конкретной строке списка
		final JComboBox<Point> boxOfStarts = new JComboBox<Point>();
		boxOfStarts.setPreferredSize(new Dimension(100, 25));
		boxOfStarts.setSelectedIndex(-1);
		boxOfStarts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (boxOfStarts.getSelectedIndex() == -1)
					label.setText("");
				if (!notSelectedStart && boxOfStarts.getSelectedIndex() != -1) {
					notSelectedStart = true;
				}
			}
		});
		final JComboBox<Point> boxOfFinishes = new JComboBox<Point>();
		boxOfFinishes.setPreferredSize(new Dimension(100, 25));
		boxOfFinishes.setSelectedIndex(-1);
		boxOfFinishes.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (boxOfFinishes.getSelectedIndex() == -1)
					label.setText("");
				if (!notSelectedFinish && boxOfFinishes.getSelectedIndex() != -1) {
					notSelectedFinish = true;
				}
			}
		});
		panelForPoints.add(boxOfStarts);
		panelForPoints.add(boxOfFinishes);

		// кнопки для добавления точек старта и финиша. по сути сбрасывают выбранный элемент в меню, добавив его предварительно в список
		final JButton addStartPoint = new JButton("+ start");
		addStartPoint.setAlignmentX(LEFT_ALIGNMENT);
		addStartPoint.setMaximumSize(new Dimension(100, 50));		
		panelForPoints.add(addStartPoint);
		final JButton addEndPoint = new JButton("+ finish");
		addEndPoint.setAlignmentX(RIGHT_ALIGNMENT);
		addEndPoint.setMaximumSize(new Dimension(100, 50));		
		panelForPoints.add(addEndPoint);

		addStartPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (notSelectedStart) {				
					Point item = (Point)boxOfStarts.getSelectedItem();
					starts.add(item);
					label.setText(item.toString());
					notSelectedStart = false;
					boxOfStarts.setSelectedIndex(-1);
					label.setText("Можно выбрать новую точку входа");
				}

			}
		});
		addEndPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (notSelectedFinish) {
					Point item = (Point)boxOfFinishes.getSelectedItem();
					finishes.add(item);
					label.setText(item.toString());
					notSelectedFinish = false;
					boxOfFinishes.setSelectedIndex(-1);
					label.setText("Можно выбрать новую точку выхода");
				}
			}
		});

		//по нажатию этой кнопки будет обновляться/становиться видимой панель с игровым полем
		final JButton butt = new JButton("Отрисовать");
		butt.setAlignmentX(LEFT_ALIGNMENT);
		butt.setAlignmentY(BOTTOM_ALIGNMENT);
		butt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {		
					// отрисовка нового изображения. проверка, в первый ли раз нажали "Отрисовать"
					if (alreadyPushed) {
						panelForWoodAndNotes.remove(0);
						boxOfStarts.removeAllItems();
						boxOfFinishes.removeAllItems();
						starts.clear();
						finishes.clear();
					}
					else
						alreadyPushed = true;
					wood = new WoodLoader().Load(new FileInputStream(woodActionListener.getWoodFile()));
					wf = new WoodField(wood);
					fileWood = woodActionListener.getWoodFile();
					// заполнение меню точек старта и финиша
					for (int i = 0; i < wood.points().length; i++) {
						boxOfStarts.addItem(wood.points()[i]);
						boxOfFinishes.addItem(wood.points()[i]);
					}
					boxOfStarts.setSelectedIndex(-1);
					boxOfStarts.repaint();
					boxOfFinishes.setSelectedIndex(-1);
					boxOfFinishes.repaint();

					panelForWoodAndNotes.add(wf);
					wf.setPreferredSize(new Dimension(wf.getWidth(), wf.getHeight()));

					wf.setVisible(true);
				} catch (LabirynthIsNotRect | NullPointerException | IOException | ArrayIndexOutOfBoundsException e1) {
					if (e1.getClass() == ArrayIndexOutOfBoundsException.class) {
						label.setText("Повторное нажатие \"Начать игру\"");
						alreadyPushed = false;
					} else
					if (e1.getClass() == NullPointerException.class) {
						label.setText("Сначала выбери лес");
						alreadyPushed = false;
					} else
					if (e1.getClass() == IOException.class)
						System.out.println("Проблемы с картинкой");
					else
						e1.printStackTrace();
					
				}

			}
		});
		panelForWoodChoice.add(butt, BorderLayout.WEST);

		JPanel panelForInput = new JPanel(); 
		panelForInput.setMaximumSize(new Dimension(350, 30));
		panelForInput.setLayout(new GridLayout(1, 2, 5, 1));
		final JTextField nameInput = new JTextField(20);

		final JButton input = new JButton("Ввести имя");
		input.setAlignmentX(RIGHT_ALIGNMENT);
		input.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (names.size() == 10)
					label.setText("Больше нет картинок для мышей");
				else {
					Pattern p = Pattern.compile("^[A-Z]*[a-z]+$");
					Matcher m = p.matcher(nameInput.getText());
					if (m.find()) {
						names.add(nameInput.getText());
						nameInput.setText("");
						label.setText("");
					} else
						label.setText("Некорректное имя, допустима латиница");			
				}
			}
		});
		panelForInput.add(nameInput);
		panelForInput.add(input);

		final JButton clean = new JButton("Сброс");
		clean.setAlignmentX(LEFT_ALIGNMENT);
		clean.setPreferredSize(new Dimension(223, 30));
		clean.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					panelForWoodAndNotes.remove(0);
					names.clear();
					starts.clear();
					finishes.clear();
					boxOfStarts.removeAllItems();
					boxOfFinishes.removeAllItems();
					boxOfStarts.setSelectedIndex(-1);
					boxOfFinishes.setSelectedIndex(-1);
					dropdownList.setSelectedIndex(-1);
					label.setText("");
					alreadyPushed = false;
				} catch (ArrayIndexOutOfBoundsException e1) {
					label.setText("Нечего сбрасывать");
				}
			}
		});


		JButton start = new JButton("Начать игру");
		start.setAlignmentX(CENTER_ALIGNMENT);
		start.setPreferredSize(new Dimension(223, 30));
		//				start.setIcon(new ImageIcon("Play_Button4.png"));
		//				start.setBorder(BorderFactory.createEmptyBorder());
		//				start.setContentAreaFilled(false);
		start.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (names.size() == 0)
					label.setText("Не был создан ни один игрок");
				else 
					if (starts.size() == 0 || finishes.size() == 0)
						label.setText("Не было выбрано ни одной точки старта или финиша");
					else {
						dropdownList.setEnabled(false);
						butt.setEnabled(false);
						addStartPoint.setEnabled(false);
						addEndPoint.setEnabled(false);
						boxOfStarts.setEnabled(false);
						boxOfFinishes.setEnabled(false);													
						panelForIntro.removeAll();											
						panelForIntro.setVisible(false);
						nameInput.setEnabled(false);
						input.setEnabled(false);
						clean.setEnabled(false);

						content.remove(panelForIntro); //0
						content.repaint();
						label.setText("");

					}
				setPressedStart(true);
			}
		});

		// новая игра доступна, когда все игроки дошли до финиша/умерли
		// возможность играть новую игру на новом поле не предусмотрена, так как придётся переделывать сервер
		JButton newGame = new JButton("Новая игра");
		newGame.setAlignmentX(RIGHT_ALIGNMENT);
		newGame.setPreferredSize(new Dimension(223, 30));
		newGame.addActionListener(new ActionListener() {		
			@Override
			public void actionPerformed(ActionEvent e) {
				if (!newGameIsPossible)
					label.setText("Новая игра недоступна");
				else {				
//					panelForWoodAndNotes.removeAll();;
					names.clear();
					starts.clear();
					finishes.clear();
//					boxOfStarts.removeAllItems();
//					boxOfFinishes.removeAllItems();
					boxOfStarts.setSelectedIndex(-1);
					boxOfFinishes.setSelectedIndex(-1);
//					dropdownList.setSelectedIndex(-1);
					label.setText("");

//					dropdownList.setEnabled(true);
//					butt.setEnabled(true);
					addStartPoint.setEnabled(true);
					addEndPoint.setEnabled(true);
					boxOfStarts.setEnabled(true);
					boxOfFinishes.setEnabled(true);
					nameInput.setEnabled(true);
					input.setEnabled(true);
					clean.setEnabled(true);
					newGameIsPossible = false;
					alreadyPushed = false;
				}
			}
		});

		panelForButtons.add(clean);
		panelForButtons.add(start);
		panelForButtons.add(newGame);

		panelForTools.add(panelForWoodChoice, BorderLayout.WEST);
		panelForTools.add(panelForPoints, BorderLayout.EAST);
		content.add(panelForTools, 1);	
		content.add(panelForInput, 2);
		content.add(panelForButtons, 3);
		content.add(panelForWoodAndNotes, 4);

		setPreferredSize(new Dimension(670, 850)); // размер окна подогнан под размер самого широкого леса. не нашла способ менять размер окна динамически. со скроллбаром тоже беда
		pack();
		setLocationRelativeTo(null);
		setVisible(true);

	}


	public static void main(String[] args) throws IOException {

		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new PictureWood();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		});

	}

//	public void render() {
//		Graphics g = wf.getGraphics();
//		//		Iterator<String> keySetIterator = namesAndLocations.keySet().iterator();
//		//		while (keySetIterator.hasNext()) {
//		//			String key = keySetIterator.next();
//		//			namesAndImages.get(key).draw(g, namesAndLocations.get(key).getX() * wf.getWoodWidth(), namesAndLocations.get(key).getY() * wf.getWoodHeight());
//		//				g.dispose();
//		wf.repaint();
//	}
}

//		while (true) {
//			for (int i = 1; i < wood.getLength(); i++) {
//				for (int j = 1; j < wood.getWidth(); j++) {
//					if (wood.getChar(new Point(i, j)) == '0') {
//						mouseX = i;
//						mouseY = j;
//						component.repaint();
//						try {
//							Thread.sleep(500);
//						} catch (InterruptedException e) {
//							e.printStackTrace();
//						}
//					}
//				}
//			}
//
//
//		}

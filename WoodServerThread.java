import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/*
 * заранее загружаем все леса во фрейме, потом получаем выбранный лес от него 
 * в main создаём фрейм, на котором уже будут подготовлены кнопки и списки. пока не будет нажата кнопка "Начать игру", не создаём сокет и не идём дальше.
 * до этого идёт выбор леса и точек. массивы точек тоже берутся из фрейма
 * Handler'у отдаём конкретные две точки и имя. также отдаём фрейм PrintableWood, там будем работать непосредственно с игроками
 * 
 * в PictureWood загружаем картинки с мышками и элементы лабиринта. в конструуторе инициализируем принятый фрейм и отрисовывам выбранный "окончательно" лабиринт --
 * в той же области, в которой отрисовываем по кнопке "Отрисовать". (или оставлять последний выбранный лес?)
 * хэшмэп соответствий картинок/спрайтов и имён, -//- и точек
 * переопределить printLabyrinth, выводить легенду сбоку
 * 
 * кнопка "Сброс"/"Новая игра" (становится активна, когда освободились все картинки (флаг?))
 */

public class WoodServerThread {
	private static ArrayList<Point> starts;
	private static ArrayList<Point> finishes;
	volatile static PictureWood frame;

	volatile static ConcurrentHashMap<String, Thread> listOfClients = new ConcurrentHashMap<String, Thread>();

	public static void main(String[] args) throws URISyntaxException, InterruptedException {
		
		try {
			frame = new PictureWood();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		while (!frame.isPressedStart()) {
		}
		
		starts = frame.starts;
		finishes = frame.finishes;
		

		try {
			ServerSocket forClients = new ServerSocket(32015); // для создания сокетов и потоков-клиентов
			
			try {
				FileInputStream stream = new FileInputStream(frame.fileWood);
				PrintableWoodLoader wl = new PrintableWoodLoader();
				PrintableWood wood = wl.Load(stream, frame);
				
				Notifier notty = new Notifier(listOfClients);
				Thread nottyThread = new Thread(notty);
				nottyThread.start();
				Terminator Arny = new Terminator();
				Arny.start();

				Random chooseThePoint = new Random();
				Point tempStart;
				Point tempFinish;

//				forClients.setSoTimeout(1000); // убрать за ненадобностью?
				ArrayList<String> namesForClients = frame.names;
				int i = 0;
				
				while (true) {
					// обновляем, так как после выбора новой игры точки выбираются заново
					starts = frame.starts;
					finishes = frame.finishes;
//					if (Arny.flag) {
//						System.out.println("SERVER STOPPED");
//						throw new Termination();
//					}

					Thread th;
					try {

						tempStart = starts.get(chooseThePoint.nextInt(starts.size()));
						tempFinish = finishes.get(chooseThePoint.nextInt(finishes.size()));
						th = new Thread(new Handler(forClients.accept(), wood, tempStart, tempFinish, listOfClients, namesForClients.get(i)));
						
					} catch (SocketTimeoutException e) {
						continue;
					}
					
					synchronized (listOfClients) {
						listOfClients.put(namesForClients.get(i), th);
					}
					th.start();
					System.out.println(listOfClients.size() + " clients!");
					
					namesForClients.remove(i);

				}
			} catch (IOException | NullPointerException e) {
				if (e.getClass() == NullPointerException.class) {
					e.printStackTrace();
					forClients.close();
					System.exit(0);
				}
				else
					e.printStackTrace();
			}

//			catch (Termination r) {
//				Iterator<String> keySetIterator = listOfClients.keySet().iterator();
//
//				while (keySetIterator.hasNext()) {
//					String key = keySetIterator.next();
//					synchronized (listOfClients.get(key)) {
//						listOfClients.remove(key);
//					}	
//				}
//
//				forClients.close();
//				System.exit(0);
//			}

		} catch (IOException e) {
			if (e.getClass().equals(BindException.class)) {
				System.out.println("Была предпринята более чем одна попытка запустить сервер, и всё сломалось. Программа была вынуждена закрыться");
				System.exit(0);
			}
			else 
				e.printStackTrace();
		}

	}

}

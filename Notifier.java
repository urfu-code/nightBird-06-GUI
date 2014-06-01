import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;


public class Notifier implements Runnable {
	private ConcurrentHashMap<String, Thread> listOfClients;

	public Notifier(ConcurrentHashMap<String, Thread> listOfClients) {
		this.listOfClients = listOfClients;
	}

	@Override
	public void run() {

		while (true) {			

			Iterator<String> keySetIterator = listOfClients.keySet().iterator();

			while (keySetIterator.hasNext()) {
				String key = keySetIterator.next();
				synchronized (listOfClients.get(key)) {
					listOfClients.get(key).notify();
				}

				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

			}

		}

	}

}

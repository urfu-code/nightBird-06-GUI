
public class Synchronizer implements Runnable {
	
	private PrintableWood wood;
	
	public Synchronizer(PrintableWood wood) {
		this.wood = wood;
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				synchronized (this) {
					notifyAll();
				}
				Thread.sleep(500);
				wood.repaint();
			}
			catch (InterruptedException e) {
				break;
			}
		}
	}	
}
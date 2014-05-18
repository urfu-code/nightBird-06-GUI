package Network;


import wood01.PrintableTheWood;


public class TheWoodServerThreadSyncronizer implements Runnable {
	private PrintableTheWood wood;
	
	public TheWoodServerThreadSyncronizer(PrintableTheWood wood) {
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
				wood.repaintWood();
			} catch (InterruptedException e) {
				break;
			} catch (Exception e) {
				System.out.println("Ошибка в отрисовке!");
				e.printStackTrace();
			}
		}
	}
	
	
}

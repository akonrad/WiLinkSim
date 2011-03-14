package simulator;

import java.util.LinkedList;

/**
 * The generic class Buffer creates a buffer containing elements of type T
 * 
 * @author 	Kat Villariba
 * @version 1.0 2011-MAR-13
 * @param 	<T>
 */
public class Buffer<T> {
	
	private LinkedList<T> queue;
	
	public Buffer () {
		queue = new LinkedList<T>();
	}
	
	/**
	 * @return An item from the beginning of the queue
	 */
	public synchronized T get() {
		T item;
		
		while (queue.isEmpty() == true) {
			try {
				wait();
			} catch (InterruptedException e) { }
		} // while

		notify();
		item = queue.removeFirst();
		System.out.println("Got item #: " + ((IPPacket) item).getIdentification());

		return item;
	}
	
	/**
	 * @param An item to add to the end of the queue
	 */
	public synchronized void put(T item) {
		queue.add(item);
		System.out.println("Put item #: " + ((IPPacket) item).getIdentification());

		notify();
	}
}

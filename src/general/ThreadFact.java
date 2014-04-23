package general;

import java.util.concurrent.ThreadFactory;

/**
 * @author Josip
 * @version 03.03.14
 */
public class ThreadFact implements ThreadFactory {

	private boolean creatingDaemons = false;

	public boolean isCreatingDaemons() {
		return creatingDaemons;
	}

	public void setCreatingDaemons(boolean creatingDaemons) {
		this.creatingDaemons = creatingDaemons;
	}

	/**
	 * Constructs a new {@code Thread}.  Implementations may also initialize
	 * priority, name, daemon status, {@code ThreadGroup}, etc.
	 *
	 * @param r a runnable to be executed by new thread instance
	 * @return constructed thread, or {@code null} if the request to
	 * create a thread is rejected
	 */
	@Override
	public Thread newThread(Runnable r) {
		Thread thread = new Thread(r);
		thread.setDaemon(creatingDaemons);
		return thread;
	}
}

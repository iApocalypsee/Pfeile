package general;

import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A really efficient working game loop. The only drawback is (I think atleast)
 * that it is optimized for real time games, not turn based games like our game.
 * @author Josip Palavra
 * @version 23.06.2014
 */
public class GameLoop {

	public static final double SECOND_AS_NANO = 1000000000.0, SECOND_AS_MILLI = 1000.0;

    /**
     * The queue of every callback scheduled to be called once by the main thread.
     * Every element is called at the beginning of an update cycle, the queue will be cleared after that.
     */
    private static final Queue<VoidConsumer> onceScheduled = new LinkedBlockingQueue<>(),
                                             regularScheduled = new LinkedBlockingQueue<>();

	private static boolean runFlag = false;

	public static void run(double delta) {
		runFlag = true;

		double nextTime = (double) System.nanoTime() / SECOND_AS_NANO;
		double maxTimeDiff = 0.5;
		int skippedFrames = 1;
		int maxSkippedFrames = 5;

		while(runFlag) {
			double currTime = (double) System.nanoTime() / SECOND_AS_NANO;
			if((currTime - nextTime) > maxTimeDiff)
                nextTime = currTime;

			if(currTime >= nextTime) {
				// assign the time for the next update
				nextTime += delta;
				update();

				if((currTime < nextTime) || (skippedFrames > maxSkippedFrames)) {
					draw();
					skippedFrames = 1;
				} else {
					skippedFrames++;
				}
			} else {
				// calculate the time to sleep
				int sleepTime = (int) (SECOND_AS_MILLI * (nextTime - currTime));
				// sanity check
				if(sleepTime > 0) {
					// sleep until the next update
					try {
						Thread.sleep(sleepTime);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public static boolean isRunFlag() {
		return runFlag;
	}

	public static void setRunFlag(boolean runFlag) {
		GameLoop.runFlag = runFlag;
	}

    /**
     * Schedules given callback for one-time execution at the next update cycle.
     * @param callback The callback to be executed in the main thread at the beginning of the next
     *                 update cycle. This callback will get deleted after the call.
     */
    public static void scheduleOnce(VoidConsumer callback) {
        onceScheduled.offer(callback);
    }

    /**
     * Schedules given callback for execution every time the game loop enters the update stage.
     * @param callback The callback to be executed in the main thread
     */
    public static void schedule(VoidConsumer callback) {
        regularScheduled.offer(callback);
    }

    /**
     * Update logic.
     */
    private static void update() {
        regularScheduled.forEach(VoidConsumer::call);
        onceScheduled.forEach(VoidConsumer::call);
        onceScheduled.clear();

        Main.getGameWindow().update();
    }

    /**
     * Self-explanatory.
     */
    private static void draw() {
        Main.getGameWindow().draw();
    }

}

package general;

import java.awt.*;

/**
 * A really efficient working game loop. The only drawback is (I think atleast)
 * that it is optimized for real time games, not turn based games like our game.
 * @author Josip Palavra
 * @version 23.06.2014
 */
public class GameLoop {

	public static final double SECOND_AS_NANO = 1000000000.0, SECOND_AS_MILLI = 1000.0;

	private static boolean runFlag = false;

	public static void run(double delta) {
		runFlag = true;

		double nextTime = (double) System.nanoTime() / SECOND_AS_NANO;
		double maxTimeDiff = 0.5;
		int skippedFrames = 1;
		int maxSkippedFrames = 5;
        int count = 0;
		while(runFlag) {
			double currTime = (double) System.nanoTime() / SECOND_AS_NANO;
			if((currTime - nextTime) > maxTimeDiff)
                nextTime = currTime;

			if(currTime >= nextTime) {
				// assign the time for the next update
				nextTime += delta;
				Main.getGameWindow().update();
                count++;
                if (count % 2 == 0) {
                    if (Main.getContext().getActivePlayer().life().getLife() > 0)
                        Main.getContext().getActivePlayer().life().setLife(
                                 Main.getContext().getActivePlayer().life().getLife() - 0.5);
                }

				if((currTime < nextTime) || (skippedFrames > maxSkippedFrames)) {
					Main.getGameWindow().draw();
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
}

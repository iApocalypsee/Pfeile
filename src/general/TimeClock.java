package general;

import animation.SoundEffectTimeClock;
import comp.Component;
import general.property.StaticProperty;
import gui.screen.*;
import scala.concurrent.duration.Duration;
import scala.concurrent.duration.Duration$;
import scala.concurrent.duration.FiniteDuration;

import java.awt.*;


/**
 * Klasse f�r Zeitbeschr�nkung und Anzeige.
 * <br><br>
 * <b>4.1.2014:</b> TimeClock erbt jetzt von Component. TimeClock ist jetzt in besserer
 * Handhabung.
 * 
 * @version 4.1.2014
 * 
 */
public class TimeClock extends Component implements Runnable {
	
	// VARIABLEN - INITIALISIERUNG
	
	/** Variable, ob die Zeit abl�uft: 
	 * true: Zeit l�uft ab
	 * false: TimeClock ist gestoppt */
	private boolean isRunning = false;

    private long sumTime = 0;

    /** The default value the timer has; It is also the first time, when the effect <code>timeEffects()</code> is triggered.
     * It's a little more than 10 seconds, because the screen needs some time to update itself, so these 50ms are just
     * synchronizing the audio effects with the screen-system [=> update rate in <code>GameLoop</code>: 1/60s]. <p>
     * Compare with {@link general.TimeClock#timer}.*/
    private final int DEFAULT_TIMER = 10050;

    /** the time after which the next side effect {@link TimeClock#timeEffects()} is called. This will regulate a second
     * difference between the start of two {@link animation.SoundEffectTimeClock#play_tickingNoise()} or rather
     * {@link animation.SoundEffectTimeClock#play_tickingCriticalNoise()}.
     * It's default value is <code>10sec</code> (<code>DEFAULT_TIMER</code>) because it's the time of the first effect. */
    private int timer = DEFAULT_TIMER;

    private static Color brightDarkGrey = Color.DARK_GRAY.brighter();

    private Color colorTime = Color.RED;

    /** this is the color, which is shown, when the time is very low (<3000 ms) */
    private Color colorVeryLowLife = new Color(222, 6, 0);

    /** this is the color, which is shown, when the time is low (<10000 ms) */
    private Color colorLowLife = new Color (118, 1, 0);

    /** this String displays the time */
	private String timePrintString = "null";

	public final Function0Delegate onTimeOver = new Function0Delegate();

    private static StaticProperty<FiniteDuration> _turnTime = new StaticProperty<>();

	// KONSTURCKTOR
	public TimeClock (PfeileContext context) {
        // these values put the underlying component directly in the upper middle of the screen.
		super(GameWindow.WIDTH / 2 - 72 / 2, 25, 72, 26,
				GameScreen.getInstance());
		stop();
        colorTime = Color.BLACK;

        ScreenManager sm = Main.getGameWindow().getScreenManager();

        onTimeOver.registerJava(() -> {
            switch (sm.getActiveScreenIndex()) {
                case GameScreen.SCREEN_INDEX:
                    context.getTurnSystem().increment();
                    break;
                case ArrowSelectionScreen.SCREEN_INDEX:
                    context.getTurnSystem().increment();
                    break;
                case AimSelectionScreen.SCREEN_INDEX:
                    context.getTurnSystem().increment();
                    break;
                case InventoryScreen.SCREEN_INDEX:
                    context.getTurnSystem().increment();
                    break;
                case WaitingScreen.SCREEN_INDEX:
                    throw new IllegalStateException("TimeClock must be paused during the Waiting Screen. " +
                            "There is no active player during WaitingScreen, even though a player is assigned.");
                default:
                    LogFacility.log("Time out! The active Screen is neither GameScreen nor Arrow-/AimSelectionScreen or InventoryScreen. " +
                            "Register it! ActiveScreen... " + sm.getActiveScreen(), LogFacility.LoggingLevel.Debug);
            }
        });

        final TurnSystem turnSystem = context.getTurnSystem();
        turnSystem.onTurnGet().registerJava(p -> {
            reset();
            start();
        });
        turnSystem.onTurnEnded().registerJava(p -> {
            stop();
        });
    }

    /** �bernimmt timeSinceLastFrame + Aufruf durch 'Main'
	 *  .... updated die aktuelle Zeit; �bernimmt Thread */
	@Override
	public void run() {
        long lastTime = System.currentTimeMillis();

		while (!Thread.currentThread().isInterrupted()) {
            /** the time at which the beginning of this calculation begins (with <code>System.currentTimeMillis()</code>) */
            long timeCurrent = System.currentTimeMillis();

			if (isRunning()) {
                sumTime = sumTime + (timeCurrent - lastTime);

                /** <code>getMilliDeath()</code> or <code>turnTime().toMillis() - sumTime</code> */
                long timeLeft = getMilliDeath();

				if (timeLeft <= 0) {
                    // if the time has been run out, the explosion sound effect reassures, that the player notice the reason it.
                    SoundEffectTimeClock.play_explosion();
                    isRunning = false;
                    timePrintString = timeFormatter(0);
					onTimeOver.apply();
				} else {
                    timePrintString = timeFormatter (timeLeft);

                    if (timeLeft <= timer) {
                        timeEffects();
                    }

                    try {
                        // only 1 millisecond if timeFormatter(long) is used
                        // even 1000 (or 999) if timeFormatterShort(long) is used
                        Thread.sleep(1);
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }
			} else {
				try {
					Thread.sleep(35);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
            lastTime = timeCurrent;
		}
	}

	/** stoppt die Ausf�hrung von TimeClock */
	public synchronized void stop () {
		isRunning = false;
	}
	
	/** started TimeClock */
	public void start () {
		isRunning = true;
	}
	
	/** setzt TimeClock auf maximale Zeit zur�ck
	 * HINWEIS: an Start/Stop wird nicht ge�ndert, also ggf. stop / start aufrufen */
	public synchronized void reset() {
        // resetting these values: default timePrintString color, default time, default time for next timeEffects()-call
        // and the default printed time with timePrintString
        colorTime = Color.BLACK;
		sumTime = 0;
        timer = DEFAULT_TIMER;
        timePrintString = timeFormatter(turnTime().toMillis());
	}
	
	
	/** Umwandlung einer long-Variable in einem String min:sec:ms */
	public static String timeFormatter(long milliSecTime) {
        String time;
        if(milliSecTime <= 0) {
            time = "00:00:000";
            //throw new RuntimeException("Negativ time value provided");

        //  } else if (milliSecTime > 357539999){
        //      throw new RuntimeException("Time value exceeds allowed format");
        } else {
          long min = milliSecTime / (60 * 1000);
          long sec = (milliSecTime - min * 60 * 1000) / 1000;
          long ms = milliSecTime - min * 60 * 1000 - sec * 1000;
          
          if (min <= 0)
        	  time = "00";
          else if (min < 10) 
        	  time = "0" + min;
          else 
        	  time = "" + min;
          
          time = time + ":";
          
          if (sec <= 0)
        	  time = time + "00";
          else if (sec < 10)
        	  time = time + "0" + sec;
          else 
        	  time = time + sec;
          
          time = time + ":";
          
          if (ms <= 0) 
        	  time = time + "000";
          else if (ms < 10) 
        	  time = time + "00" + ms;
          else if (ms < 100)
        	  time = time + "0" + ms;
          else 
        	  time = time + ms;
        }
        return time;
	}
	
	/** Umwandlung einer Zeitangabe in Millisekunden in einen String [min:sec] */
	public static String timeFormatterShort(long milliSecTime) {
        String time = null;
        if(milliSecTime <= 0) {
            time = "00:00";
            //throw new RuntimeException("Negativ time value provided");

        //} else if (milliSecTime>357539999) {
        //    throw new RuntimeException("Time value exceeds allowed format");
        } else {
           milliSecTime = milliSecTime/1000000;
           long min= (milliSecTime/60);
           long sec= milliSecTime-min*60;
          
           if(min < 10 && sec < 10){
                 time = "0"+min+":"+"0"+sec;
           }
           if(min > 10 && sec < 10){
               time = ""+min+":"+"0"+sec;
           }
           if(min < 10 && sec > 10){
               time = "0"+min+":"+sec;
           }
           if(min > 10 && sec > 10){
               time = ""+min+":"+sec;
           }
        } 
        return time;
    }

    /** Every special effect (i.e. for easier noticing) is controlled here.
     * Right now, there is the sound and the change of color.
     *
     * Add new Effects here by the syntax: <p>
     *     case: theTimeUnderOrEqualToTheEffectShouldBePlayed : theEffect  break;
     *        <i> // maybe you need to add the end of that effect to the {@link TimeClock#reset()} method [i.e. if there need to be the standard color]</i> */
    private void timeEffects () {
        switch (timer) {
            case 1050: SoundEffectTimeClock.play_tickingCriticalNoise(); break;
            case 2050: SoundEffectTimeClock.play_tickingCriticalNoise(); break;
            case 3050: SoundEffectTimeClock.play_tickingCriticalNoise(); colorTime = colorVeryLowLife; break;
            case 4050: SoundEffectTimeClock.play_tickingNoise(); break;
            case 5050: SoundEffectTimeClock.play_tickingNoise(); break;
            case 6050: SoundEffectTimeClock.play_tickingNoise(); break;
            case 7050: SoundEffectTimeClock.play_tickingNoise(); break;
            case 8050: SoundEffectTimeClock.play_tickingNoise(); break;
            case 9050: SoundEffectTimeClock.play_tickingNoise(); break;
            case 10050:SoundEffectTimeClock.play_tickingNoise(); colorTime = colorLowLife; break;
        }
        // next time it's one second earlier
        timer = timer - 1000;
    }
	
	/**
	 * gibt zur�ck ob der Zug enden muss oder nicht (entspricht
     * <code>return getMilliDeath() < 0</code>)
	 *
	 * @return true - wenn die maximale Zeit pro Zug ('timeMax') ohne die Vergangene Zeit ('sumTime') kleiner als 0
	 */
	public synchronized boolean isEnd () {
        return turnTime().toMillis() - sumTime < 0;
	}
	
	/** GETTER 
	 * @return timePrintString*/ 
	public synchronized String getTimePrintString () {
		return timePrintString;
	}
	
	/** <code>turnTime().toMillis - sumTime </code> (sumTime ist die abgelaufene Zeit)
	 * @return timeLeft - die �brige Zeit f�r diesen Zug
	 */
	public synchronized long getMilliDeath() {
		return turnTime().toMillis() - sumTime;
	}
	
	public boolean isRunning() {
		return isRunning;
	}

    /** Returns the time in which a player is allowed to make moves. <p>
     *
     * If the underlying turn time variable is null, this method returns <code>Duration.Inf</code> (scala) /
     * <code> scala.concurrent.duration.Duration$.MODULES$.Inf()</code> (java),
     * otherwise it returns the underlying turn time variable directly. <p>
     *
     * For direct time calculation, use time conversion methods provided with the Duration object:
     * <code>toMillis, toNanos, toMinutes, toSeconds</code>
     */
    public static Duration turnTime ()
    {
        if(_turnTime.get() == null)
            return Duration$.MODULE$.Inf();
        else
            return _turnTime.get();
    }

    /** Sets the new turn time.
     *
     * The new value may be <code>null</code>. In case of <code>null</code> the turn time
     * defaults to infinite time.
     */
    public static void setTurnTime (FiniteDuration turnTime) {
        if (!turnTime.isFinite())
            _turnTime.set(null);
        _turnTime.set(turnTime);
    }

    /** Returns true if the turn time is infinite. */
    public static boolean isTurnTimeInfinite () {
        return _turnTime.isEmpty();
    }

	@Override
	public void draw(Graphics2D g) {
		g.setColor(brightDarkGrey);
		g.fillRoundRect(getX() - 2,
				getY() - 2,
				getWidth() + 4,
				getHeight() + 4, 8, 5);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(getX(),
				getY(),
				getWidth(),
				getHeight(), 30, 12);
		
		g.setColor(colorTime);
		g.setFont(STD_FONT);
        g.drawString(getTimePrintString(), getX() + 4, getY() + 16);
	}
}

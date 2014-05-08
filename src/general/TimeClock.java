package general;

import gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import comp.Border;
import comp.Component;


/**
 * Klasse für Zeitbeschränkung und Anzeige.
 * <br><br>
 * <b>4.1.2014:</b> TimeClock erbt jetzt von Component. TimeClock ist jetzt in besserer
 * Handhabung.
 * 
 * @version 4.1.2014
 * 
 */
@SuppressWarnings("unused")
public class TimeClock extends Component implements Runnable {
	
	// VARIABLEN - INITIALISIERUNG
	
	/** Variable, ob die Zeit abläuft: 
	 * true: Zeit läuft ab
	 * false: TimeClock ist gestoppt */
	private boolean isRunning;
	
	/** übrige Zeit (in Millisekunden) bis der Timmer abläuft */
	private long timeLeft = Mechanics.timePerPlay; 
	
	/** letzter Zeitpunkt der Berechnungen */
	private long lastTime;
	
	/** aktuelle Zeit */ 
	private long timeCurrent = 0;
	
	/** String, der am Bildschirm die Zeit angeben soll */
	private String timePrintString = null;
	
	// KONSTURCKTOR
	/** KONSTRUCKTOR der Klasse 
	 */
	public TimeClock () {
		super(Main.getWindowWidth() / 2 - 72 / 2, 25, 72, 26, 
				(GameScreen) Main.getGameWindow().getScreenManager().getScreens().get(GameScreen.SCREEN_INDEX));
		stop();
	}
	
	/** Übernimmt timeSinceLastFrame + Aufruf durch 'Main'
	 *  .... updated die aktuelle Zeit; übernimmt Thread */
	@Override
	public void run() {
		lastTime = System.currentTimeMillis(); 
		long sumTime = 0;
		while (true) {
			if (isRunning()) {
				timeCurrent = System.currentTimeMillis(); 
				sumTime = sumTime + (timeCurrent - lastTime);
				timePrintString = timeFormatter (Mechanics.timePerPlay - sumTime);
				lastTime = timeCurrent;
			} else {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {e.printStackTrace();}
			}
		}
	}

	/** stoppt die Ausführung von TimeClock */
	public void stop () {
		isRunning = false;
	}
	
	/** started TimeClock */
	public void start () {
		isRunning = true;
	}
	
	/** setzt TimeClock auf maximale Zeit zurück
	 * HINWEIS: an Start/Stop wird nicht geändert, also ggf. stop / start aufrufen */
	public synchronized void reset() {
		timeCurrent = 0;
	}
	
	
	/** Umwandlung einer long-Variable in einem String min:sec:ms */
	public static String timeFormatter(long millisecTime) {
        String time = null;
        if(millisecTime < 0){  //überprüft ob die Zeit negativ ist
            //throw new RuntimeException("Negativ time value provided");
        	time = "00:00:000";
//        } else if (millisecTime > 357539999){   //überprüft ob das Limit des Formates nicht überschreitet
//            throw new RuntimeException("Time value exceeds allowed format");
        } else {
          long min = millisecTime / (60 * 1000);
          long sec = (millisecTime - min * 60 * 1000) / 1000;
          long ms = millisecTime - min * 60 * 1000 - sec * 1000;
          
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
	public static String timeFormatterShort(long millisecTime) {
        String time = null;
        if(millisecTime<0){                        //überprüft ob zeit negativ ist
            //throw new RuntimeException("Negativ time value provided");
        	
        	time = "00:00:000";
        }else if (millisecTime>357539999){                 //überprüft ob das limit von format nicht überschreitet
            throw new RuntimeException("Time value exceeds allowed format"); 
        }else {
          millisecTime = millisecTime/1000000;
          long min= (millisecTime/60);
          long sec= millisecTime-min*60;
          
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
	
	/**
	 * UNUSED
	 * 
	 * gibt zurück ob der Zug enden muss oder nicht
	 * 
	 * @return true - wenn die maximale Zeit pro Zug ('timeMax') ohne die Vergangene Zeit ('timeCurrent') kleiner als 0
	 * @return false - wenn die 'true'-Bedingung nicht zutrifft
	 */
	public synchronized boolean isEnd () {
		
		if (Mechanics.timePerPlay - this.timeCurrent < 0) {
			return true;
		} else 
			return false;
	}
	
	/** GETTER 
	 * @return timePrintString*/ 
	public synchronized String getTimePrintString () {
		return timePrintString;
	}
	
	/** 
	 * @return timeLeft - die übrige Zeit für diesen Zug
	 */
	public synchronized long getMilliDeath() {
		return Mechanics.timePerPlay - timeCurrent;
	}
	
	public boolean isRunning() {
		return isRunning;
	}


	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.DARK_GRAY.brighter());
		g.fillRoundRect(getX() - 2,
				getY() - 2,
				getWidth() + 4,
				getHeight() + 4, 8, 5);
		g.setColor(Color.LIGHT_GRAY);
		g.fillRoundRect(getX(),
				getY(),
				getWidth(),
				getHeight(), 30, 12);
		
		g.setColor(Color.BLACK);
		g.setFont(STD_FONT);
		if (this.getTimePrintString() != null) {
			g.drawString(this.getTimePrintString(), getX() + 4,
					getY() + 16);
		} else
			System.err.println("String 'getTimePrintString()' == null; caused by 'TimeClock.draw(Graphics2D g)'");
	}
	
	/** Iniziert Position abhängig von der Position der Lebensleiste */
	public void initNewPosition () {
		setX((int) (GameScreen.getInstance().getWorld().getActivePlayer().getLife().getBoundingLife().x + 
				Math.round(GameScreen.getInstance().getWorld().getActivePlayer().getLife().getBoundingLife().width / 2.0 - getWidth() / 2.0)) - 2);
		setY(GameScreen.getInstance().getWorld().getActivePlayer().getLife().getBoundingLife().y - 40);
		
		World.timeLifeBox.initNewPosition();
		Field.infoBox.init();
	}
}

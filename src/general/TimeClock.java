package general;

import gui.GameScreen;

import java.awt.Color;
import java.awt.Graphics2D;

import comp.Component;


/**
 * Klasse f�r Zeitbeschr�nkung und Anzeige.
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
	
	/** Initialisierungszeitpunkt */
	private long timeInit = System.currentTimeMillis();
	
	/** Variable, ob die Zeit abl�uft: 
	 * true: Zeit l�uft ab
	 * false: TimeClock ist gestoppt */
	private boolean isRunning = false;
	
	/** �brige Zeit (in Millisekunden) bis der Timmer abl�uft */
	private long timeLeft = Mechanics.timePerPlay; 
	
	/** aktuelle Zeit */ 
	private long timeCurrent = 0;
	
	/** maximal m�glich Zeit f�r einen Zug */
	private long timeMax = Mechanics.timePerPlay;
	
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
	
	
	// METHODEN 
	/** updated die aktuelle Zeit; Aufruf i.d.R. durch die Main-Schleife 
	 * updted 
	 * @param timeSinceLastFrame (float) */
	protected synchronized void update (float timeSinceLastFrame) {
		if (isRunning == true) {
			this.timeCurrent = (long) (timeCurrent + timeSinceLastFrame);
			timePrintString = timeFormatter (timeMax - timeCurrent);
		}
	}
	
	/** �bernimmt timeSinceLastFrame + Aufruf durch 'Main'
	 *  .... updated die aktuelle Zeit; �bernimmt Thread */
	@Override
	public void run() {
		
		long currentTime = System.currentTimeMillis(); 
		long lastTime = System.currentTimeMillis(); 
		long deltaTime = System.currentTimeMillis(); 
		
		while (true) {
			
			currentTime = System.currentTimeMillis(); 
			deltaTime = currentTime - lastTime; 
			
			update (deltaTime); 
			lastTime = currentTime; 
			
		}
		
	}

	/** stoppt die Ausf�hrung von TimeClock */
	public void stop () {
		isRunning = false;
	}
	
	/** started TimeClock */
	public void start () {
		isRunning = true;
	}
	
	/** setzt TimeClock auf maximale Zeit zur�ck
	 * HINWEIS: an Start/Stop wird nicht ge�ndert, also ggf. stop / start aufrufen */
	public synchronized void reset() {
		timeCurrent = 0;
	}
	
	
	/** Umwandlung einer long-Variable in einem String min:sec:ms */
	public static String timeFormatter(long millisecTime) {
        String time = null;
        if(millisecTime < 0){  //�berpr�ft ob die Zeit negativ ist
            //throw new RuntimeException("Negativ time value provided");
        	time = "00:00:000";
        } else if (millisecTime > 357539999){   //�berpr�ft ob das Limit des Formates nicht �berschreitet
            throw new RuntimeException("Time value exceeds allowed format");
        } else {
          long min = millisecTime / (60 * 1000);
          long sec = (millisecTime - min * 60 * 1000) / 1000;
          long ms = millisecTime - min * 60 * 1000 - sec * 1000;
          
          if (min <= 0)
        	  time = "00";
          else if (min < 10) 
        	  time = "0" + min;
          else if (min >= 10)
        	  time = "" + min;
          
          time = time + ":";
          
          if (sec <= 0)
        	  time = time + "00";
          else if (sec < 10)
        	  time = time + "0" + sec;
          else if (sec >= 10) 
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
        if(millisecTime<0){                        //�berpr�ft ob zeit negativ ist
            //throw new RuntimeException("Negativ time value provided");
        	
        	time = "00:00:000";
        }else if (millisecTime>357539999){                 //�berpr�ft ob das limit von format nicht �berschreitet
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
	 * Updated die BoundingBox auf den neuesten Stand.
	 * @deprecated Wurde in {@link Component#setX(int)} und {@link Component#setY(int)} integriert.
	 */
	void updateBounds () {
//		this.bounds. = this.x;
//		this.bounds.y = this.y;
	}
	
	/**
	 * UNUSED
	 * 
	 * gibt zur�ck ob der Zug enden muss oder nicht
	 * 
	 * @return true - wenn die maximale Zeit pro Zug ('timeMax') ohne die Vergangene Zeit ('timeCurrent') kleiner als 0
	 * @return false - wenn die 'true'-Bedingung nicht zutrifft
	 */
	public synchronized boolean isEnd () {
		
		if (this.timeMax - this.timeCurrent < 0) {
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
	 * @return timeLeft - die �brige Zeit f�r diesen Zug
	 */
	public synchronized long getMilliDeath() {
		return timeMax - timeCurrent;
	}
	
	public boolean isRunning() {
		return isRunning;
	}


	@Override
	public void draw(Graphics2D g) {
		g.setColor(Color.BLACK);
		g.drawRoundRect(getX() - 1,
				getY() - 1,
				getWidth(),
				getHeight(), 0, 0);
		g.setColor(Color.DARK_GRAY);
		g.fillRoundRect(getX(),
				getY(),
				getWidth(),
				getHeight(), 0, 0);
		
		g.setColor(Color.WHITE);
		g.setFont(STD_FONT);
		if (this.getTimePrintString() != null) {
			g.drawString(this.getTimePrintString(), getX(),
					getY() + 15);
		}
	}
}

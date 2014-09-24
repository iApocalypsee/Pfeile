package animation;

import javax.sound.sampled.*;
import java.io.IOException;

/** This class supports the playing of sounds. All Methods are public and static. Note, that the isPlaying...() methods
 * needs time to return true, because of the overhead. That's why a call directly after the play...() probably returns
 * false.
 * <p>
 * <b>titleMelodie: </b>  This music clip should be played at the beginning of the game, during PreWindowScreen or ArrowSelectionScreenPreSet.
 * <p>
 * <b>mainThemeMelodie: </b>  This music clip is the prefered melodie during the game when the player is in GameScreen or ArrowSelectionScreen.
 * <p>
 * <b>tensionThemeMelodie: </b>  This music clip is reserved for parts in the game where you have tension.
        Note, that the tension beginns after the first 10 to 20 secounds. It could also be used after the rounds, if you can choose a new item.
 */
public class SoundPool {
    /** plays the loop countinously.
     * Use in:
     * <b>playLoop(int)</b> */
    public static int LOOP_COUNTINOUSLY = Clip.LOOP_CONTINUOUSLY;

    /** a music clip for playing the title melodie */
    private static Clip titleMelodie;

    /** a music clip for playing the main theme - it should be played during the game very often */
    private static Clip mainThemeMelodie;

    /** a music clip for playing parts with tension. Note, that it needs some time (~15s) to come to that point */
    private static Clip tensionThemeMelodie;

    static {
        AudioInputStream audioInputStream = null;
        try{
            audioInputStream = AudioSystem.getAudioInputStream(
                    SoundPool.class.getClassLoader().getResourceAsStream("resources/sfx/titleMelodie.wav"));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            titleMelodie = (Clip) AudioSystem.getLine(info);
            titleMelodie.open(audioFormat, audio, 0, size);

            audioInputStream = AudioSystem.getAudioInputStream(
                    SoundPool.class.getClassLoader().getResourceAsStream("resources/sfx/mainThemeMelodie.wav"));
            audioFormat = audioInputStream.getFormat();
            size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            audio = new byte[size];
            info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            mainThemeMelodie = (Clip) AudioSystem.getLine(info);
            mainThemeMelodie.open(audioFormat, audio, 0, size);

            audioInputStream = AudioSystem.getAudioInputStream(
                    SoundPool.class.getClassLoader().getResourceAsStream("resources/sfx/tensionThemeMelodie.wav"));
            audioFormat = audioInputStream.getFormat();
            size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            audio = new byte[size];
            info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            tensionThemeMelodie = (Clip) AudioSystem.getLine(info);
            tensionThemeMelodie.open(audioFormat, audio, 0, size);
        } catch (Exception e) { e.printStackTrace(); }
        finally {
            try {
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // TITLE MELODIE
    /** This plays the background title melodie of pfeile in an loop <code>count</code> times.
     * It will always start at the beginning after calling <code> playLoop() </code>.
     * To stop it again use <code> stop_titleMelodie </code>
     * The song should play until entering GameScreen / NewWorldTestScreen.
     * So use: <code>SoundPool.playLoop_titleMelodie(SoundPool.LOOP_COUNTINOUSLY);</code>
     * */
    public static void playLoop_titleMelodie (int count) { titleMelodie.loop(count); }

    /** This plays the background title melodie of Pfeile once.
     * To stop it again use <code>stop_titleMelodie</code>
     */
    public static void play_titleMelodie () { titleMelodie.start(); }

    /** Stops the playing of the endless loop started with <code> playLoop </code>.
     * To Start again use <code>titleMelodie_PlayLoop()</code> or <code>titleMelodie_Play</code>.
     * @see #play_titleMelodie()
     * @see #playLoop_titleMelodie(int)
     */
    public static void stop_titleMelodie () {
        titleMelodie.stop();
    }

    /** is the titleMelodie Playing? */
    public static boolean isPlaying_titleMelodie () {
        return titleMelodie.isRunning();
    }


    // MAIN THEME MELODIE
    /** This plays the main theme melodie of pfeile in an loop with <code>count</code> times.
     * It will always start at the beginning after calling <code> playLoop() </code>.
     * To stop it again use <code> stop </code>
     * It can be played in en endless Loop by using instead of any integer for count <code>SoundPool.LOOP_COUNTINOUSLY</code>
     * */
    public static void playLoop_mainThemeMelodie (int count) { mainThemeMelodie.loop(count); }

    /** This plays the background title melodie of Pfeile once.
     * To stop it again use <code>stop_titleMelodie</code>
     */
    public static void play_mainThemeMelodie () { mainThemeMelodie.start(); }

    /** Stops the playing of loop started with <code> playLoop </code> or <code>play</code>.
     * To Start again use the play or playLoop method of Soundpool.[play]_mainThemeMelodie.
     */
    public static void stop_mainThemeMelodie () {
        mainThemeMelodie.stop();
    }

    /** Is the mainTheme music clip still playing? */
    public static boolean isPlaying_mainThemeMelodie () { return mainThemeMelodie.isRunning(); }


    // TENSION THEME MELODIE
    /** This plays the main theme melodie of pfeile in an loop with <code>count</code> times.
     * It will always start at the beginning after calling <code> playLoop() </code>.
     * To stop it again use <code> stop_tensionThemeMelodie </code>
     * For an endless loop use : <code>SoundPool.playLoop_tensionThemeMelodie(SoundPool.LOOP_COUNTINOUSLY);</code>
     * */
    public static void playLoop_tensionThemeMelodie (int count) { tensionThemeMelodie.loop(count); }

    /** This plays the tensionTheme of Pfeile once.
     * To stop it again use the stop method of tensionTheme
     */
    public static void play_tensionThemeMelodie () { tensionThemeMelodie.start(); }

    /** Stops the playing of loop started with <code> playLoop </code> or <code>play</code>.
     * To Start again use the play or playLoop methods of tensionThemeMelodie.
     */
    public static void stop_tensionThemeMelodie () {
        tensionThemeMelodie.stop();
    }

    /** Is the tensionTheme music clip still playing? */
    public static boolean isPlaying_tensionThemeMelodie () { return tensionThemeMelodie.isRunning(); }
}

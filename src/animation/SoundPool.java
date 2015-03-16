package animation;

import general.LogFacility;

import javax.sound.sampled.Clip;

/** This class supports the playing of sounds. All Methods are public and static. Note, that the isPlaying...() methods
 * needs time to return true, because of the overhead. That's why a call directly after the play...() probably returns
 * false. After every melodie has been loaded, it automatically begins to start playing the title melodie and loading
 * {@link animation.SoundEffectTimeClock} as well.
 * <p>
 * <b>titleMelodie: </b>  This music clip should be played at the beginning of the game, during PreWindowScreen or ArrowSelectionScreenPreSet.
 * <p>
 * <b>mainThemeMelodie: </b>  This music clip is the preferred melodie during the game when the player is in GameScreen or ArrowSelectionScreen.
 * <p>
 * <b>tensionThemeMelodie: </b>  This music clip is reserved for parts in the game where you have tension.
        Note, that the tension begins after the first 10 to 20 seconds. It could also be used after the rounds, if you can choose a new item.
 */
public class SoundPool {
    /** plays the loop continuously.
     * Use in:
     * <b>playLoop(int)</b> */
    public static int LOOP_CONTINUOUSLY = Clip.LOOP_CONTINUOUSLY;

    /** <code>true</code> - if the music files have been loaded. */
    private static boolean loaded = false;

    /** <code>true</code> - if the music files have been loaded. */
    public static boolean isLoaded () {
        return loaded;
    }

    /** a music clip for playing the title melodie */
    private static Clip titleMelodie;

    /** a music clip for playing the main theme - it should be played during the game very often */
    private static Clip mainThemeMelodie;

    /** a music clip for playing parts with tension. Note, that it needs some time (~15s) to come to that point */
    private static Clip tensionThemeMelodie;

    /** When the game enters {@link gui.screen.GameOverScreen}, this clip is played as background music. It is sad.   */
    private static Clip gameOverMelodie;

    static {
        // loading all melodies
        Thread x = new Thread(new Runnable() {
            @Override
            public void run () {
                titleMelodie = SoundLoader.load("resources/sfx/titleMelodie.wav");
                mainThemeMelodie = SoundLoader.load("resources/sfx/mainThemeMelodie.wav", 6);
                tensionThemeMelodie = SoundLoader.load("resources/sfx/tensionThemeMelodie.wav", 5);
                // the gameOverMelodie should be slightly quieter, because of a smooth change from mainThemeMelodie [or whatever] to GameOverScreen
                gameOverMelodie = SoundLoader.load("resources/sfx/gameOverMelodie.wav", -12);
                loaded = true;
                LogFacility.log("Background music files loaded.", "Info", "initprocess");

                // When the files are loaded, the computer can play the title melodie
                play_titleMelodie(LOOP_CONTINUOUSLY);

                // That will loaded SoundEffectTimeClock as well
                SoundEffectTimeClock.isLoaded();
            }
        });
        x.setPriority(Thread.MAX_PRIORITY);
        x.setDaemon(true);
        x.start();
    }

    // TITLE MELODIE
    /** This plays the background title melodie of Pfeile in an loop <code>count</code> times.
     * It will always start at the position after calling <code> playLoop() </code>.
     * To stop it again use <code> stop_titleMelodie </code>
     * The song should play until entering GameScreen / NewWorldTestScreen.
     * So use: <code>SoundPool.play_titleMelodie(SoundPool.LOOP_CONTINUOUSLY);</code>
     * */
    public static void play_titleMelodie (int count) {
        stop_allMelodies();
        titleMelodie.loop(count);
        titleMelodie.start();
    }

    /** This plays the background title melodie of Pfeile once.
     * To stop it again use <code>stop_titleMelodie</code>
     * The Melodie will always start at the beginning and any other melodie defined by this class will stop.
     */
    public static void play_titleMelodie () {
        stop_allMelodies();
        titleMelodie.setFramePosition(0);
        titleMelodie.start();
    }

    /** Stops the playing of the endless loop started with <code> playLoop </code>.
     * To Start again use <code>play_titleMelodie()</code>.
     * @see #play_titleMelodie()
     * @see #play_titleMelodie(int)
     */
    public static void stop_titleMelodie () { titleMelodie.stop(); }

    /** is the titleMelodie Playing? */
    public static boolean isPlaying_titleMelodie () { return titleMelodie.isRunning(); }


    // MAIN THEME MELODIE
    /** This plays the main theme melodie of pfeile in an loop with <code>count</code> times.
     * It will always start at the point, the melodie is right now, after calling <code> playLoop() </code>.
     * To stop it again use <code> stop </code>
     * It can be played in en endless Loop by using instead of any integer for count <code>SoundPool.LOOP_CONTINUOUSLY</code>
     * */
    public static void play_mainThemeMelodie (int count) {
        stop_allMelodies();
        mainThemeMelodie.loop(count);
        mainThemeMelodie.start();
    }

    /** This plays the background title melodie of Pfeile once.
     * To stop it again use <code>stop_titleMelodie</code>.
     * It will always start at the beginning and every melodie (defined by this class) will stop immediately.
     */
    public static void play_mainThemeMelodie () {
        stop_allMelodies();
        mainThemeMelodie.setFramePosition(0);
        mainThemeMelodie.start();
    }

    /** Stops the playing of loop started with <code> playLoop </code> or <code>play</code>.
     * To Start again use the play or playLoop method of <code>SoundPool.[play]_mainThemeMelodie</code>.
     */
    public static void stop_mainThemeMelodie () { mainThemeMelodie.stop(); }

    /** Is the mainTheme music clip still playing? */
    public static boolean isPlaying_mainThemeMelodie () { return mainThemeMelodie.isRunning(); }


    // TENSION THEME MELODIE
    /** This plays the main theme melodie of Pfeile in an loop with <code>count</code> times.
     * It will always start at the point where it ended at the point of the call, after calling <code> playLoop() </code>.
     * To stop it again use <code> stop_tensionThemeMelodie </code>
     * For an endless loop use : <code>SoundPool.play_tensionThemeMelodie(SoundPool.LOOP_CONTINUOUSLY);</code>
     * */
    public static void play_tensionThemeMelodie (int count) {
        stop_allMelodies();
        tensionThemeMelodie.loop(count);
        tensionThemeMelodie.start();
    }

    /** This plays the tensionTheme of Pfeile once.
     * To stop it again use the stop method of tensionTheme
     * It always starts at the beginning, every other melodie will be stopped by this method.
     */
    public static void play_tensionThemeMelodie () {
        stop_allMelodies();
        tensionThemeMelodie.setFramePosition(0);
        tensionThemeMelodie.start();
    }

    /** Stops the playing of loop started with <code> playLoop </code> or <code>play</code>.
     * To Start again use the play or playLoop methods of tensionThemeMelodie.
     */
    public static void stop_tensionThemeMelodie () {
        tensionThemeMelodie.stop();
    }

    /** Is the tensionTheme music clip still playing? */
    public static boolean isPlaying_tensionThemeMelodie () { return tensionThemeMelodie.isRunning(); }


    // GAME OVER MELODIE
    /** The gameOverMelodie is played at the end of the game, if player looses and {@link gui.screen.GameOverScreen} has been
     * entered.
     *
     * @see animation.SoundPool#play_gameOverMelodie(int) */
    public static void play_gameOverMelodie () {
        stop_allMelodies();
        gameOverMelodie.setFramePosition(0);
        gameOverMelodie.start();
    }

    /** The gameOverMelodie is played at the end of the game, if player looses and {@link gui.screen.GameOverScreen} has been
     * entered. {@link SoundPool#stop_allMelodies()} is called within this method.
     * The melodie is continued <code>count</code> times. If you want to play this melodie until the end
     * of game use: {@link animation.SoundPool#LOOP_CONTINUOUSLY}.
     *
     * @see SoundPool#play_gameOverMelodie() */
    public static void play_gameOverMelodie (int count) {
        stop_allMelodies();
        gameOverMelodie.setFramePosition(0);
        gameOverMelodie.loop(count);
        gameOverMelodie.start();
    }

    /** This should only be true, if the actual Screen is GameOverScreen
     * <code>Main.getGameWindow().getScreenManager().getActiveScreen().SCREEN_INDEX == GameOverScreen.SCREEN_INDEX</code>.
     *
     * @see SoundPool#play_gameOverMelodie()
     * @return Is the gameOverMelodie playing?
     */
    public static boolean isPlaying_gameOverMelodie () { return gameOverMelodie.isRunning(); }

    /** The gameOverMelodie is interrupted. If {@link animation.SoundPool#play_gameOverMelodie(int)} is called, the number
     * of loops is deleted. */
    public static void stop_gameOverMelodie () {
        gameOverMelodie.stop();
    }


    // Referring to all melodies

    /** This stops all possible melodies */
    public static void stop_allMelodies() {
        stop_mainThemeMelodie();
        stop_tensionThemeMelodie();
        stop_titleMelodie();
        stop_gameOverMelodie();
    }
}

package animation;

import general.LogFacility;

import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;


/** TimeClock needs some sounds, that are provided by these static-methods. <p>
 * They are: */
public class SoundEffectTimeClock {

    /** If time is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip.*/
    private static Clip tickingNoise;

    /** An Explosion at the end of timeClock (when the times runs out) is necessary for the user to notice the end of his turn. */
    private static Clip explosion;

    /** If timeClock is below 3s, the user should notice it by playing this sound instead of {@link SoundEffectTimeClock#play_tickingNoise()}. */
    private static Clip tickingCriticalNoise;

    private static boolean isLoaded = false;

    /** <code>true</code> - if the sound effect files has been loaded. */
    public static boolean isLoaded () {
        return isLoaded;
    }

    static {
        Thread x = new Thread(() -> {
            tickingNoise = SoundLoader.load("resources/sfx/soundEffects/clock sound effect.wav");

            /* this controls the volume of <code>tickingNoise</code> */
            FloatControl gainControlTicking = (FloatControl) tickingNoise.getControl(FloatControl.Type.MASTER_GAIN);

            tickingCriticalNoise = SoundLoader.load("resources/sfx/soundEffects/clock sound effect.wav");

            /* to set the volume of {@link animation.SoundEffectTimeClock#play_tickingCriticalNoise().*/
            FloatControl gainControlTickingCritical = (FloatControl) tickingCriticalNoise.getControl(FloatControl.Type.MASTER_GAIN);

            explosion = SoundLoader.load("resources/sfx/soundEffects/explosion.wav");

            /* this controls the volume of <code>explosion</code> */
            FloatControl gainControlExplosion = (FloatControl) explosion.getControl(FloatControl.Type.MASTER_GAIN);


            // setting volume
            gainControlTicking.setValue(-7);   // - 8 db
            gainControlExplosion.setValue(+2); // + 2 db
            gainControlTickingCritical.setValue(-1);  // - 1 db
            // instead of using another sound file for criticalTickingNoise, I'm just increasing the volume of tickingCriticalNoise

            isLoaded = true;

            LogFacility.log("Sound effect files loaded.", "Info", "init process");
        }, "SoundLoaderTimeClock");
        x.setDaemon(true);
        x.setPriority(2);
        x.start();
    }


    // TICKING_NOISE

    /** This plays one tick/click of TimeClock (10s --> 3s). It is equal to one second at the countdown. */
    public static void play_tickingNoise () {
        tickingNoise.setFramePosition(0);
        tickingNoise.start();
    }

    /** Stops playing the ticking/clicking timeClock sound. <b>Usually not necessary! (one tick should/is shorter than a second)</b>*/
    public static void stop_tickingNoise () { tickingNoise.stop(); }

    /** Is the timeClock sound playing? <b>Usually not necessary (one click need to be shorter than a second)</b>*/
    public static boolean isRunning_tickingNoise () { return tickingNoise.isRunning(); }

    /** the full length in Microseconds of this soundEffect. [1 Microsecond is 1/1000 Millisecond] */
    public static long getMicroSecLength_tickingNoise () { return tickingNoise.getMicrosecondLength(); }

    /** The position of this soundEffect at this time. Compare with {@link SoundEffectTimeClock#getMicroSecLength_tickingNoise()}. <p>
     * The level of precision is not guaranteed. For example, an implementation might calculate the microsecond position
     * from the current frame position and the audio sample frame rate.
     * The precision in microseconds would then be limited to the number of microseconds per sample frame.  */
    public static long getCurrentMicroSecLength_tickingNoise () { return tickingNoise.getMicrosecondPosition(); }


    // TICKING_CRITICAL_NOISE

    /** This plays if there are only 3 or fewer seconds left on TimeClock. The sound differs from
     * {@link SoundEffectTimeClock#play_tickingNoise()}, because the user knows exactly that his turn stops soon. */
    public static void play_tickingCriticalNoise () {
        tickingCriticalNoise.setFramePosition(0);
        tickingCriticalNoise.start();
    }

    /** Stops the ticking sound of <code>TimeClock</code>. <p> <b>Usually this should be unnecessary as the duration of this sound is less than a second.</b>*/
    public static void stop_tickingCriticalNoise () {
        tickingCriticalNoise.stop();
    }

    /** returns if <code>tickingCriticalNoise</code> {@link SoundEffectTimeClock#play_tickingCriticalNoise()}is played right now, or not */
    public static boolean isRunning_tickingCriticalNoise () {
        return tickingCriticalNoise.isRunning();
    }

    /** the complete duration of the sound played if TimeClock is below 3s
     * @see SoundEffectTimeClock#getCurrentMicroSecLength_tickingCriticalNoise() */
    public static long getMicroSecLength_tickingCriticalNoise () {
        return tickingCriticalNoise.getMicrosecondLength();
    }

    /** position of this soundEffect at this time. Compare with {@link SoundEffectTimeClock#getMicroSecLength_tickingCriticalNoise()}. <p>
    * The level of precision is not guaranteed. For example, an implementation might calculate the microsecond position
    * from the current frame position and the audio sample frame rate.
    * The precision in microseconds would then be limited to the number of microseconds per sample frame. */
    public static long getCurrentMicroSecLength_tickingCriticalNoise () {
        return tickingCriticalNoise.getMicrosecondPosition();
    }


    // EXPLOSION

    /** Starts playing explosion. Any click of timeClock will be stopped. The Explosion represents the end of time. */
    public static void play_explosion () {
        stopAllSoundEffects();

        explosion.setFramePosition(0);
        explosion.start();
    }

    /** Is the explosion playing? <p>
     * @see SoundEffectTimeClock#play_explosion() */
    public static boolean isRunning_explosion () { return explosion.isRunning(); }

    /** the full length in Microseconds of this soundEffect */
    public static long getMicroSecLength_Explosion () { return explosion.getMicrosecondLength(); }

    /** The position of this soundEffect at this time. Compare with {@link SoundEffectTimeClock#getMicroSecLength_Explosion()}. <p>
     * The level of precision is not guaranteed. For example, an implementation might calculate the microsecond position
     * from the current frame position and the audio sample frame rate.
     * The precision in microseconds would then be limited to the number of microseconds per sample frame.  */
    public static long getCurrentMicroSecLength_explosion () { return explosion.getMicrosecondPosition(); }


    // GENERAL METHODS

    /** this stops all sound coming from this class except the explosion. (it would sound strange to stop an explosion while it is playing)
     * The method calls: {@link SoundEffectTimeClock#stop_tickingNoise()} and {@link SoundEffectTimeClock#stop_tickingCriticalNoise()} */
    public static void stopAllSoundEffects () {
        stop_tickingNoise();
        stop_tickingCriticalNoise();
    }
}

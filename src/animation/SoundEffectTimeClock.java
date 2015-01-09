package animation;

import javax.sound.sampled.*;
import java.io.IOException;

public class SoundEffectTimeClock {

    /** If time is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip.*/
    private static Clip outOfTime10s;

    /** the whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    private static Clip outOfTime60s;

    static {
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(
                    SoundPool.class.getClassLoader().getResourceAsStream("resources/sfx/soundEffects/10 Seconds Countdown Clock.wav"));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            outOfTime10s = (Clip) AudioSystem.getLine(info);
            outOfTime10s.open(audioFormat, audio, 0, size);

            audioInputStream = AudioSystem.getAudioInputStream(
                    SoundPool.class.getClassLoader().getResourceAsStream("resources/sfx/soundEffects/1 Minute Countdown Clock.wav"));
            audioFormat = audioInputStream.getFormat();
            size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            audio = new byte[size];
            info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            outOfTime60s = (Clip) AudioSystem.getLine(info);
            outOfTime60s.open(audioFormat, audio, 0, size);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                assert audioInputStream != null;
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // OutOfTime10Sec

    /** Starts playing the timeClock sound at the beginning. If <code>OutOfTime10Sec</code> is still playing, it will be stopped immediately.
     * If time of TimeClock is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip. */
    public static void play_OutOfTime10Sec () {
        stopAllSoundEffects();

        outOfTime10s.setFramePosition(0);
        outOfTime10s.start();
    }

    /** Stops playing the timeClock sound.
     * If time (of TimeClock) is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip. */
    public static void stop_OutOfTime10Sec () { outOfTime10s.stop(); }

    /** Is the timeClock sound playing?
     * If time (of TimeClock) is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip. */
    public static boolean isRunning_OutOfTime10Sec () { return outOfTime10s.isRunning(); }

    /** the full length in Microseconds of this soundEffect. [1 Microsecond is 1/1000 Millisecond] */
    public static long getMicroSecLength_OutOfTime10Sec () { return outOfTime10s.getMicrosecondLength(); }

    /** The position of this soundEffect at this time. Compare with {@link SoundEffectTimeClock#getMicroSecLength_OutOfTime10Sec()}. <p>
     * The level of precision is not guaranteed. For example, an implementation might calculate the microsecond position
     * from the current frame position and the audio sample frame rate.
     * The precision in microseconds would then be limited to the number of microseconds per sample frame.  */
    public static long getCurrentMicroSecLength_OutOfTime10Sec () { return outOfTime10s.getMicrosecondPosition(); }


    // OutOfTime60Sec


    /** Starts playing the long clip. If <code>OutOfTime10Sec</code> is playing, it will be stop immediately.
     * The whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    public static void play_OutOfTime60Sec () {
        stopAllSoundEffects();

        outOfTime60s.setFramePosition(0);
        outOfTime60s.start();
    }

    /** Stops playing the long clip.
     * The whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    public static void stop_OutOfTime60Sec () { outOfTime60s.stop(); }

    /** Is the long clip playing?
     * The whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    public static boolean isRunning_OutOfTime60Sec () { return outOfTime60s.isRunning(); }

    /** the full length in Microseconds of this soundEffect */
    public static long getMicroSecLength_OutOfTime60Sec () { return outOfTime60s.getMicrosecondLength(); }

    /** The position of this soundEffect at this time. Compare with {@link SoundEffectTimeClock#getMicroSecLength_OutOfTime60Sec()}. <p>
     * The level of precision is not guaranteed. For example, an implementation might calculate the microsecond position
     * from the current frame position and the audio sample frame rate.
     * The precision in microseconds would then be limited to the number of microseconds per sample frame.  */
    public static long getCurrentMicroSecLength_OutOfTime60Sec () { return outOfTime60s.getMicrosecondPosition(); }


    // general methods

    /** if the explosion is playing either by OutOfTime10Sec or OutOfTime60Sec, this method returns true.
     * However, the implementation estimates the position where the explosion begins. Consequently, it won't be absolutely
     * correct, but its precision should be enough for most applications. */
    public static boolean isExplosionPlaying () {
        if (isRunning_OutOfTime10Sec()) {
            // the position the explosion begins is about 00:00:10.105
            return getCurrentMicroSecLength_OutOfTime10Sec() > 10105000;
        } else if (isRunning_OutOfTime60Sec()) {
            // the position the explosion begins is about 00:00:55.105
            return getCurrentMicroSecLength_OutOfTime60Sec() > 55105000;
        }
        // if no sound is playing
        return false;
    }

    /** this stops all sound coming from this class both OutOfTime10Sec and OutOfTime60Sec */
    public static void stopAllSoundEffects () {
        stop_OutOfTime10Sec();
        stop_OutOfTime60Sec();
    }
}

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
        if (isRunning_OutOfTime60Sec())
            stop_OutOfTime60Sec();

        outOfTime10s.setMicrosecondPosition(0);
        outOfTime10s.start();
    }

    /** Stops playing the timeClock sound.
     * If time (of TimeClock) is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip. */
    public static void stop_OutOfTime10Sec () { outOfTime10s.stop(); }

    /** Is the timeClock sound playing?
     * If time (of TimeClock) is running low, there should be same kind of clicking sound - 10 seconds until explosion at the end of the clip. */
    public static boolean isRunning_OutOfTime10Sec () { return outOfTime10s.isRunning(); }


    // OutOfTime60Sec

    /** Starts playing the long clip. If <code>OutOfTime10Sec</code> is playing, it will be stop immediately.
     * The whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    public static void play_OutOfTime60Sec () {
        if (isRunning_OutOfTime10Sec())
            stop_OutOfTime10Sec();

        outOfTime60s.setMicrosecondPosition(0);
        outOfTime60s.start();
    }

    /** Stops playing the long clip.
     * The whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    public static void stop_OutOfTime60Sec () { outOfTime60s.stop(); }

    /** Is the long clip playing?
     * The whole clip takes 1 minute with explosion. It is the same sound like <code>outOfTime10s</code>, but with longer ticking clock. */
    public static boolean isRunning_OutOfTime60Sec () { return outOfTime60s.isRunning(); }
}

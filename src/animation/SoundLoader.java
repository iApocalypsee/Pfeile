package animation;

import general.LogFacility;

import javax.sound.sampled.*;
import java.io.IOException;

/**
 * Every Sound played from {@link animation.SoundPool} or {@link animation.SoundEffectTimeClock} are loaded by this class.
 */
public class SoundLoader {

    /** This returns an {@link javax.sound.sampled.Clip} from the address given by the String <code>URL</code>.
     * The String URL is used to load an <p>
     *     <code>AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(</code> <p>
     *             <code>SoundLoader.class.getClassLoader().getResourceAsStream(URL));</code> <p>
     *  So an example of an URL might be: <code>resources/sfx/soundFile.wav</code>.
     *  The Clip is loaded completely from its beginning to its end. This method may throw an... <p>
     *  <code>UnsupportedAudioFileException</code> if the stream does not point to valid audio file data recognized by the system <p>
     *  <code>LineUnavailableException</code>  if a matching line is not available due to resource restrictions <p>
     *  <code>IOException</code> if the file doesn't exit
     */
    public static Clip load (String URL) {
        Clip audioClip = null;
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = AudioSystem.getAudioInputStream(
                    SoundLoader.class.getClassLoader().getResourceAsStream(URL));
            AudioFormat audioFormat = audioInputStream.getFormat();
            int size = (int) (audioFormat.getFrameSize() * audioInputStream.getFrameLength());
            byte[] audio = new byte[size];
            DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, size);
            audioInputStream.read(audio, 0, size);
            audioClip = (Clip) AudioSystem.getLine(info);
            audioClip.open(audioFormat, audio, 0, size);
        } catch (UnsupportedAudioFileException | LineUnavailableException | IOException e) {
            LogFacility.log("An error occurred at loading: " + URL, LogFacility.LoggingLevel.Error);
            e.printStackTrace();
        } finally {
            // closing the stream
            try {
                assert audioInputStream != null;
                audioInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return audioClip;
    }
}

package sound

import general.Delegate.Function0Delegate
import sound.Channel.Playing

import javax.sound.sampled.{LineEvent, LineListener}

/**
 *
 * @author Josip Palavra
 */
class Channel(val soundSystem: SoundSystemLike) {

  private var _volume = 0.0f
  private var _currentlyPlaying = List[Playing]()

  def volume = _volume

  def volumeMasterAligned = soundSystem.masterVolume * volume

  def decibelVolume = soundSystem.maximumVolume * volumeMasterAligned

  def currentlyPlaying = _currentlyPlaying

  private[sound] def appendPlaying(now: Playing): Unit = {
    _currentlyPlaying = _currentlyPlaying ++ List(now)
    // When the track is done playing, it has to be removed from the "currently playing" list
    now.onDonePlaying += { () => _currentlyPlaying = _currentlyPlaying filter { _ ne now } }
  }

  def volume_=(a: Float) = {
    require(a >= 0.0 && a <= 1.0, "Volume must be in between 0.0 and 1.0")
    _volume = a
  }

}

object Channel {

  case class Playing(soundtrack: SoundtrackLike) {

    val onDonePlaying = new Function0Delegate

    soundtrack.underlying.addLineListener(new LineListener {
      override def update(event: LineEvent): Unit = {
        import scala.concurrent.ExecutionContext.Implicits.global
        // If the end of the media has been reached, notify all callbacks about that.
        // I don't want the clumsy Java-style listener system.
        if(event.getType == LineEvent.Type.STOP) {
          onDonePlaying.callAsync()
        }
      }
    })

  }

}

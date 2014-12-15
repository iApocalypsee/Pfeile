package sound

import java.net.URI
import javax.sound.sampled._

import scala.concurrent.duration.FiniteDuration

/** Base trait for soundtrack implementations.
  *
  * This is what the whole sound API fuzz is all about: the soundtrack. I need to have a way of
  * playing them nice and neatly, not in a verbose low-level style like Java does.
  *
  */
trait SoundtrackLike {

  /** The underlying, lower level on which the soundtrack object is built. */
  val underlying: DataLine

  /** The address from which the soundtrack has been loaded. */
  val from: URI

  /** The time length of the soundtrack. */
  val trackLength: FiniteDuration

  /** Plays the soundtrack with the given channel. */
  def render(by: Channel): Channel.Playing = {
    // Setting volume is important when playing with channels.
    underlying.getControl( FloatControl.Type.MASTER_GAIN ).asInstanceOf[FloatControl].setValue( by.decibelVolume )
    underlying.start( )
    // Register the soundtrack to the channel that it is playing right now
    val ret = Channel.Playing( this )
    by.appendPlaying( ret )
    ret
  }

}

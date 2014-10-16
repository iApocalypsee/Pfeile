package sound

import java.net.URI
import java.util.concurrent.TimeUnit
import javax.sound.sampled.{SourceDataLine, Clip, DataLine, AudioSystem}
import javax.sound.sampled.DataLine.Info

import sound.Channel.Playing

import scala.concurrent.duration.FiniteDuration

/** Default implementation of a soundtrack. */
class Soundtrack private[sound](override val from: URI, lineClass: Class[_]) extends SoundtrackLike {

  /** The lowest representation of sound ever possible. */
  private var _rawData: Array[Byte] = null
  /** Have system resources been allocated for the soundtrack? */
  private var _loaded = false

  /** The audio input stream. Has to be closed somewhere. */
  private val _src = AudioSystem.getAudioInputStream( from.toURL )

  /** The time length of the track. */
  override val trackLength = FiniteDuration( (_src.getFrameLength / _src.getFormat
    .getFrameRate).asInstanceOf[Long] * 1000000, TimeUnit.MICROSECONDS )

  /** The underlying track representation. */
  override val underlying = {
    val size = (_src.getFormat.getFrameSize * _src.getFrameLength).asInstanceOf[Int]
    _rawData = new Array[Byte]( size )
    val info = new Info( lineClass, _src.getFormat, size )
    _src.read( _rawData, 0, size )
    AudioSystem.getLine( info ).asInstanceOf[DataLine]
  }

  /** Indicates whether the soundtrack's data has been loaded into the memory. */
  def areResourcesAcquired = _loaded

  /** Loads the soundtrack's data into memory, acquiring all needed system resources in the process as well. */
  def acquireResources(): Unit = {
    if (!_loaded) {
      underlying.open( )
      _loaded = true
    }
  }

  /** Unloads the soundtrack from memory. */
  def unload(): Unit = {
    // Only deallocate memory if the soundtrack is loaded
    if (_loaded) {
      underlying.close( )
      _loaded = false
    }
  }

  /** Plays the soundtrack with the given channel. */
  override def render(by: Channel): Playing = {
    if (!areResourcesAcquired) acquireResources( )
    super.render( by )
  }


  // Close call should be last call in the constructor, maybe some other method
  // needs information from the AudioInputStream
  _src.close( )
}

/** Provides factory methods for creating soundtracks in various ways; as a clip or as a source data line for now.
  *
  */
object Soundtrack {

  /** Constructs a soundtrack with an underlying clip object
    *
    * @param from The resource to load the sound file from.
    * @return The soundtrack.
    */
  def clip(from: URI) = new Soundtrack( from, classOf[Clip] )

  /** Constructs a soundtrack with an underlying source data line object
    *
    * @param from The resource to load the sound file from.
    * @return The soundtrack.
    */
  def sourceDataLine(from: URI) = new Soundtrack( from, classOf[SourceDataLine] )

}

package sound

/**
 *
 * @author Josip Palavra
 */
trait SoundSystemLike {

  /** The standard channel for unspecified usage. Use this channel if you want
    * to play temporary (?) soundtracks.
    */
  val standardChannel = new Channel( this )

  /** The maximum volume in decibels that the sound system caps to. */
  var maximumVolume: Float

  /** The master volume of the sound system.
    * Value is not provided in decibel, but in a value reaching from <code>0.0</code> to <code>1.0</code>.
    * <code>1.0</code> is equal to the maximum volume that the sound system is supporting.
    */
  var masterVolume: Float

}

object SoundConstraints {

  /** The upper border of volume, measured in decibels. */
  lazy val MaximumPossibleVolume = 40f

}

object GameSoundSystem extends SoundSystemLike {

  private var _maximumVolume = 25f
  private var _masterVolume = 0.6f

  override def maximumVolume = _maximumVolume
  override def maximumVolume_=(a: Float) = {
    require( a >= 0f, "Volume in dB must not be negative." )
    require( a < SoundConstraints.MaximumPossibleVolume, s"Volume in dB must not be above ${SoundConstraints
      .MaximumPossibleVolume} dB" )
    _maximumVolume = a
  }

  override def masterVolume = _masterVolume
  override def masterVolume_=(a: Float) = {
    require(a >= 0f && a <= 1f, "Master volume must be inside 0.0 or 1.0")
    _masterVolume = a
  }

  /** Channel for all background music. */
  val backgroundMusic = new Channel(this)

}

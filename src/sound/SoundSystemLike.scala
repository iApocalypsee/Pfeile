package sound

/**
 *
 * @author Josip Palavra
 */
trait SoundSystemLike {

  /** The standard channel for unspecified usage. Use this channel if you want
    * to play temporary (?) soundtracks.
    */
  val standardChannel = new Channel(this)

  /** The maximum volume in decibels that the sound system caps to. */
  def maximumVolume: Float

  /** The master volume of the sound system.
    * Value is not provided in decibel, but in a value reaching from <code>0.0</code> to <code>1.0</code>.
    * <code>1.0</code> is equal to the maximum volume that the sound system is supporting.
    */
  def masterVolume: Float

}

package geom

/**
 *
 * @author Josip Palavra
 */
trait VectorLike {
  
  type VecType <: VectorLike

  /** The squared length of the vector.
    *
    * Use this to avoid additional square rooting. Square rooting takes additional time to calculate.
    */
  def lengthSq: Float

  /** The square root of <code>lengthSq</code> */
  def length: Float

  /** Normalizes the vector. */
  def normalize(): Unit

  /** Returns a copy of this vector with normalized coordinates.
    *
    * The source vector remains unchanged, a deep copy is instantiated and normalized.
    * @return A normalized copy of this vector.
    */
  def normalized: VecType

  def dot(vec: VecType): Float

  def +(vec: VecType): VecType

  def +(f: Float): VecType

  def -(vec: VecType): VecType

  def -(f: Float): VecType

  def *(vec: VecType): VecType

  def *(f: Float): VecType

  def /(vec: VecType): VecType

  def /(f: Float): VecType

  def abs: VecType

  def ==(vec: VecType): Boolean

  def lerp(dest: VecType, lerpFactor: Float): VecType

  def asFloatArray: Array[Float]
}

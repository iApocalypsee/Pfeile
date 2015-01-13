package geom

/**
 *
 * @author Josip Palavra
 */
trait VectorLike[NumberType <: AnyVal] {
  
  type VecType <: VectorLike[_]

  /** The squared length of the vector.
    *
    * Use this to avoid additional square rooting. Square rooting takes additional time to calculate.
    */
  def lengthSq: NumberType

  /** The square root of <code>lengthSq</code> */
  def length: NumberType

  /** Returns a copy of this vector with normalized coordinates.
    *
    * The source vector remains unchanged, a deep copy is instantiated and normalized.
    * @return A normalized copy of this vector.
    */
  def normalized: VecType

  def +(vec: VecType): VecType

  def +(f: NumberType): VecType

  def -(vec: VecType): VecType

  def -(f: NumberType): VecType

  def *(vec: VecType): VecType

  def *(f: NumberType): VecType

  def /(vec: VecType): VecType

  def /(f: NumberType): VecType

  def abs: VecType

  def ==(vec: VecType): Boolean
  
  def unifiedVector(factor: NumberType): VecType
  
  def vectorFrom(x: Array[NumberType]): VecType = vectorFrom(x.toList)
  
  def vectorFrom(x: List[NumberType]): VecType

  def asList: List[NumberType]
  
  def dimension = asList.length
}

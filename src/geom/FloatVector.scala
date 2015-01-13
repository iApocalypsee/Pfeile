package geom

import scala.math._

/**
  * Created by jolecaric on 12/01/15.
  */
trait FloatVector extends VectorLike[Float] {

  override type VecType <: FloatVector

  override def +(vec: VecType): VecType = modifiedVector(vec, (thisFloat, that) => thisFloat + that)

  override def -(vec: VecType): VecType = modifiedVector(vec, (thisFloat, that) => thisFloat - that)

  override def *(vec: VecType): VecType = modifiedVector(vec, (thisFloat, that) => thisFloat * that)

  override def /(vec: VecType): VecType = modifiedVector(vec, (thisFloat, that) => thisFloat / that)

  override def +(f: Float): VecType = this + unifiedVector(f)

  override def -(f: Float): VecType = this - unifiedVector(f)

  override def *(f: Float): VecType = this * unifiedVector(f)

  override def /(f: Float): VecType = this / unifiedVector(f)

  override def lengthSq = this.asList.foldLeft(0f) { (prev, elem) =>
    prev + pow(elem, 2).asInstanceOf[Float]
  }

  override def length = sqrt(lengthSq).asInstanceOf[Float]

  override def abs: VecType = vectorFrom(asList.map { math.abs } )

  override def ==(vec: VecType): Boolean = {
    if (vec.dimension != this.dimension) return false

    val vecValues = vec.asList
    val thisValues = asList

    (0 until this.dimension).foldLeft(true) { (yetEqual, index) =>
      // Only do the equality test if no inequality has been found yet.
      if (yetEqual) vecValues(index) != thisValues(index)
      else false
    }
  }

  /**
    * Returns a copy of this vector with normalized coordinates.
    *
    * The source vector remains unchanged, a deep copy is instantiated and normalized.
    * @return A normalized copy of this vector.
    */
  override def normalized: VecType = {
    val values = asList
    val len = length
    vectorFrom(values.map { _ / len })
  }

  /**
    * Implementation of the algorithm that calculates this vector and the specified vector together.
    * The implementation depends on the given operation function.
    * @param vec The vector to calculate on.
    * @param operation The operation to perform.
    * @return A new, modified vector.
    */
  private def modifiedVector(vec: VecType, operation: (Float, Float) => Float) = {
    val thisValues = this.asList
    val theseValues = vec.asList

    // The vectors must have the same dimension
    require(thisValues.length == theseValues.length)

    val summary = (0 until this.dimension).foldLeft(List[Float]()) { (prev, index) =>
      prev ++ List(operation(thisValues(index), theseValues(index)))
    }

    vectorFrom(summary)
  }

}

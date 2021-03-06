package comp

import java.awt.Shape
import java.awt.geom.AffineTransform

import general.Delegate
import geom.Vector

import scala.math.toRadians

/**
 * Class hiding 2D-matrix details to provide a common facade to the matrix operations.
 */
class Transformation2D {

  private var _translationMatrix = new AffineTransform
  private var _rotationMatrix = new AffineTransform
  private var _scaleMatrix = new AffineTransform

  /** in degree*/
  private var _rotation = 0.0

  val onTranslated = Delegate.create[TranslationChange]
  val onRotated = Delegate.create[RotationChange]
  val onScaled = Delegate.create[ScaleChange]

  val onTransformed = Delegate.create[TransformationEvent]

  onTranslated += { x => onTransformed(x) }
  onRotated += { x => onTransformed(x) }
  onScaled += { x => onTransformed(x) }

  /**
   * Resets the whole transformation, so that every stat is cleaned.
   *
   * No transformation, no rotation, no scale. Everything zeroed out.
   */
  def resetTransformation() = {
    _translationMatrix = new AffineTransform
    _rotationMatrix = new AffineTransform
    _scaleMatrix = new AffineTransform
    _rotation = 0.0
    this
  }

  /**
    * Applies a translation to the transformation.
 *
    * @param x The amount of units to translate it in x-direction.
    * @param y The amount of units to translate it in y-direction.
    * @return This.
    */
  def translate(x: Double, y: Double) = setTranslation(translation.getX + x, translation.getY + y)

  /**
    * Rotates the transformation by the <b>degree</b> given.
    *
    * @param angle The angle, in degrees.
    * @return This.
    */
  def rotate(angle: Double) = setRotation(_rotation + angle)

  /**
    * Scales the transformation.
    *
    * @param sx The amount of scaling in the x-direction.
    * @param sy The amount of scaling in the y-direction.
    * @return This.
    */
  def scale(sx: Double, sy: Double): Transformation2D = setScale(scale.getX * sx, scale.getY * sy)

  def scale(fac: Double): Transformation2D = scale(fac, fac)

  /**
    * Sets the absolute translation.
    * __Use with care when this [[comp.Transformation2D]] object is used by a [[comp.Component]].
    * '''I am not going to watch the use of this function, so I won't fix bugs that arise out of the use of it.'''__
 *
    * @param x The x position to set the translation to.
    * @param y The y position to set the translation to.
    * @return This.
    */
  def setTranslation(x: Double, y: Double) = {
    val old = translation
    setTranslationWithoutSideEffect(x, y)
    val firedEvent = TranslationChange(old, translation, this)
    if(firedEvent.isDelta) {
      onTranslated(firedEvent)
    }
    this
  }

  private[comp] def setTranslationWithoutSideEffect(x: Double, y: Double) = {
    _translationMatrix = AffineTransform.getTranslateInstance(x, y)
  }

  /**
   * Sets the absolute rotation. <b>(in degrees)</b>
   *
   * @param angle The new angle to set the transformation's rotation to.
   * @return This.
   */
  def setRotation(angle: Double) = {
    val old = _rotation
    setRotationWithoutSideEffect(angle)
    val firedEvent = RotationChange(old, _rotation, this)
    if(firedEvent.isDelta) {
      onRotated(firedEvent)
    }
    this
  }

  private[comp] def setRotationWithoutSideEffect(angle: Double) = {
    _rotationMatrix = AffineTransform.getRotateInstance(toRadians(angle))
    _rotation = angle
  }

  def copy = {
    val cpy = new Transformation2D
    cpy._translationMatrix = this.translationMatrix
    cpy._rotationMatrix = this.rotationMatrix
    cpy._scaleMatrix = this.scaleMatrix
    cpy._rotation = this.rotation
    cpy
  }

  /**
    * Assigns the data given by the other transformation to this transformation, so that `this == other`.
    *
    * @param other The other transformation to copy the data from.
    */
  def assign(other: Transformation2D): Unit = {
    setTranslation(other.translation.getX, other.translation.getY)
    setRotation(other.rotation)
    setScale(other.scale.getX, other.scale.getY)
  }

  /**
   * Sets the absolute scale of this transformation.
   *
   * @param sx The scale in x-direction.
   * @param sy The scale in y-direction.
   * @return This.
   */
  def setScale(sx: Double, sy: Double) = {
    val old = scale
    setScaleWithoutSideEffect(sx, sy)
    val firedEvent = ScaleChange(old, scale, this)
    if(firedEvent.isDelta) {
      onScaled(firedEvent)
    }
    this
  }

  private[comp] def setScaleWithoutSideEffect(sx: Double, sy: Double) = {
    _scaleMatrix = AffineTransform.getScaleInstance(sx, sy)
  }

  def translation = new Vector(_translationMatrix.getTranslateX, _translationMatrix.getTranslateY)
  def rotation = _rotation
  def scale = new Vector(_scaleMatrix.getScaleX, _scaleMatrix.getScaleY)

  def translationMatrix = new AffineTransform(_translationMatrix)
  def rotationMatrix = new AffineTransform(_rotationMatrix)
  def scaleMatrix = new AffineTransform(_scaleMatrix)

  def localConcatenatedMatrix: AffineTransform = {
    // Get a fresh copy of every matrix. I don't want to modify the original matrices.
    val t = translationMatrix
    val r = rotationMatrix
    r.concatenate(scaleMatrix)
    t.concatenate(r)
    t
  }

  def transformOriginal(s: Shape) = localConcatenatedMatrix.createTransformedShape(s)

}

object Transformation2D {
  def applySilently(c: Component, t: Transformation2D): Component = {
    val translation = t.translation
    c.getTransformation.setTranslationWithoutSideEffect(translation.getX, translation.getY)
    c.getTransformation.setRotationWithoutSideEffect(t.rotation)
    c.getTransformation.setScaleWithoutSideEffect(t.scale.getX.asInstanceOf[Int], t.scale.getY.asInstanceOf[Int])
    c
  }
}

trait TransformationEvent {
  def applied: Transformation2D
  def matrix: AffineTransform
  def deltaMatrix: AffineTransform
  def isDelta = !deltaMatrix.isIdentity
  def applyTransformation(x: Transformation2D): Unit
}

object TransformationEvent {

  lazy val InvalidRotation: PartialFunction[TransformationEvent, Unit] = {
    case rotation: RotationChange => throw new UnsupportedOperationException("Rotation not supported")
    case _ =>
  }

  lazy val InvalidScale: PartialFunction[TransformationEvent, Unit] = {
    case scale: ScaleChange => throw new UnsupportedOperationException("Scaling not supported")
    case _ =>
  }

}

case class RotationChange(oldDegree: Double, newDegree: Double, override val applied: Transformation2D) extends TransformationEvent {
  val delta = newDegree - oldDegree
  override lazy val matrix = AffineTransform.getRotateInstance(newDegree)
  override lazy val deltaMatrix = AffineTransform.getRotateInstance(delta)

  // TODO Children rotation should be taken care of as well, children should be translated around the center of applied transform
  override def applyTransformation(x: Transformation2D) = {
    x.rotate(delta)
  }
}

case class TranslationChange(oldTranslation: Vector, newTranslation: Vector, override val applied: Transformation2D) extends TransformationEvent {
  val delta = newTranslation - oldTranslation
  override lazy val matrix = AffineTransform.getTranslateInstance(newTranslation.getX, newTranslation.getY)
  override lazy val deltaMatrix = AffineTransform.getTranslateInstance(delta.getX, delta.getY)

  override def applyTransformation(x: Transformation2D) = {
    x.translate(delta.getX, delta.getY)
  }
}

case class ScaleChange(oldScale: Vector, newScale: Vector, override val applied: Transformation2D) extends TransformationEvent {
  val delta = new Vector(newScale.getX / oldScale.getX, newScale.getY / oldScale.getY)
  override lazy val matrix = AffineTransform.getScaleInstance(newScale.getX, newScale.getY)

  // Multiplicative delta matrix.
  override def deltaMatrix = AffineTransform.getScaleInstance(delta.getX, delta.getY)

  override def applyTransformation(x: Transformation2D) = {
    // Following line causes a few problems with components and their positions.
    // So scale events will not propagate.
    //x.scale(delta.getX, delta.getY)
  }
}

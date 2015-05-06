package comp

import java.awt.Shape
import java.awt.geom.AffineTransform

import general.Delegate
import geom.Vector2

import scala.math.toRadians

/**
 * Class hiding 2D-matrix details to provide a common facade to the matrix operations.
 */
class Transformation2D {

  private var _translationMatrix = new AffineTransform
  private var _rotationMatrix = new AffineTransform
  private var _scaleMatrix = new AffineTransform

  private var _rotation = 0.0

  val onTranslated = Delegate.create[TranslationChange]
  val onRotated = Delegate.create[RotationChange]
  val onScaled = Delegate.create[ScaleChange]

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
  }

  /**
    * Applies a translation to the transformation.
    * @param x The amount of units to translate it in x-direction.
    * @param y The amount of units to translate it in y-direction.
    * @return This.
    */
  def translate(x: Double, y: Double) = setTranslation(translation.x + x, translation.y + y)

  /**
    * Rotates the transformation by the degree given.
    * @param angle The angle, in degrees.
    * @return This.
    */
  def rotate(angle: Double) = setRotation(_rotation + angle)

  /**
    * Scales the transformation.
    * @param sx The amount of scaling in the x-direction.
    * @param sy The amount of scaling in the y-direction.
    * @return This.
    */
  def scale(sx: Double, sy: Double): Transformation2D = setScale(scale.x * sx, scale.y * sy)

  /**
    * Sets the absolute translation.
    * __Use with care when this [[comp.Transformation2D]] object is used by a [[comp.Component]].
    * '''I am not going to watch the use of this function, so I won't fix bugs that arise out of the use of it.'''__
    * @param x The x position to set the translation to.
    * @param y The y position to set the translation to.
    * @return This.
    */
  def setTranslation(x: Double, y: Double) = {

    val old = translation
    setTranslationWithoutSideEffect(x, y)
    val firedEvent = TranslationChange(old, translation)
    if(firedEvent.delta == Vector2.zero) {
      onTranslated(firedEvent)
    }

    this
  }

  private[comp] def setTranslationWithoutSideEffect(x: Double, y: Double) = {
    _translationMatrix = AffineTransform.getTranslateInstance(x, y)
  }

  /**
   * Sets the absolute rotation.
   * @param angle The new angle to set the transformation's rotation to.
   * @return This.
   */
  def setRotation(angle: Double) = {
    val old = _rotation
    setRotationWithoutSideEffect(angle)
    val firedEvent = RotationChange(old, _rotation)
    if(firedEvent.delta != 0) {
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
   * Sets the absolute scale of this transformation.
   * @param sx The scale in x-direction.
   * @param sy The scale in y-direction.
   * @return This.
   */
  def setScale(sx: Double, sy: Double) = {
    val old = scale
    setScaleWithoutSideEffect(sx, sy)
    val firedEvent = ScaleChange(old, scale)
    // TODO Delta check?
    onScaled(ScaleChange(old, scale))
    this
  }

  private[comp] def setScaleWithoutSideEffect(sx: Double, sy: Double) = {
    _scaleMatrix = AffineTransform.getScaleInstance(sx, sy)
  }

  def translation = Vector2(_translationMatrix.getTranslateX.asInstanceOf[Float], _translationMatrix.getTranslateY.asInstanceOf[Float])
  def rotation = _rotation
  def scale = Vector2(_scaleMatrix.getScaleX.asInstanceOf[Float], _scaleMatrix.getScaleY.asInstanceOf[Float])

  def translationMatrix = new AffineTransform(_translationMatrix)
  def rotationMatrix = new AffineTransform(_rotationMatrix)
  def scaleMatrix = new AffineTransform(_scaleMatrix)

  def concatenatedMatrix: AffineTransform = {
    // Get a fresh copy of every matrix. I don't want to modify the original matrices.
    val t = translationMatrix
    val r = rotationMatrix
    r.concatenate(scaleMatrix)
    t.concatenate(r)
    t
  }

  def transformOriginal(s: Shape) = concatenatedMatrix.createTransformedShape(s)

}

object Transformation2D {
  def applySilently(c: Component, t: Transformation2D): Component = {
    val translation = t.translation.toPoint
    c.getTransformation.setTranslationWithoutSideEffect(translation.x, translation.y)
    c.getTransformation.setRotationWithoutSideEffect(t.rotation)
    c.getTransformation.setScaleWithoutSideEffect(t.scale.x.asInstanceOf[Int], t.scale.y.asInstanceOf[Int])
    c
  }
}

trait TransformationEvent {
  def matrix: AffineTransform
  def deltaMatrix: AffineTransform
}

case class RotationChange(oldDegree: Double, newDegree: Double) extends TransformationEvent {
  val delta = newDegree - oldDegree
  override lazy val matrix = AffineTransform.getRotateInstance(newDegree)
  override lazy val deltaMatrix = AffineTransform.getRotateInstance(delta)
}

case class TranslationChange(oldTranslation: Vector2, newTranslation: Vector2) extends TransformationEvent {
  val delta = newTranslation - oldTranslation
  override lazy val matrix = AffineTransform.getTranslateInstance(newTranslation.x, newTranslation.y)
  override lazy val deltaMatrix = AffineTransform.getTranslateInstance(delta.x, delta.y)
}

case class ScaleChange(oldScale: Vector2, newScale: Vector2) extends TransformationEvent {
  override lazy val matrix = AffineTransform.getScaleInstance(newScale.x, newScale.y)
  override def deltaMatrix = throw new NotImplementedError("Scale delta not implemented yet")
}
